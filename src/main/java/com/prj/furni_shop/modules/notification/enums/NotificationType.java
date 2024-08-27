package com.prj.furni_shop.modules.notification.enums;

import com.prj.furni_shop.exception.AppException;
import com.prj.furni_shop.exception.ErrorCode;

public enum NotificationType {
    CREATE_ORDER,
    CONFIRM_ORDER,
    DELIVERING_ORDER,
    COMPLETED_ORDER;

    public static NotificationType fromValue(int value) {
        if(value < 0 || value >= values().length) {
            throw new AppException(ErrorCode.INVALID_INPUT_DATA);
        }
        return values()[value];
    }
}
