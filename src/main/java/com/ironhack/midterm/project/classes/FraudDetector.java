package com.ironhack.midterm.project.classes;

import com.ironhack.midterm.project.enums.Status;
import com.ironhack.midterm.project.model.account.Account;
import com.ironhack.midterm.project.model.account.CheckingAccount;
import com.ironhack.midterm.project.model.account.Savings;
import com.ironhack.midterm.project.model.account.StudentChecking;
import com.ironhack.midterm.project.model.users.ThirdParty;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embeddable;
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
    private BigDecimal maxDailyAmount;
    private BigDecimal currentDayTransactions;

    //region constructors, getters and setters
    public FraudDetector() {
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

//    public Money getLastTransactionAmount() {
//        return lastTransactionAmount;
//    }
//
//    public void setLastTransactionAmount(Money lastTransactionAmount) {
//        this.lastTransactionAmount = lastTransactionAmount;
//    }

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

    public static void checkThirdPartyTransaction(Account account, ThirdParty thirdParty) {
        FraudDetector fraudDetector = account.getFraudDetector();

        //Check if the last transaction was less than a second ago
        if(fraudDetector.lastTransactionTime != null) {
            if(fraudDetector.lastTransactionLessThanOneSecondAgo()) {
                if(account instanceof Savings)
                    ((Savings) account).setStatus(Status.FROZEN);
                else if(account instanceof CheckingAccount)
                    ((Savings)account).setStatus(Status.FROZEN);
                else if(account instanceof StudentChecking)
                    ((StudentChecking)account).setStatus(Status.FROZEN);
                throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Too many requests were sent. Target account will be frozen");
            }
        }

        //Check if transactions made in the last 24 hours total more than 150% of the highest daily total transactions in any other 24h period

    }

    public static void checkAccountHolderTransaction(Account originAccount, Account targetAccount) {

    }

    public boolean lastTransactionLessThanOneSecondAgo() {
        //Example of two timestamps in the same second with same second value (nanoseconds do not matter)
        //  00:00:01:001    and    00:00:01:053
        //Example of two timestamps with different second value (nanoseconds are smaller in the current timestamp than in the last transaction timestamp)
        //  00:00:01:005     and    00:00:02:003

        //Check date is the same
        if(this.lastTransactionTime.toLocalDate().isEqual(LocalDate.now())) {
            //If second is the same, last transaction was less than one second ago
            if(this.lastTransactionTime.toLocalTime().getSecond() == LocalTime.now().getSecond())
                return true;
            //If second is smaller than current transaction second, we have to check nanoseconds
            else if(this.lastTransactionTime.toLocalTime().getSecond() < LocalTime.now().getSecond()) {
                //If nanoseconds in last transaction are bigger than current transaction nanos, the last transaction was less than one second ago
                if(this.lastTransactionTime.toLocalTime().getNano() > LocalTime.now().getNano())
                    return true;
                else return false;
            } else return false;
        } else return false;
    }

}
