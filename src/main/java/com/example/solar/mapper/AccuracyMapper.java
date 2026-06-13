package com.example.solar.mapper;

import com.example.solar.dto.DailyAccuracyDto;
import com.example.solar.model.DailyAccuracy;
import org.springframework.stereotype.Component;

@Component
public class AccuracyMapper {
    public static DailyAccuracyDto toDto(DailyAccuracy accuracy){
        return new DailyAccuracyDto(accuracy.getStationId(), accuracy.getSourceName(), accuracy.getDate(), accuracy.getMape(), accuracy.getRmse());
    }
}
