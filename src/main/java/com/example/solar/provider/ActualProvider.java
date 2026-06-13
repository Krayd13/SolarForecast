package com.example.solar.provider;

import com.example.solar.SourceNames;
import com.example.solar.model.ForecastData;
import com.example.solar.model.Station;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class ActualProvider implements ForecastProvider{
    @Override
    public SourceNames getSourceName() {
        return SourceNames.ACTUAL;
    }

    @Override
    public List<ForecastData> fetch(Station station) {
        List<ForecastData> testData = new ArrayList<>();
        // Беремо початок сьогоднішнього дня (00:00)
        LocalDateTime todayStart = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);

        for (int hour = 0; hour < 24; hour++) {
            LocalDateTime timestamp = todayStart.withHour(hour);

            // Чиста синусоїда: пік генерації вдень, вночі (до 6 ранку і після 18) - по нулях
            double baseValue = 5.0 * Math.sin(Math.PI * hour / 24.0);
            if (hour < 5 || hour > 19) {
                baseValue = 0.0;
            }

            testData.add(ForecastData.builder()
                    .station(station)
                    .sourceName(getSourceName())
                    .timestamp(timestamp)
                    .value(baseValue) // Ідеальний факт без шуму
                    .build());
        }
        return testData;
    }
}
