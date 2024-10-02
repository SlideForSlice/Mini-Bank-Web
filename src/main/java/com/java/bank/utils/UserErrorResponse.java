package com.java.bank.utils;

import com.java.bank.models.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

@Getter
@Setter
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class UserErrorResponse {
    private String errorMessage;
    private long timestamp;

}
