package com.phegon.phegonbank.config;

import com.phegon.phegonbank.role.entity.Role;
import com.phegon.phegonbank.role.repo.RoleRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component @RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
 private final RoleRepo roleRepo;
 @Override public void run(String... args){
  create("ROLE_CUSTOMER"); create("ROLE_AUDITOR"); create("ROLE_ADMIN");
 }
 private void create(String name){if(roleRepo.findByName(name).isEmpty())roleRepo.save(Role.builder().name(name).build());}
}
