package com.java.bank.services;

import com.java.bank.models.BankAccount;
import com.java.bank.models.Credit;
import com.java.bank.repositories.BankAccountRepository;
import com.java.bank.repositories.CardRepository;
import com.java.bank.repositories.CreditRepository;
import com.java.bank.models.enums.CreditStatus;
import com.java.bank.utils.NumberGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@Transactional(readOnly = true)
public class CreditService {

    private final CreditRepository creditRepository;
    private final CardRepository cardRepository;
    private final BankAccountRepository bankAccountRepository;

    public CreditService(CreditRepository creditRepository, CardRepository cardRepository, BankAccountRepository bankAccountRepository) {
        this.creditRepository = creditRepository;
        this.cardRepository = cardRepository;
        this.bankAccountRepository = bankAccountRepository;
    }

    public List<Credit> getAllCredits() {
        log.info("getAllCredits");
        return creditRepository.findAll();
    }


    public Optional<Credit> getCreditById(int id) {
        log.info("getCreditById" + id);
        return creditRepository.findById(id);
    }

    @Transactional
    public void  createCredit(int idBankAccount, int creditTerm) {

        Credit credit = new Credit();

        String creditNumber;
        do {
            creditNumber = NumberGenerator.generateCardNumber();
        } while (creditRepository.findByCreditNumber(creditNumber).isPresent());

        credit.setCreditNumber(creditNumber);
        credit.setBankAccount(bankAccountRepository.findById(idBankAccount).get());
        credit.setLoanDebt(0);
        credit.setInterestRate(14);
        credit.setOpenDate(LocalDate.now());
        credit.setEndDate(LocalDate.now().plusMonths(creditTerm));
        credit.setCreditStatus(CreditStatus.ACTIVE);
        log.info("Credit created");
        creditRepository.save(credit);
    }


    @Transactional
    public void deleteCredit(int id) {
        log.info("deleteCredit" + id);
        creditRepository.deleteById(id);
    }

    // поиск всех депозитов по банк аккаунту
    public List<Credit> getCreditsByBankAccount(BankAccount bankAccount) {
        log.info("getCreditsByBankAccount" + bankAccount);
        return creditRepository.findByBankAccount(bankAccount);
    }

    @Transactional
    public void cashInToCreditFromCard(int id, float amount, String cardNumber) {
        log.info("cashInToCreditFromCard" + id + "from card" + cardNumber + "amount" + amount);
//        получили баланс депозита
        float currentCreditBalance = creditRepository.findById(id).get().getLoanDebt();
//        нужно получить баланс карты
        float currentCardBalance = cardRepository.findByCardNumber(cardNumber).get().getBalance();
//        если сумма кредита равна 0, то кредит считается погашенным
        if (currentCreditBalance >= 0 ) {
            //        проверка, достаточно ли баланса?
            if (currentCardBalance >= amount) {
                creditRepository.findById(id).get().setLoanDebt(currentCreditBalance - amount);
                creditRepository.save(creditRepository.findById(id).get());

                cardRepository.findByCardNumber(cardNumber).get().setBalance(currentCardBalance - amount);
                cardRepository.save(cardRepository.findByCardNumber(cardNumber).get());
            } else {
                log.info("insufficient balance");

            }
        } else {
            return;
        }


    }

    @Transactional
    public void cashOutFromCreditToCard(int id, float amount, String cardNumber) {
        log.info("cashOutFromCreditToCard" + cardNumber + ", from credit" + id + ", amount" + amount);
//        получили баланс депозита
        float currentCreditBalance = creditRepository.findById(id).get().getLoanDebt();
//        нужно получить баланс карты
        float currentCardBalance = cardRepository.findByCardNumber(cardNumber).get().getBalance();
//        проверка, достаточно ли баланса?

            creditRepository.findById(id).get().setLoanDebt(currentCreditBalance + amount);
            creditRepository.save(creditRepository.findById(id).get());
            cardRepository.findByCardNumber(cardNumber).get().setBalance(currentCardBalance + amount);
            cardRepository.save(cardRepository.findByCardNumber(cardNumber).get());

    }

    @Transactional
    public void accrueInterest() {
        log.info("accrueInterest");
        LocalDate today = LocalDate.now();
        List<Credit> activeCredits = creditRepository.findByEndDateAfter(today);

        for (Credit credit : activeCredits) {
            float interest = calculateInterest(credit);
            credit.setLoanDebt(credit.getLoanDebt() + interest);
            creditRepository.save(credit);
        }
    }

    private float calculateInterest(Credit credit) {
        float  loanDebt = credit.getLoanDebt();
        float interestRate = credit.getInterestRate();
        LocalDate startDate = credit.getOpenDate();
        LocalDate endDate = credit.getEndDate();

        long days = startDate.until(endDate).getDays();
        float dailyInterestRate = interestRate / 365;
        float interest = loanDebt * dailyInterestRate * days;

        return interest;
    }
}
