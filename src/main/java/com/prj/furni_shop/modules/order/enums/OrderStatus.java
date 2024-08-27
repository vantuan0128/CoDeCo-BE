package com.prj.furni_shop.modules.order.enums;

import com.prj.furni_shop.exception.AppException;
import com.prj.furni_shop.exception.ErrorCode;

public enum OrderStatus {
    CANCELLED,
    PENDING,
    CONFIRMED,
    DELIVERING,
    COMPLETED;

    public static OrderStatus fromValue(int value) {
        if(value < 0 || value >= values().length) {
            throw new AppException(ErrorCode.INVALID_INPUT_DATA);
        }
        return values()[value];
    }
}
