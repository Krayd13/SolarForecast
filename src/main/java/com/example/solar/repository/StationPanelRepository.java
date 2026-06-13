package com.example.solar.repository;

import com.example.solar.model.StationPanel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StationPanelRepository extends JpaRepository<StationPanel, Long> {
    List<StationPanel> findAllByStationId(Long stationId);

    void deleteByStationId(Long stationId);
}
