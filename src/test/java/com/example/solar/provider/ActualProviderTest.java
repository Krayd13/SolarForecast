package com.example.solar.provider;

import com.example.solar.SourceNames;
import com.example.solar.model.ForecastData;
import com.example.solar.model.Station;
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

import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@SpringBootTest(properties = {
        "solar.provider.foxess.url=https://api.foxesscloud.com/op/v0/device/history/query",
        "solar.provider.foxess.path=/op/v0/device/history/query"
})
class ActualProviderTest {

    @Autowired
    private ActualProvider actualProvider;

    @Autowired
    private RestTemplate restTemplate;

    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    void shouldSuccessfullyFetchAndAverageFoxEssData() {
        Station station = new Station();
        station.setId(1L);
        station.setApiToken("test-token-123");
        station.setDeviceSn("device-sn-xyz");

        String mockJsonResponse = """
        {
          "errno": 0,
          "result": [
            {
              "deviceSN": "device-sn-xyz",
              "datas": [
                {
                  "variable": "pvPower",
                  "data": [
                    { "value": 1.5 },
                    { "value": 4.5 }
                  ]
                }
              ]
            }
          ]
        }
        """;

        mockServer.expect(requestTo("https://api.foxesscloud.com/op/v0/device/history/query"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("token", "test-token-123"))
                .andRespond(withSuccess(mockJsonResponse, MediaType.APPLICATION_JSON));

        List<ForecastData> result = actualProvider.fetch(station);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(3.0, result.get(0).getValue(), 0.001);
        Assertions.assertEquals(SourceNames.ACTUAL, result.get(0).getSourceName());

        mockServer.verify();
    }

    @Test
    void shouldReturnEmptyListWhenCredentialsAreMissing() {
        Station station = new Station();
        station.setId(1L);
        station.setApiToken(null);

        List<ForecastData> result = actualProvider.fetch(station);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());
    }
}
