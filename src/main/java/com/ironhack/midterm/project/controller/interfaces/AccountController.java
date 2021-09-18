package com.ironhack.midterm.project.controller.interfaces;

import com.ironhack.midterm.project.controller.dto.AccountDTO;
import com.ironhack.midterm.project.controller.dto.BalanceDTO;
import com.ironhack.midterm.project.controller.dto.TransferDTO;
import com.ironhack.midterm.project.model.account.Account;

import java.util.List;
import java.util.Optional;

public interface AccountController {
    Account store(AccountDTO accountDto);
    void updateBalance(Long id, BalanceDTO balanceDto);
    void receiveOrTransferMoney(Optional<String> optionalHashedKey, TransferDTO transferDto);
    void transferMoney(TransferDTO transferDTO);
}
