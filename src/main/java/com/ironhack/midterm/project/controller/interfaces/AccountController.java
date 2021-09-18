package com.ironhack.midterm.project.controller.interfaces;

import com.ironhack.midterm.project.controller.dto.AccountDTO;
import com.ironhack.midterm.project.controller.dto.BalanceDTO;
import com.ironhack.midterm.project.model.account.Account;

import java.util.List;

public interface AccountController {
    List<Account> getAllByUser(String userName);
    Account store(AccountDTO accountDto);
    void updateBalance(Long id, BalanceDTO balanceDto);
    void receiveMoney(String hashedKey, Long id, String secretKey, BalanceDTO balanceDto);
    void transferMoneyThirdParty(String hashedKey, Long id, String secretKey, BalanceDTO balanceDto);
    void transferMoney(String userName, Long id, Long receivingAccountId, String AccountOwnerName, BalanceDTO balanceDto);
}
