package com.phegon.phegonbank.account.controllers;
import com.phegon.phegonbank.account.dtos.AccountDTO;
import com.phegon.phegonbank.account.services.AccountService;
import com.phegon.phegonbank.res.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/accounts") @RequiredArgsConstructor
public class AccountController {
 private final AccountService service;
 @PostMapping public Response<AccountDTO> create(@RequestParam(defaultValue="SAVINGS") String accountType,@RequestParam(defaultValue="USD") String currency){return service.createAccount(accountType,currency);}
 @GetMapping public Response<List<AccountDTO>> mine(){return service.myAccounts();}
 @GetMapping("/{accountNumber}") public Response<AccountDTO> get(@PathVariable String accountNumber){return service.getAccount(accountNumber);}
 @PatchMapping("/{accountNumber}/status") public Response<AccountDTO> status(@PathVariable String accountNumber,@RequestParam String status){return service.updateStatus(accountNumber,status);}
}
