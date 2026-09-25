package com.phegon.phegonbank.auditor.controllers;

import com.phegon.phegonbank.account.entity.Account;
import com.phegon.phegonbank.account.dtos.AccountDTO;
import com.phegon.phegonbank.account.repo.AccountRepo;
import com.phegon.phegonbank.auth_users.dtos.UserDTO;
import com.phegon.phegonbank.auth_users.repo.UserRepo;
import com.phegon.phegonbank.res.Response;
import com.phegon.phegonbank.transaction.dtos.TransactionDTO;
import com.phegon.phegonbank.transaction.entity.Transaction;
import com.phegon.phegonbank.transaction.repo.TransactionRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/api/auditor") @RequiredArgsConstructor @PreAuthorize("hasAnyRole('AUDITOR','ADMIN')")
public class AuditorController {
 private final TransactionRepo transactions; private final AccountRepo accounts; private final UserRepo users; private final ModelMapper mapper;
 @GetMapping("/transactions") public Response<List<TransactionDTO>> transactions(@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="50") int size){
  var p=transactions.findAll(PageRequest.of(Math.max(0,page),Math.min(Math.max(1,size),100)));
  return Response.<List<TransactionDTO>>builder().statusCode(200).message("Audit transactions fetched").data(p.getContent().stream().map(t->mapper.map(t,TransactionDTO.class)).toList()).meta(java.util.Map.of("totalElements",p.getTotalElements(),"totalPages",p.getTotalPages())).build();
 }
 @GetMapping("/accounts") public Response<List<AccountDTO>> accounts(){return Response.<List<AccountDTO>>builder().statusCode(200).message("Accounts fetched").data(accounts.findAll().stream().map(a->mapper.map(a,AccountDTO.class)).toList()).build();}
 @GetMapping("/users") public Response<List<UserDTO>> users(){return Response.<List<UserDTO>>builder().statusCode(200).message("Users fetched").data(users.findAll().stream().map(u->mapper.map(u,UserDTO.class)).toList()).build();}
}
