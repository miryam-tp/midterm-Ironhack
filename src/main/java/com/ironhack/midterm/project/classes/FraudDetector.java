package com.ironhack.midterm.project.classes;

import com.ironhack.midterm.project.enums.Status;
import com.ironhack.midterm.project.model.account.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Embeddable
public class FraudDetector {
    private LocalDateTime lastTransactionTime;
    @AttributeOverrides({
            @AttributeOverride(name = "currency", column = @Column(name = "last_currency")),
            @AttributeOverride(name = "amount", column = @Column(name = "last_amount"))
    })
    private Money lastTransactionAmount;
    @NotNull
    private BigDecimal maxDailyAmount;
    @NotNull
    private BigDecimal currentDayTransactions;

    //region constructors, getters and setters
    public FraudDetector() {
    }

    public FraudDetector(BigDecimal maxDailyAmount, BigDecimal currentDayTransactions) {
        this.maxDailyAmount = maxDailyAmount;
        this.currentDayTransactions = currentDayTransactions;
    }

    public FraudDetector(LocalDateTime lastTransactionTime, Money lastTransactionAmount, BigDecimal maxDailyAmount, BigDecimal currentDayTransactions) {
        this.lastTransactionTime = lastTransactionTime;
        this.lastTransactionAmount = lastTransactionAmount;
        this.maxDailyAmount = maxDailyAmount;
        this.currentDayTransactions = currentDayTransactions;
    }

    public LocalDateTime getLastTransactionTime() {
        return lastTransactionTime;
    }

    public void setLastTransactionTime(LocalDateTime lastTransactionTime) {
        this.lastTransactionTime = lastTransactionTime;
    }

    public Money getLastTransactionAmount() {
        return lastTransactionAmount;
    }

    public void setLastTransactionAmount(Money lastTransactionAmount) {
        this.lastTransactionAmount = lastTransactionAmount;
    }

    public BigDecimal getMaxDailyAmount() {
        return maxDailyAmount;
    }

    public void setMaxDailyAmount(BigDecimal maxDailyAmount) {
        this.maxDailyAmount = maxDailyAmount;
    }

    public BigDecimal getCurrentDayTransactions() {
        return currentDayTransactions;
    }

    public void setCurrentDayTransactions(BigDecimal currentDayTransactions) {
        this.currentDayTransactions = currentDayTransactions;
    }


    //endregion

    /**
     * Method to validate transaction parameters and check if there is fraud danger
     * @param account Account
     */
    public static void checkTransaction(Account account, BigDecimal transactionAmount) {
        FraudDetector fraudDetector = account.getFraudDetector();

        //Check if the last transaction was less than a second ago
        if(fraudDetector.lastTransactionTime != null) {
            if(fraudDetector.isLastTransactionLessThanOneSecondAgo()) {
                freezeAccount(account);
                throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Too many requests were sent in the span of a second. The account will be frozen");
            }
        }

        //Check if transactions made in the last 24 hours total more than 150% of the highest daily total transactions in any other 24h period
        if(fraudDetector.isMoreThanHighestDailyTransactions(transactionAmount)) {
            freezeAccount(account);
            throw new ResponseStatusException(HttpStatus.LOCKED, "This transaction is possibly fraudulent and the account will be frozen");
        }
    }

    /**
     * Method to determine if the last transaction set in the account's FraudDetector happened less than one second before the current one
     * @return true if the last transaction was made less than a second ago, false if it was made more than a second ago or if the last transaction date is null
     */
    public boolean isLastTransactionLessThanOneSecondAgo() {
        //Example of two timestamps in the same second with same second value (nanoseconds do not matter)
        //  00:00:01:001    and    00:00:01:053
        //Example of two timestamps with different second value (nanoseconds are smaller in the current timestamp than in the last transaction timestamp)
        //  00:00:01:005     and    00:00:02:003

        //Check date is not null
        if(this.lastTransactionTime != null) {
            //Check date is the same
            if(this.lastTransactionTime.toLocalDate().isEqual(LocalDate.now())) {
                //Check hour is the same
                if(this.lastTransactionTime.toLocalTime().getHour() == LocalTime.now().getHour()) {
                    //If second is the same, last transaction was less than one second ago
                    if(this.lastTransactionTime.toLocalTime().getSecond() == LocalTime.now().getSecond())
                        return true;
                        //If second is smaller than current transaction second, we have to check nanoseconds
                    else if(LocalTime.now().getSecond() - this.lastTransactionTime.toLocalTime().getSecond() <= 1) {
                        //If nanoseconds in last transaction are bigger than current transaction nanos, the last transaction was less than one second ago
                        if(this.lastTransactionTime.toLocalTime().getNano() > LocalTime.now().getNano())
                            return true;
                        else return false;
                    } else return false;
                } else return false;
            } else return false;
        }
        else return false;
    }

    /**
     * Method to determine if the transaction amount that is passed to the method is higher than 150% the maximum daily amount specified in the account's FraudDetector.
     * The method will check if maxDailyAmount is not zero, which would mean the account was created recently, as well as if another transaction was made in the same day.
     * If both of these conditions are true, it will validate if the transaction amount is bigger than the maximum daily amount and flag the transaction as possibly
     * fraudulent in case it is.
     * @param amount Amount
     * @return true if the amount is bigger than maximum daily amount multiplied by 1.5
     */
    public boolean isMoreThanHighestDailyTransactions(BigDecimal amount) {
        if(this.maxDailyAmount.compareTo(new BigDecimal("0")) == 0)  //If this is true, then the account is new and will not validate the amount
            return false;
        else if(this.lastTransactionTime.toLocalDate().isEqual(LocalDate.now().minusDays(1))) {  //Check if a transaction was made in the last 24 hours
            if(this.lastTransactionTime.isBefore(LocalDateTime.now().minusHours(24)))
                return false;
        }
        else if(!this.lastTransactionTime.toLocalDate().isEqual(LocalDate.now()))
            return false;

        if(amount.compareTo(this.maxDailyAmount.multiply(new BigDecimal("1.5"))) > 0)  //Compare the transaction amount
            return true;
        else return false;
    }

    /**
     * Method to freeze an account.
     * If the account is an instance of Credit Card it does nothing, since Credit Card does not have a Status
     * @param account Account
     */
    private static void freezeAccount(Account account) {
        if(account instanceof Savings)
            ((Savings) account).setStatus(Status.FROZEN);
        else if(account instanceof CheckingAccount)
            ((CheckingAccount)account).setStatus(Status.FROZEN);
        else if(account instanceof StudentChecking)
            ((StudentChecking)account).setStatus(Status.FROZEN);
    }
}
