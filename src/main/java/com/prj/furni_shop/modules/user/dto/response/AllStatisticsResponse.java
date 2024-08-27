package com.prj.furni_shop.modules.user.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AllStatisticsResponse {
    StatisticsDto userStatistics;
    StatisticsDto orderStatistics;
    StatisticsDto totalMoneyStatistics;
    StatisticsDto soldCountStatistics;
}
