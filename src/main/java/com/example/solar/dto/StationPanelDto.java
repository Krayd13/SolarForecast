package com.example.solar.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record StationPanelDto(
        @NotNull(message = "Станція є обов'язкова для прив'язки панелі")
        Long stationId,
        @NotNull(message = "Азимут є обов'язковим полем")
        @Min(value = -180, message = "Азимут не може бути меншим за -180 градусів (Північ)")
        @Max(value = 180, message = "Азимут не може бути більшим за 180 градусів (Північ)")
        Integer azimuth,
        @NotNull(message = "Кут нахилу є обов'язковим полем")
        @Min(value = 0, message = "Нахил не може бути меншим за 0 градусів")
        @Max(value = 90, message = "Нахил не може бути більшим за 90 градусів")
        Integer tilt,
        @NotNull(message = "Потужність панелі є обов'язковим полем")
        @Positive(message = "Потужність панелі повинна бути більшою за нуль")
        Integer capacity) {
}
