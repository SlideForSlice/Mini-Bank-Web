package com.java.bank.services;

import com.java.bank.models.BankAccount;
import com.java.bank.models.Credit;
import com.java.bank.repositories.CardRepository;
import com.java.bank.repositories.CreditRepository;
import com.java.bank.models.enums.CreditStatus;
import com.java.bank.utils.NumberGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@Transactional(readOnly = true)
public class CreditService {

    private final CreditRepository creditRepository;
    private final CardRepository cardRepository;

    public CreditService(CreditRepository creditRepository, CardRepository cardRepository) {
        this.creditRepository = creditRepository;
        this.cardRepository = cardRepository;
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
    public void  createCredit(BankAccount bankAccount, int amount) {

        Credit credit = new Credit();

        String creditNumber;
        do {
            creditNumber = NumberGenerator.generateNumber();
        } while (creditRepository.findByCreditNumber(creditNumber).isPresent());

        creditRepository.findById(credit.getId()).get().setCreditNumber(creditNumber);
        creditRepository.findById(credit.getId()).get().setBankAccount(bankAccount);
        creditRepository.findById(credit.getId()).get().setLoanDebt(amount);
        creditRepository.findById(credit.getId()).get().setCreditStatus(CreditStatus.ACTIVE);

        log.info("Create Credit");

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
//        проверка, достаточно ли баланса?
        if (currentCardBalance >= amount) {
            creditRepository.findById(id).get().setLoanDebt(currentCreditBalance - amount);
            creditRepository.save(creditRepository.findById(id).get());

            cardRepository.findByCardNumber(cardNumber).get().setBalance(currentCardBalance - amount);
            cardRepository.save(cardRepository.findByCardNumber(cardNumber).get());
        } else {
            log.info("insufficient balance");
//            TODO
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
        if (currentCreditBalance >= amount) {
            creditRepository.findById(id).get().setLoanDebt(currentCreditBalance - amount);
            creditRepository.save(creditRepository.findById(id).get());

            cardRepository.findByCardNumber(cardNumber).get().setBalance(currentCardBalance + amount);
            cardRepository.save(cardRepository.findByCardNumber(cardNumber).get());
        } else {
            log.info("insufficient credit balance");
//            TODO
        }
    }

    @Transactional
    public void addInterestToCredit(int id, float interest) {
        log.info("payInterestToDeposit" + id + ", interest" + interest);
//        получили баланс
        float currentDepositBalance = creditRepository.findById(id).get().getLoanDebt();
//        рассчет ежедневной ставки
        float dailyInterest = interest / 30;
//      выплачиваем процент
//        TODO
    }

}
