package com.example.solar.provider;

import com.example.solar.model.ForecastData;
import com.example.solar.model.Station;
import com.example.solar.model.StationPanel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@SpringBootTest(properties = {
        "solar.provider.open-meteo.url=https://api.open-meteo.com/v1/forecast",
        "solar.provider.open-meteo.efficiency=0.85",
        "solar.provider.open-meteo.default-capacity=5.0"
})
class OpenMeteoProviderTest {

    @Autowired
    private OpenMeteoProvider openMeteoProvider;

    @Autowired
    private RestTemplate restTemplate;

    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    void shouldCalculatePowerCorrectlyBasedOnRadiation() {
        Station station = new Station();
        station.setId(10L);
        station.setLatitude(49.84);
        station.setLongitude(24.02);

        StationPanel panel = new StationPanel();
        panel.setCapacity(4000);
        station.setPanels(List.of(panel));

        String mockJsonResponse = """
                {
                  "hourly": {
                    "time": ["2026-06-29T12:00"],
                    "shortwave_radiation": [500.0]
                  }
                }
                """;

        mockServer.expect(requestTo("https://api.open-meteo.com/v1/forecast?latitude=49.84&longitude=24.02&hourly=shortwave_radiation&forecast_days=1"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(mockJsonResponse, MediaType.APPLICATION_JSON));

        List<ForecastData> result = openMeteoProvider.fetch(station);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(1.7, result.get(0).getValue(), 0.001);
        mockServer.verify();
    }

    @Test
    void shouldFallbackToFiveCapacityWhenStationHasNoPanels() {
        Station station = new Station();
        station.setPanels(List.of());

        String mockJsonResponse = """
                {
                  "hourly": {
                    "time": ["2026-06-29T12:00"],
                    "shortwave_radiation": [1000.0]
                  }
                }
                """;

        mockServer.expect(requestTo("https://api.open-meteo.com/v1/forecast?latitude=null&longitude=null&hourly=shortwave_radiation&forecast_days=1"))
                .andRespond(withSuccess(mockJsonResponse, MediaType.APPLICATION_JSON));

        List<ForecastData> result = openMeteoProvider.fetch(station);

        Assertions.assertEquals(4.25, result.get(0).getValue(), 0.001);
    }
}
