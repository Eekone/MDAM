package com.zgkxzx.modbus4And.sero.messaging;

import java.io.IOException;

public class TimeoutException extends IOException {
    private static final long serialVersionUID = 1L;

    public TimeoutException(String message) {
        super(message);
    }
}
