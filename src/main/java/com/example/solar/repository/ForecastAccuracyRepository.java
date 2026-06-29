package com.example.solar.repository;

import com.example.solar.dto.DailyAccuracyDto;
import com.example.solar.model.DailyAccuracy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ForecastAccuracyRepository extends JpaRepository<DailyAccuracy, Long> {
    @Query("""
                SELECT new com.example.solar.dto.DailyAccuracyDto(
                    d.stationId, null, null, AVG(d.mape), AVG(d.rmse)
                )
                FROM DailyAccuracy d
                GROUP BY d.stationId
                ORDER BY AVG(d.mape) ASC
            """)
    List<DailyAccuracyDto> getStationLeaderboard();

    @Query("""
                SELECT new com.example.solar.dto.DailyAccuracyDto(
                    null, d.sourceName, null, AVG(d.mape), AVG(d.rmse)
                )
                FROM DailyAccuracy d
                GROUP BY d.sourceName
                ORDER BY AVG(d.mape) ASC
            """)
    List<DailyAccuracyDto> getSourceLeaderboard();

    @Query("""
            SELECT new com.example.solar.dto.DailyAccuracyDto(
                d.stationId, d.sourceName, null, AVG(d.mape), AVG(d.rmse)
            )
            FROM DailyAccuracy d
            WHERE d.stationId = :stationId
            GROUP BY d.sourceName, d.stationId
            """)
    List<DailyAccuracyDto> getAverageSourcesAccuracyByStationId(@Param("stationId") Long stationId);

    List<DailyAccuracy> findAllByStationId(Long stationId);

    List<DailyAccuracy> findAllByStationIdAndDate(Long stationId, LocalDate date);

    @Query("SELECT AVG(d.mape) FROM DailyAccuracy d")
    Double getGlobalAverageMape();
}
