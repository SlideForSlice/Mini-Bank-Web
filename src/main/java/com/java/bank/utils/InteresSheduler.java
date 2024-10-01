package com.java.bank.utils;

import com.java.bank.services.CreditService;
import com.java.bank.services.DepositService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class InteresSheduler {
    private final DepositService depositService;
    private final CreditService creditService;

    @Autowired
    public InteresSheduler(DepositService depositService, CreditService creditService) {
        this.depositService = depositService;
        this.creditService = creditService;
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void accrueInterestDayli(){
        creditService.accrueInterest();

    }

}
