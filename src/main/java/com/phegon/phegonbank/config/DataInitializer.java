package com.phegon.phegonbank.config;

import com.phegon.phegonbank.account.entity.Account;
import com.phegon.phegonbank.account.repo.AccountRepo;
import com.phegon.phegonbank.auth_users.entity.User;
import com.phegon.phegonbank.auth_users.repo.UserRepo;
import com.phegon.phegonbank.enums.*;
import com.phegon.phegonbank.role.entity.Role;
import com.phegon.phegonbank.role.repo.RoleRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.List;

@Component @RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
 private final RoleRepo roleRepo; private final UserRepo userRepo; private final AccountRepo accountRepo; private final PasswordEncoder encoder;
 private final SecureRandom random=new SecureRandom();
 @Value("${ADMIN_EMAIL:}") private String adminEmail;
 @Value("${ADMIN_PASSWORD:}") private String adminPassword;

 @Override public void run(String... args){
  Role customer=create("ROLE_CUSTOMER"); create("ROLE_AUDITOR"); Role admin=create("ROLE_ADMIN");
  if(adminEmail!=null&&!adminEmail.isBlank()&&adminPassword!=null&&!adminPassword.isBlank()){
   User user=userRepo.findByEmail(adminEmail.toLowerCase()).orElseGet(()->userRepo.save(User.builder().firstName("System").lastName("Admin").email(adminEmail.toLowerCase()).password(encoder.encode(adminPassword)).active(true).roles(List.of(admin)).build()));
   if(user.getRoles()==null||user.getRoles().stream().noneMatch(r->r.getName().equals("ROLE_ADMIN"))){user.setRoles(List.of(admin));userRepo.save(user);}
   if(user.getAccounts()==null||user.getAccounts().isEmpty()) accountRepo.save(Account.builder().accountNumber(unique()).balance(BigDecimal.ZERO).accountType(AccountType.CURRENT).currency(Currency.USD).status(AccountStatus.ACTIVE).user(user).build());
  }
 }
 private Role create(String name){return roleRepo.findByName(name).orElseGet(()->roleRepo.save(Role.builder().name(name).build()));}
 private String unique(){String n;do{n=String.valueOf(100000000000000L+random.nextLong(900000000000000L));}while(accountRepo.findByAccountNumber(n).isPresent());return n;}
}
