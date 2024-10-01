package com.java.bank.utils;

import com.java.bank.models.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class UserErrorResponse {
    private String errorMessage;
    private long timestamp;

    public UserErrorResponse(String errorMessage, long timestamp) {
        this.errorMessage = errorMessage;
        this.timestamp = timestamp;
    }

}
