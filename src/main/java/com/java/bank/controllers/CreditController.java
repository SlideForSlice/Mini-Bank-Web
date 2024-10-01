package com.java.bank.controllers;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.java.bank.controllers.DTO.BankAccountIdDTO;
import com.java.bank.controllers.DTO.CardTransDTO;
import com.java.bank.models.BankAccount;
import com.java.bank.models.Credit;
import com.java.bank.repositories.CardRepository;
import com.java.bank.services.CardService;
import com.java.bank.services.CreditService;
import com.java.bank.utils.CreditErrorResponse;
import com.java.bank.utils.CreditPaidException;
import com.java.bank.utils.MapperForDTO;
import com.java.bank.utils.UserErrorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping("/credit-service")
@RestController
@RequiredArgsConstructor
public class CreditController {

    private final CreditService creditService;
    private final MapperForDTO mapperForDTO;
    private final CardService cardService;
    private final CardRepository cardRepository;

    @GetMapping()
    public String getAllCredits(BankAccount idBankAccount) {
        creditService.getCreditsByBankAccount(idBankAccount);
        return "";
    }

    @PostMapping("/create")
    public Map<String, String> createCredit(@RequestBody BankAccountIdDTO idBankAccount,
                            @RequestParam int creditTerm) {
//        creditTerm = срок кредита в месяцах
        int id = idBankAccount.getId();
        creditService.createCredit(id, creditTerm);
        return Map.of("status", "success");
    }

    @DeleteMapping("/{id}/delete")
    public void deleteCredit(@PathVariable int id) {
        creditService.deleteCredit(id);
    }

    @PatchMapping("/{id}/cash-in")
    public Map<String, Float> cashIn(@PathVariable int id,
                       @RequestParam float amount,
                       @RequestBody CardTransDTO cardTransDTO) {
        String cardNumber = mapperForDTO.convertToCard(cardTransDTO).getCardNumber();
        float cardBalance = cardRepository.findByCardNumber(cardNumber).get().getBalance();
        float loanDebt = creditService.getCreditById(id).get().getLoanDebt();
        if ((loanDebt > 0 && cardBalance >= amount && amount <= loanDebt) || amount == loanDebt) {
            creditService.cashInToCreditFromCard(id, amount, cardNumber);
            float newCardBalance = cardRepository.findByCardNumber(cardNumber).get().getBalance();
            float newLoanDebt = creditService.getCreditById(id).get().getLoanDebt();
            return Map.of("Credit balance", newLoanDebt,
                    "Card balance", newCardBalance);
        }
        else {
            throw new CreditPaidException("Credit paid error");
        }
    }

    @PatchMapping("/{id}/cash-out")
    public Map<String, Float> cashOut(@PathVariable int id,
                                      @RequestParam float amount,
                                      @RequestBody CardTransDTO cardTransDTO) {

        String cardNumber = mapperForDTO.convertToCard(cardTransDTO).getCardNumber();
        creditService.cashOutFromCreditToCard(id, amount, cardNumber);
        float cardBalance = cardRepository.findByCardNumber(cardNumber).get().getBalance();
        float loanDebt = creditService.getCreditById(id).get().getLoanDebt();
        return Map.of("Credit balance", loanDebt,
                "Card balance", cardBalance);
    }

    @ExceptionHandler
    private ResponseEntity<CreditErrorResponse> handleException(CreditPaidException e) {
        CreditErrorResponse response = new CreditErrorResponse(
                "Credit already paid!",
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
