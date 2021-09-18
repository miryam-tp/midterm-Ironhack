package com.ironhack.midterm.project.service.interfaces;

import com.ironhack.midterm.project.controller.dto.AccountDTO;
import com.ironhack.midterm.project.controller.dto.BalanceDTO;
import com.ironhack.midterm.project.controller.dto.TransferDTO;
import com.ironhack.midterm.project.model.account.Account;

import java.util.Optional;

public interface AccountService {
    public BalanceDTO getBalance(Long id);
    Account store(AccountDTO accountDto);
    void updateBalance(Long id, BalanceDTO balanceDto);
    void receiveOrTransferMoney(Optional<String> optionalHashedKey, TransferDTO transferDto);
}
