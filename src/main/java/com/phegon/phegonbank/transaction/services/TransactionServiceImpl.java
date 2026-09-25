package com.phegon.phegonbank.transaction.services;

import com.phegon.phegonbank.account.entity.Account;
import com.phegon.phegonbank.account.repo.AccountRepo;
import com.phegon.phegonbank.enums.*;
import com.phegon.phegonbank.exceptions.*;
import com.phegon.phegonbank.security.AuthUser;
import com.phegon.phegonbank.transaction.dtos.*;
import com.phegon.phegonbank.transaction.entity.Transaction;
import com.phegon.phegonbank.transaction.repo.TransactionRepo;
import com.phegon.phegonbank.res.Response;
import com.phegon.phegonbank.notification.dtos.NotificationDTO;
import com.phegon.phegonbank.notification.services.NotificationService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Service @RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
 private final AccountRepo accounts; private final TransactionRepo transactions; private final ModelMapper mapper; private final NotificationService notificationService;
 @Override @Transactional public Response<TransactionDTO> execute(TransactionRequest r){
  if(r.getAmount()==null||r.getAmount().compareTo(BigDecimal.ZERO)<=0)throw new InvalidTransactionException("Amount must be greater than zero");
  if(r.getTransactionType()==null)throw new InvalidTransactionException("Transaction type is required");
  Account source=accountForUpdate(r.getAccountNumber()); owner(source); active(source);
  if(r.getTransactionType()==TransactionType.DEPOSIT){source.setBalance(source.getBalance().add(r.getAmount()));Response<TransactionDTO> result=save(source,r,source.getAccountNumber(),null);notify(source,"Deposit successful","A deposit of "+r.getAmount()+" "+source.getCurrency()+" was credited to account "+source.getAccountNumber()+".");return result;}
  if(r.getTransactionType()==TransactionType.WITHDRAWAL){debit(source,r.getAmount());Response<TransactionDTO> result=save(source,r,source.getAccountNumber(),null);notify(source,"Withdrawal successful","A withdrawal of "+r.getAmount()+" "+source.getCurrency()+" was debited from account "+source.getAccountNumber()+".");return result;}
  if(r.getDestinationAccountNumber()==null||r.getDestinationAccountNumber().isBlank())throw new InvalidTransactionException("Destination account is required for transfer");
  if(source.getAccountNumber().equals(r.getDestinationAccountNumber()))throw new InvalidTransactionException("Source and destination accounts must differ");
  Account dest=accountForUpdate(r.getDestinationAccountNumber());active(dest);debit(source,r.getAmount());
  dest.setBalance(dest.getBalance().add(r.getAmount()));accounts.save(dest);
  Response<TransactionDTO> result=save(source,r,source.getAccountNumber(),dest.getAccountNumber());
  Transaction credit=Transaction.builder().amount(r.getAmount()).transactionType(TransactionType.TRANSFER).transactionDate(java.time.LocalDateTime.now())
    .description(r.getDescription()==null?"Transfer received":r.getDescription()).status(TransactionStatus.SUCCESS).account(dest)
    .sourceAccount(source.getAccountNumber()).destinationAccount(dest.getAccountNumber()).build();
  transactions.save(credit); notify(source,"Transfer successful","A transfer of "+r.getAmount()+" "+source.getCurrency()+" was sent from "+source.getAccountNumber()+" to "+dest.getAccountNumber()+"."); notify(dest,"Transfer received","A transfer of "+r.getAmount()+" "+dest.getCurrency()+" was received from "+source.getAccountNumber()+"."); return result;
 }
 @Override public Response<List<TransactionDTO>> history(String n,int page,int size){
  Account a=account(n);owner(a);var p=transactions.findByAccount_AccountNumber(n,PageRequest.of(Math.max(page,0),Math.min(Math.max(size,1),100)));
  return Response.<List<TransactionDTO>>builder().statusCode(200).message("Transactions fetched")
   .data(p.getContent().stream().map(t->mapper.map(t,TransactionDTO.class)).toList())
   .meta(java.util.Map.of("page",p.getNumber(),"size",p.getSize(),"totalElements",p.getTotalElements(),"totalPages",p.getTotalPages())).build();
 }
 private Response<TransactionDTO> save(Account a,TransactionRequest r,String src,String dest){accounts.save(a);Transaction t=Transaction.builder().amount(r.getAmount()).transactionType(r.getTransactionType()).transactionDate(java.time.LocalDateTime.now()).description(r.getDescription()).status(TransactionStatus.SUCCESS).account(a).sourceAccount(src).destinationAccount(dest).build();return Response.<TransactionDTO>builder().statusCode(200).message("Transaction successful").data(mapper.map(transactions.save(t),TransactionDTO.class)).build();}
 private void debit(Account a,BigDecimal amount){if(a.getBalance().compareTo(amount)<0)throw new InsufficientBalanceException("Insufficient balance");a.setBalance(a.getBalance().subtract(amount));}
 private void notify(Account account,String subject,String body){
  if(account.getUser()!=null) notificationService.sendEmail(NotificationDTO.builder().recipient(account.getUser().getEmail()).subject(subject).body(body).build(),account.getUser());
 }
 private Account account(String n){if(n==null||n.isBlank())throw new InvalidTransactionException("Account number is required");return accounts.findByAccountNumber(n).orElseThrow(()->new NotFoundException("Account not found"));}
 private Account accountForUpdate(String n){if(n==null||n.isBlank())throw new InvalidTransactionException("Account number is required");return accounts.findByAccountNumberForUpdate(n).orElseThrow(()->new NotFoundException("Account not found"));}
 private void active(Account a){if(a.getStatus()!=AccountStatus.ACTIVE)throw new InvalidTransactionException("Account is not active");}
 private void owner(Account a){if(!(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof AuthUser au)||!a.getUser().getId().equals(au.getUser().getId()))throw new BadRequestException("You do not own this account");}
}
