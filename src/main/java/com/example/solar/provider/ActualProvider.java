package com.example.solar.provider;

import com.example.solar.SourceNames;
import com.example.solar.model.ForecastData;
import com.example.solar.model.Station;
import com.example.solar.model.StationPanel;
import com.example.solar.provider.dto.ActualDto;
import com.example.solar.provider.dto.FoxEssRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Component
public class ActualProvider implements ForecastProvider {
    private static final Logger log = LoggerFactory.getLogger(ActualProvider.class);
    private final RestTemplate restTemplate;
    private final String apiUrl;
    private final String apiPath;

    public ActualProvider(
            RestTemplate restTemplate,
            @Value("${solar.provider.foxess.url}") String apiUrl,
            @Value("${solar.provider.foxess.path}") String apiPath) {
        this.restTemplate = restTemplate;
        this.apiUrl = apiUrl;
        this.apiPath = apiPath;
    }

    @Override
    public SourceNames getSourceName() {
        return SourceNames.ACTUAL;
    }

    @Override
    public List<ForecastData> fetch(Station station) {
        String apiToken = station.getApiToken();
        String deviceSn = station.getDeviceSn();

        if (apiToken == null || deviceSn == null) {
            log.warn("API credentials are missing for station ID: {}", station.getId());
            return new ArrayList<>();
        }

        long nowMillis = Instant.now().toEpochMilli();
        long oneHourAgoMillis = Instant.now().minus(1, ChronoUnit.HOURS).toEpochMilli();

        String signature = createSignature(apiToken, nowMillis);

        HttpHeaders headers = createHeaders(nowMillis, apiToken, signature);
        FoxEssRequest requestBody = new FoxEssRequest(deviceSn, List.of("pvPower"), oneHourAgoMillis, nowMillis);
        HttpEntity<FoxEssRequest> request = new HttpEntity<>(requestBody, headers);

        try {
            ActualDto response = restTemplate.postForObject(buildUrl(station, null), request, ActualDto.class);

            if (isValidResponse(response)) {
                List<ActualDto.Result.VariablesData.Data> points = response.result().get(0).datas().get(0).data();
                log.info("Points received from Fox Ess {}", points.size());

                return List.of(mapToForecast(points, station, oneHourAgoMillis));
            }
            log.warn("Response cant be null or empty: \nResponse: {}\nResponse result: {}", response, response.result());
        } catch (Exception e) {
            log.error("Error with request to Fox Ess: {}", e.getMessage());
        }

        return new ArrayList<>();
    }

    @Override
    public String buildUrl(Station station, StationPanel panel) {
        return apiUrl;
    }

    private String createSignature(String apiToken, long timestamp) {
        String signatureSource = apiPath + "\r\n" + apiToken + "\r\n" + timestamp;
        return DigestUtils.md5DigestAsHex(signatureSource.getBytes(StandardCharsets.UTF_8));
    }

    private HttpHeaders createHeaders(long timestamp, String apiToken, String signature) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("lang", "en");
        headers.set("timestamp", String.valueOf(timestamp));
        headers.set("token", apiToken);
        headers.set("signature", signature);

        return headers;
    }

    private boolean isValidResponse(ActualDto response) {
        return response != null
                && response.result() != null
                && !response.result().isEmpty()
                && response.result().get(0).datas() != null
                && !response.result().get(0).datas().isEmpty();
    }

    private ForecastData mapToForecast(List<ActualDto.Result.VariablesData.Data> points, Station station, long timestampMillis) {
        double averagePowerByHour = points.stream().mapToDouble(p -> p.value()).average().orElse(0.0);
        ForecastData actualData = ForecastData.builder()
                .station(station)
                .sourceName(getSourceName())
                .value(averagePowerByHour)
                .timestamp(LocalDateTime.ofInstant(Instant.ofEpochMilli(timestampMillis), ZoneId.systemDefault()))
                .build();

        return actualData;
    }
}
