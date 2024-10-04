package com.java.bank.utils;
import org.springframework.stereotype.Component;

import java.util.Random;


@Component
public class NumberGenerator {

    public static String generateCardNumber() {
        Random random = new Random();
        StringBuilder cardNumber = new StringBuilder("4");

        for (int i = 0; i < 14; i++) {
            cardNumber.append(random.nextInt(10));
        }

        int checksum = calculateLuhnChecksum(cardNumber.toString());
        cardNumber.append(checksum);

        return cardNumber.toString();
    }

    public static String generateCreditNumber() {
        Random random = new Random();
        StringBuilder cardNumber = new StringBuilder("RUBCRED");

        // Генерируем первые 17 цифр
        for (int i = 0; i < 17; i++) {
            cardNumber.append(random.nextInt(10)); // добавляем цифры от 0 до 9
        }

        return cardNumber.toString();
    }

    public static String generateDepositNumber() {
        Random random = new Random();
        StringBuilder cardNumber = new StringBuilder("RUBDEP");

        // Генерируем первые 17 цифр
        for (int i = 0; i < 18; i++) {
            cardNumber.append(random.nextInt(10)); // добавляем цифры от 0 до 9
        }

        return cardNumber.toString();
    }

    public static int calculateLuhnChecksum(String cardNumber) {
        int sum = 0;
        boolean alternate = false;

        // Проходим по всем цифрам справа налево
        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int n = Character.getNumericValue(cardNumber.charAt(i));

            if (alternate) {
                n *= 2;
                if (n > 9) {
                    n -= 9; // вычитаем 9, если число больше 9
                }
            }
            sum += n;
            alternate = !alternate; // меняем флаг
        }

        return (10 - (sum % 10)) % 10; // вычисляем контрольную цифру
    }

}
