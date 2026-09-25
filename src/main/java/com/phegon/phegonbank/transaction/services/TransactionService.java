package com.phegon.phegonbank.transaction.services;
import com.phegon.phegonbank.transaction.dtos.TransactionDTO;
import com.phegon.phegonbank.transaction.dtos.TransactionRequest;
import com.phegon.phegonbank.res.Response;
import java.util.List;
public interface TransactionService {
 Response<TransactionDTO> execute(TransactionRequest request);
 Response<List<TransactionDTO>> history(String accountNumber,int page,int size);
}
