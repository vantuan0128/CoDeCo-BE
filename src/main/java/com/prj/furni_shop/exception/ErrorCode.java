package com.prj.furni_shop.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    INVALID_KEY(1001, "Uncategorized error", HttpStatus.BAD_REQUEST),
    INVALID_INPUT_DATA(1002, "Invalid input data", HttpStatus.BAD_REQUEST),
    INVALID_TOKEN(1003,"Token invalid", HttpStatus.UNAUTHORIZED),
    FILE_TOO_LARGE(1004, "The file size exceeds the maximum limit", HttpStatus.BAD_REQUEST),
    INVALID_ORDER_STATUS(1005, "Invalid order status for this operation", HttpStatus.BAD_REQUEST),
    VOUCHER_CONDITION_NOT_MET(1006, "Voucher conditions not met", HttpStatus.BAD_REQUEST),
    INVALID_CAPTCHA(1007, "Invalid captcha", HttpStatus.BAD_REQUEST),
    INVALID_OTP(1008, "Invalid key", HttpStatus.BAD_REQUEST),

    EXISTED(1101, "Existed", HttpStatus.BAD_REQUEST),
    NOT_EXISTED(1102, "Not existed", HttpStatus.BAD_REQUEST),
    CATEGORY_HAS_SUBCATEGORIES(1103, "Category has sub categories", HttpStatus.BAD_REQUEST),
    PRODUCT_HAS_PRODUCTDETAILS(1104, "Product has product details", HttpStatus.BAD_REQUEST),

    UNAUTHENTICATED(1201, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1202, "You do not have permission", HttpStatus.FORBIDDEN),
    UNVERIFIED_ACCOUNT(1203, "Your account is not verified", HttpStatus.BAD_REQUEST),
    FORBIDDEN_ACCOUNT(1204, "Your account has been banned", HttpStatus.BAD_REQUEST),

    FAIL_TO_SEND_OTP(1301, "Unable to send OTP, please try again", HttpStatus.INTERNAL_SERVER_ERROR),

    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),

    INSUFFICIENT_STOCK(1401, "INSUFFICIENT STOCK", HttpStatus.BAD_REQUEST);

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private final int code;
    private final String message;
    private HttpStatusCode statusCode;
}
