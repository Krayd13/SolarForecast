package com.example.solar.provider;

import com.example.solar.SourceNames;
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
        "solar.provider.forecast-solar.url=https://api.forecast.solar/estimate"
})
class ForecastSolarProviderTest {

    @Autowired
    private ForecastSolarProvider forecastSolarProvider;

    @Autowired
    private RestTemplate restTemplate;

    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    void shouldFetchAndAggregateMultiplePanelsCorrectly() {
        Station station = new Station();
        station.setId(5L);
        station.setLatitude(50.45);
        station.setLongitude(30.52);

        StationPanel panel1 = new StationPanel();
        panel1.setTilt(35);
        panel1.setAzimuth(0);
        panel1.setCapacity(3000);

        StationPanel panel2 = new StationPanel();
        panel2.setTilt(20);
        panel2.setAzimuth(-90);
        panel2.setCapacity(2000);

        station.setPanels(List.of(panel1, panel2));

        String jsonPanel1 = "{ \"result\": { \"watt_hours_period\": { \"2026-06-29 12:00:00\": 1000.0 } } }";
        String jsonPanel2 = "{ \"result\": { \"watt_hours_period\": { \"2026-06-29 12:00:00\": 500.0 } } }";

        mockServer.expect(requestTo("https://api.forecast.solar/estimate/50.45/30.52/35/0/3.0"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(jsonPanel1, MediaType.APPLICATION_JSON));

        mockServer.expect(requestTo("https://api.forecast.solar/estimate/50.45/30.52/20/-90/2.0"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(jsonPanel2, MediaType.APPLICATION_JSON));

        List<ForecastData> result = forecastSolarProvider.fetch(station);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(1.5, result.get(0).getValue(), 0.001);
        Assertions.assertEquals(SourceNames.FORECAST_SOLAR, result.get(0).getSourceName());

        mockServer.verify();
    }
}
