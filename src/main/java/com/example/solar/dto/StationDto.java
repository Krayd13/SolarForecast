package com.example.solar.dto;

import jakarta.validation.constraints.*;

import java.util.List;

public record StationDto(
                        @NotBlank(message = "Назва станції не може бути порожньою або складатися з пробілів")
                        @Size(max = 60, message = "Назва не повинна бути довшою за 60 символів")
                        String name,
                        @NotNull(message = "Широта є обов'язковим полем")
                        @Min(value = -90, message = "Широта не може бути меншою за -90")
                        @Max(value = 90, message = "Широта не може бути більшою за 90")
                        Double latitude,
                        @NotNull(message = "Довгота є обов'язковим полем")
                        @Min(value = -180, message = "Довгота не може бути меншою за -180")
                        @Max(value = 180, message = "Довгота не може бути більшою за 180")
                        Double longitude,
                        @NotNull(message = "Токен є обов'язковим полем")
                        @NotBlank(message = "Токен не може бути порожнім або складатися з пробілів")
                        String apiToken,
                        @NotNull(message = "Серійний номер є обов'язковим полем")
                        @NotBlank(message = "Серійний номер не може бути порожнім або складатися з пробілів")
                        String deviceSn,
                        List<StationPanelDto> panels) {
}
