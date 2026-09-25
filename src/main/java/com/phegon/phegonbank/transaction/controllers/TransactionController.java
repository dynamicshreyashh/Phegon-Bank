package com.phegon.phegonbank.transaction.controllers;
import com.phegon.phegonbank.res.Response;
import com.phegon.phegonbank.transaction.dtos.*;
import com.phegon.phegonbank.transaction.services.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/transactions") @RequiredArgsConstructor
public class TransactionController {
 private final TransactionService service;
 @PostMapping public Response<TransactionDTO> execute(@Valid @RequestBody TransactionRequest request){return service.execute(request);}
 @GetMapping("/{accountNumber}") public Response<List<TransactionDTO>> history(@PathVariable String accountNumber,@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="20") int size){return service.history(accountNumber,page,size);}
}
