package com.minjer.smartchill.service;

import com.minjer.smartchill.entity.result.Result;

import java.time.LocalDate;

public interface StatisticsService {
    Result getSellStatisticsByDate(LocalDate begin, LocalDate end);
}
