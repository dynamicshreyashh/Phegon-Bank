package com.phegon.phegonbank.account.services;
import com.phegon.phegonbank.account.dtos.AccountDTO;
import com.phegon.phegonbank.account.entity.Account;
import com.phegon.phegonbank.account.repo.AccountRepo;
import com.phegon.phegonbank.auth_users.entity.User;
import com.phegon.phegonbank.auth_users.repo.UserRepo;
import com.phegon.phegonbank.enums.*;
import com.phegon.phegonbank.exceptions.BadRequestException;
import com.phegon.phegonbank.exceptions.NotFoundException;
import com.phegon.phegonbank.security.AuthUser;
import com.phegon.phegonbank.res.Response;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.List;

@Service @RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
 private final AccountRepo repo; private final UserRepo userRepo; private final ModelMapper mapper; private final SecureRandom random=new SecureRandom();
 @Override @Transactional public Response<AccountDTO> createAccount(String type,String currency){
  User u=current(); Account a=Account.builder().accountNumber(uniqueNumber()).balance(BigDecimal.ZERO)
   .accountType(AccountType.valueOf(type.toUpperCase())).currency(Currency.valueOf(currency.toUpperCase())).status(AccountStatus.ACTIVE).user(u).build();
  repo.save(a); return ok(a,"Account created");
 }
 @Override public Response<AccountDTO> getAccount(String n){Account a=find(n);ensureOwner(a);return ok(a,"Account fetched");}
 @Override public Response<List<AccountDTO>> myAccounts(){return Response.<List<AccountDTO>>builder().statusCode(200).message("Accounts fetched").data(repo.findByUserId(current().getId()).stream().map(a->mapper.map(a,AccountDTO.class)).toList()).build();}
 @Override @Transactional public Response<AccountDTO> updateStatus(String n,String status){Account a=find(n);ensureOwner(a);AccountStatus s=AccountStatus.valueOf(status.toUpperCase());if(s==AccountStatus.CLOSED)a.setClosedAt(java.time.LocalDateTime.now());a.setStatus(s);repo.save(a);return ok(a,"Account status updated");}
 private Account find(String n){return repo.findByAccountNumber(n).orElseThrow(()->new NotFoundException("Account not found"));}
 private void ensureOwner(Account a){if(!a.getUser().getId().equals(current().getId()))throw new BadRequestException("You do not own this account");}
 private User current(){if(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof AuthUser au)return userRepo.findById(au.getUser().getId()).orElseThrow();throw new BadRequestException("Authentication required");}
 private String uniqueNumber(){String n;do{n=String.valueOf(100000000000000L+Math.abs(random.nextLong())%900000000000000L);}while(repo.findByAccountNumber(n).isPresent());return n.substring(0,15);}
 private Response<AccountDTO> ok(Account a,String m){return Response.<AccountDTO>builder().statusCode(200).message(m).data(mapper.map(a,AccountDTO.class)).build();}
}
