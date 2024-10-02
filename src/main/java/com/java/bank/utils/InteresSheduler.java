package com.java.bank.utils;

import com.java.bank.services.CreditService;
import com.java.bank.services.DepositService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class InteresSheduler {
    private final DepositService depositService;
    private final CreditService creditService;

    @Scheduled(cron = "0 0 0 * * ?")
    public void accrueInterestDayli(){
        creditService.accrueInterest();

    }

}
