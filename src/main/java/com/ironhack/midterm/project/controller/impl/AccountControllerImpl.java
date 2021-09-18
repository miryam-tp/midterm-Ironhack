package com.ironhack.midterm.project.controller.impl;

import com.ironhack.midterm.project.controller.dto.AccountDTO;
import com.ironhack.midterm.project.controller.dto.BalanceDTO;
import com.ironhack.midterm.project.controller.interfaces.AccountController;
import com.ironhack.midterm.project.model.account.Account;
import com.ironhack.midterm.project.repository.AccountRepository;
import com.ironhack.midterm.project.service.interfaces.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
public class AccountControllerImpl implements AccountController {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountService accountService;

    public List<Account> getAllByUser(String userName) {
        return null;
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

    public void receiveMoney(String hashedKey, Long id, String secretKey, BalanceDTO balanceDto) {

    }

    public void transferMoneyThirdParty(String hashedKey, Long id, String secretKey, BalanceDTO balanceDto) {

    }

    public void transferMoney(String userName, Long id, Long receivingAccountId, String AccountOwnerName, BalanceDTO balanceDto) {

    }
}
