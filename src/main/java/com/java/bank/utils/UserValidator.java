package com.java.bank.utils;

import com.java.bank.models.User;
import com.java.bank.services.UserDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class UserValidator implements Validator {

    private final UserDetailService userDetailService;

    @Override
    public boolean supports(Class<?> clazz) {
        return User.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        User user = (User) target;
        try {
            userDetailService.loadUserByUsername(user.getUsername());
            errors.rejectValue("username", "", "Username is already exist");
        } catch (UsernameNotFoundException ignored) {
            return;
        }
    }
}
