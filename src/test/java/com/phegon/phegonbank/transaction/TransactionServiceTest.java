package com.phegon.phegonbank.transaction;

import com.phegon.phegonbank.account.entity.Account;
import com.phegon.phegonbank.account.repo.AccountRepo;
import com.phegon.phegonbank.enums.*;
import com.phegon.phegonbank.exceptions.InsufficientBalanceException;
import com.phegon.phegonbank.transaction.dtos.TransactionRequest;
import com.phegon.phegonbank.transaction.repo.TransactionRepo;
import com.phegon.phegonbank.transaction.services.TransactionServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.modelmapper.ModelMapper;
import java.math.BigDecimal;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import com.phegon.phegonbank.auth_users.entity.User;

class TransactionServiceTest {
 @Mock AccountRepo accounts; @Mock TransactionRepo transactions; @InjectMocks TransactionServiceImpl service;
 TransactionServiceTest(){MockitoAnnotations.openMocks(this);}
 @Test void withdrawalFailsWhenBalanceIsInsufficient(){
  User user=User.builder().id(1L).email("test@example.com").build();
  Account a=Account.builder().id(1L).accountNumber("123456789012345").balance(new BigDecimal("10")).status(AccountStatus.ACTIVE).user(user).build();
  SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(com.phegon.phegonbank.security.AuthUser.builder().user(user).build(),null));
  when(accounts.findByAccountNumberForUpdate(a.getAccountNumber())).thenReturn(Optional.of(a));
  TransactionRequest r=new TransactionRequest();r.setAccountNumber(a.getAccountNumber());r.setAmount(new BigDecimal("11"));r.setTransactionType(TransactionType.WITHDRAWAL);
  assertThrows(InsufficientBalanceException.class,()->service.execute(r));
  verify(transactions,never()).save(any());
  SecurityContextHolder.clearContext();
 }
}
