package com.ironhack.midterm.project.service.interfaces;

import com.ironhack.midterm.project.controller.dto.AccountDTO;
import com.ironhack.midterm.project.controller.dto.BalanceDTO;
import com.ironhack.midterm.project.model.account.Account;

public interface AccountService {
    Account store(AccountDTO accountDto);
    void updateBalance(Long id, BalanceDTO balanceDto);
}
