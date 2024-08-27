package com.prj.furni_shop.modules.user.service;

import com.prj.furni_shop.exception.AppException;
import com.prj.furni_shop.exception.ErrorCode;
import com.prj.furni_shop.modules.order.enums.OrderStatus;
import com.prj.furni_shop.modules.order.repository.OrderRepository;
import com.prj.furni_shop.modules.user.dto.response.AllStatisticsResponse;
import com.prj.furni_shop.modules.user.dto.response.StatisticsChartResponse;
import com.prj.furni_shop.modules.user.dto.response.StatisticsDto;
import com.prj.furni_shop.modules.user.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@PreAuthorize("hasRole('ADMIN')")
public class AdminDashboardService {

    UserRepository userRepository;
    OrderRepository orderRepository;

    public AllStatisticsResponse getAllStatistics() {
        LocalDateTime now = LocalDateTime.now();

        LocalDateTime today = now.toLocalDate().atStartOfDay().plusHours(23).plusMinutes(59).plusSeconds(59);
        LocalDateTime yesterday = now.minusDays(1).toLocalDate().atStartOfDay().plusHours(23).plusMinutes(59).plusSeconds(59);
        LocalDateTime lastWeek = now.minusWeeks(1).toLocalDate().atStartOfDay().plusHours(23).plusMinutes(59).plusSeconds(59);

        Long totalUsersUntilToday = userRepository.countUserUntil(today);
        Long totalUsersUntilYesterday = userRepository.countUserUntil(yesterday);
        Long userChange = totalUsersUntilToday - totalUsersUntilYesterday;

        StatisticsDto userStatistics = StatisticsDto.builder()
                .total(totalUsersUntilToday)
                .change(userChange)
                .build();

        Long totalOrdersUntilToday = orderRepository.countOrderUntil(today);
        Long totalOrdersUntilLastWeek = orderRepository.countOrderUntil(lastWeek);
        Long orderChange = totalOrdersUntilToday - totalOrdersUntilLastWeek;

        StatisticsDto orderStatistics = StatisticsDto.builder()
                .total(totalOrdersUntilToday)
                .change(orderChange)
                .build();

        Long totalMoneyUntilToday = orderRepository.totalMoneyUntil(today, OrderStatus.COMPLETED);
        Long totalMoneyUntilYesterday = orderRepository.totalMoneyUntil(yesterday, OrderStatus.COMPLETED);
        System.out.println(totalMoneyUntilToday);
        System.out.println(totalMoneyUntilYesterday);
        Long moneyChange = totalMoneyUntilToday - totalMoneyUntilYesterday;

        StatisticsDto totalMoneyStatistics = StatisticsDto.builder()
                .total(totalMoneyUntilToday)
                .change(moneyChange)
                .build();

        Long totalSoldCountUntilToday = orderRepository.totalSaleUntil(today, OrderStatus.COMPLETED);
        Long totalSoldCountUntilYesterday = orderRepository.totalSaleUntil(yesterday, OrderStatus.COMPLETED);
        Long soldCountChange = totalSoldCountUntilToday - totalSoldCountUntilYesterday;

        StatisticsDto soldCountStatistics = StatisticsDto.builder()
                .total(totalSoldCountUntilToday)
                .change(soldCountChange)
                .build();

        return AllStatisticsResponse.builder()
                .userStatistics(userStatistics)
                .orderStatistics(orderStatistics)
                .totalMoneyStatistics(totalMoneyStatistics)
                .soldCountStatistics(soldCountStatistics)
                .build();
    }

    public List<StatisticsChartResponse> getStatisticsChart(String start, String end, String type) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate startDate = (start != null) ? LocalDate.parse(start, formatter) : null;
        LocalDate endDate = (end != null) ? LocalDate.parse(end, formatter) : null;

        if (startDate.isAfter(endDate) || startDate.isAfter(LocalDate.now()) || endDate.isAfter(LocalDate.now())
                || startDate.plusDays(10).isBefore(endDate)) {
            System.out.println(1);
            throw new AppException(ErrorCode.INVALID_INPUT_DATA);
        }

        List<StatisticsChartResponse> result = new ArrayList<>();
        LocalDate currentDate = startDate;

        while (!currentDate.isAfter(endDate)) {

            LocalDateTime startOfDay = currentDate.atStartOfDay();
            LocalDateTime endOfDay = currentDate.atTime(LocalTime.MAX);

            Long total;
            if ("revenue".equals(type)) {
                total = orderRepository.totalMoneyForDate(startOfDay, endOfDay, OrderStatus.COMPLETED);
            } else if ("order".equals(type)) {
                total = orderRepository.totalOrderForDate(startOfDay, endOfDay, OrderStatus.COMPLETED);
            } else {
                System.out.println(2);
                throw new AppException(ErrorCode.INVALID_INPUT_DATA);
            }
            result.add(new StatisticsChartResponse(currentDate, total));
            currentDate = currentDate.plusDays(1);
        }

        return result;
    }

}
