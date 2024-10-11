package com.java.bank.services;

import com.java.bank.models.BankAccount;
import com.java.bank.models.Deposit;
import com.java.bank.repositories.BankAccountRepository;
import com.java.bank.repositories.CardRepository;
import com.java.bank.repositories.DepositRepository;
import com.java.bank.models.enums.DepositStatus;
import com.java.bank.utils.NumberGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class DepositService {

    private final DepositRepository depositRepository;
    private final CardRepository cardRepository;
    private final CardService cardService;
    private final BankAccountRepository bankAccountRepository;

    public List<Deposit> getAllDeposits(BankAccount bankAccount) {
        log.info("getAllDeposits");
        return depositRepository.findAll();
    }

    public Optional<Deposit> getDepositById(int id) {
        log.info("getDepositById" + id);
        return depositRepository.findById(id);
    }

    @Transactional
    public Deposit createDeposit(int idBankAccount, int depositTerm) {

        Deposit deposit = new Deposit();

        String depositNumber;
        do {
            depositNumber = NumberGenerator.generateDepositNumber();
        } while (depositRepository.findByDepositNum(depositNumber).isPresent());

        deposit.setDepositNum(depositNumber);
        deposit.setBankAccount(bankAccountRepository.findById(idBankAccount).get());
        deposit.setBalance(0);
        deposit.setInterest(10);
        deposit.setOpenDate(LocalDate.now());
        deposit.setEndDate(LocalDate.now().plusMonths(depositTerm));
        deposit.setDepositStatus(DepositStatus.ACTIVE);
        log.info("Create Deposit");

        depositRepository.save(deposit);
        return deposit;
    }


    @Transactional
    public void deleteDeposit(int id) {

        depositRepository.deleteById(id);
    }

// поиск всех депозитов по банк аккаунту
    public List<Deposit> getDepositsByBankAccount(BankAccount bankAccount) {
        return depositRepository.findByBankAccount(bankAccount);
    }

    @Transactional
    public void cashInToDepositFromCard(int id, float amount, String cardNumber) {
        log.info("cashInToDepositFromCard" + id + "from card" + cardNumber + "amount" + amount);
//        получили баланс депозита
        float currentDepositBalance = depositRepository.findById(id).get().getBalance();
//        нужно получить баланс карты
        float currentCardBalance = cardRepository.findByCardNumber(cardNumber).get().getBalance();
//        проверка, достаточно ли баланса?
        if (currentCardBalance >= amount) {
             depositRepository.findById(id).get().setBalance(currentDepositBalance + amount);
             depositRepository.save(depositRepository.findById(id).get());

             cardRepository.findByCardNumber(cardNumber).get().setBalance(currentCardBalance - amount);
             cardRepository.save(cardRepository.findByCardNumber(cardNumber).get());
        } else {
            log.info("insufficient balance");
//            TODO
        }
    }

    @Transactional
    public void cashOutToCardFromDeposit(int id, float amount, String cardNumber) {
        log.info("cashOutToCardFromDeposit" + id + ", to card" + cardNumber + ", amount" + amount);
//        получили баланс депозита
        float currentDepositBalance = depositRepository.findById(id).get().getBalance();
//        нужно получить баланс карты
        float currentCardBalance = cardRepository.findByCardNumber(cardNumber).get().getBalance();
//        проверка, достаточно ли баланса?
        if (currentDepositBalance >= amount) {
            depositRepository.findById(id).get().setBalance(currentDepositBalance - amount);
            depositRepository.save(depositRepository.findById(id).get());

            cardRepository.findByCardNumber(cardNumber).get().setBalance(currentCardBalance + amount);
            cardRepository.save(cardRepository.findByCardNumber(cardNumber).get());
        } else {
            log.info("insufficient deposit balance");
//            TODO
        }
    }

    @Transactional
    public void accrueInterest() {
        log.info("accrueInterest");
        LocalDate today = LocalDate.now();
        List<Deposit> activeDeposits = depositRepository.findByEndDateAfter(today);

        for (Deposit deposit : activeDeposits) {
            float interest = calculateInterest(deposit);
            deposit.setBalance(deposit.getBalance() + interest);
            depositRepository.save(deposit);
        }
    }

    private float calculateInterest(Deposit deposit) {
        float  balance = deposit.getBalance();
        float interestRate = deposit.getInterest();
        LocalDate startDate = deposit.getOpenDate();
        LocalDate endDate = deposit.getEndDate();

        long days = ChronoUnit.DAYS.between(startDate, endDate);;
        float dailyInterestRate = interestRate / 365;
        float interest = balance * dailyInterestRate * days;

        return interest;
    }

}
