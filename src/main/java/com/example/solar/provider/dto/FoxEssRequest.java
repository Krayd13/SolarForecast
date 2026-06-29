package com.example.solar.provider.dto;

import java.util.List;

public record FoxEssRequest(String deviceSn, List<String> variables, Long begin, Long end) {
}
