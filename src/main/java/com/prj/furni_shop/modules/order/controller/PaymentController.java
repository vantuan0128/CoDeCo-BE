package com.prj.furni_shop.modules.order.controller;

import com.prj.furni_shop.base.ApiResponse;
import com.prj.furni_shop.modules.order.dto.response.VNPayResponse;
import com.prj.furni_shop.modules.order.service.PaymentService;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentController {

    PaymentService paymentService;

    @GetMapping("/vn-pay")
    @Hidden
    public ApiResponse<VNPayResponse> pay(HttpServletRequest request) {
        return ApiResponse.<VNPayResponse>builder()
                .result(paymentService.createVnPayPayment(request))
                .build();
    }

}
