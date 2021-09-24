package com.ironhack.midterm.project.controller.impl;

import com.ironhack.midterm.project.controller.dto.AccountDTO;
import com.ironhack.midterm.project.controller.dto.BalanceDTO;
import com.ironhack.midterm.project.controller.dto.TransferDTO;
import com.ironhack.midterm.project.controller.interfaces.AccountController;
import com.ironhack.midterm.project.model.account.Account;
import com.ironhack.midterm.project.service.interfaces.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@RestController
public class AccountControllerImpl implements AccountController {

    @Autowired
    private AccountService accountService;

    @GetMapping("/accounts/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BalanceDTO getBalance(@PathVariable Long id) {
        return accountService.getBalance(id);
    }

    @PostMapping("/accounts")
    @ResponseStatus(HttpStatus.CREATED)
    public Account store(@RequestBody @Valid AccountDTO accountDto) {
        return accountService.store(accountDto);
    }

    @PutMapping("/accounts/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateBalance(@PathVariable Long id, @RequestBody @Valid BalanceDTO balanceDto) {
        accountService.updateBalance(id, balanceDto);
    }

    @PutMapping("/accounts/third-party")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void receiveOrTransferMoney(@RequestHeader("hashedKey") Optional<String> optionalHashedKey, @RequestBody @Valid TransferDTO transferDto) {
        accountService.receiveOrTransferMoney(optionalHashedKey, transferDto);
    }

    @PutMapping("/accounts/transfer")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void transferMoney(@RequestBody @Valid TransferDTO transferDto) {
        accountService.transferMoney(transferDto);
    }
}
