package com.java.bank.services;

import com.java.bank.models.BankAccount;
import com.java.bank.models.Deposit;
import com.java.bank.DAO.CardRepository;
import com.java.bank.DAO.DepositRepository;
import com.java.bank.models.enums.DepositStatus;
import com.java.bank.utils.NumberGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@Slf4j
public class DepositService {

    private final DepositRepository depositRepository;
    private final CardRepository cardRepository;
    private final CardService cardService;

    @Autowired
    public DepositService(DepositRepository depositRepository, CardRepository cardRepository, CardService cardService) {
        this.depositRepository = depositRepository;
        this.cardRepository = cardRepository;
        this.cardService = cardService;
    }

    public List<Deposit> getAllDeposits(BankAccount bankAccount) {
        log.info("getAllDeposits");
        return depositRepository.findAll();
    }

    public Optional<Deposit> getDepositById(int id) {
        log.info("getDepositById" + id);
        return depositRepository.findById(id);
    }

    @Transactional
    public void createDeposit(BankAccount bankAccount) {

        Deposit deposit = new Deposit();

        String depositNumber;
        do {
            depositNumber = NumberGenerator.generateNumber();
        } while (depositRepository.findByDepositNum(depositNumber).isEmpty());

        depositRepository.findById(deposit.getId()).get().setDepositNum(depositNumber);
        depositRepository.findById(deposit.getId()).get().setBankAccount(bankAccount);
        depositRepository.findById(deposit.getId()).get().setBalance(0);
        depositRepository.findById(deposit.getId()).get().setStatus(DepositStatus.ACTIVE);

        log.info("Create Deposit");

        depositRepository.save(deposit);
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

//    начисление процентов
    @Transactional
    public void payInterestToDeposit(int id, float interest) {
        log.info("payInterestToDeposit" + id + ", interest" + interest);
//        получили баланс
        float currentDepositBalance = depositRepository.findById(id).get().getBalance();
//        рассчет ежедневной ставки
        float dailyInterest = interest / 30;
//      выплачиваем процент
        currentDepositBalance = currentDepositBalance + (currentDepositBalance * dailyInterest);
        depositRepository.findById(id).get().setBalance(currentDepositBalance);
        depositRepository.save(depositRepository.findById(id).get());
    }

}
