package com.phegon.phegonbank.account.services;
import com.phegon.phegonbank.account.dtos.AccountDTO;
import com.phegon.phegonbank.res.Response;
import java.util.List;
public interface AccountService {
 Response<AccountDTO> createAccount(String accountType,String currency);
 Response<AccountDTO> getAccount(String accountNumber);
 Response<List<AccountDTO>> myAccounts();
 Response<AccountDTO> updateStatus(String accountNumber,String status);
}
