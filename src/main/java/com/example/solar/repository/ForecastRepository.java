package com.example.solar.repository;

import com.example.solar.SourceNames;
import com.example.solar.model.ForecastData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ForecastRepository extends JpaRepository<ForecastData, Long> {
    List<ForecastData> findAllByStationIdAndSourceNameAndTimestampBetween(Long stationId,
                                                                         SourceNames sourceName,
                                                                         LocalDateTime from,
                                                                         LocalDateTime to);

    void deleteByStationId(Long stationId);
    @Query("SELECT DISTINCT f.sourceName FROM ForecastData f " +
            "WHERE f.station.id = :stationId " +
            "AND f.timestamp BETWEEN :start AND :end")
    List<String> findDistinctSourceNamesByStationIdAndTimestampBetween(@Param("stationId") Long stationId,
                                                                       @Param("start") LocalDateTime start,
                                                                       @Param("end") LocalDateTime end);

    List<ForecastData> findAllByStationIdAndTimestampBetween(Long stationId, LocalDateTime start, LocalDateTime end);
}
