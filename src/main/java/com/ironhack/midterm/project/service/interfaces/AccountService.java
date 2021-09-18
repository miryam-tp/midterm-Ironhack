package com.ironhack.midterm.project.service.interfaces;

import com.ironhack.midterm.project.controller.dto.AccountDTO;
import com.ironhack.midterm.project.controller.dto.BalanceDTO;
import com.ironhack.midterm.project.controller.dto.TransferDTO;
import com.ironhack.midterm.project.model.account.Account;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Optional;

public interface AccountService {
    Account store(AccountDTO accountDto);
    void updateBalance(Long id, BalanceDTO balanceDto);
    void receiveOrTransferMoney(@RequestHeader("hashedKey") Optional<String> optionalHashedKey, TransferDTO transferDto);
}
