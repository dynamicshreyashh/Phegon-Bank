package com.phegon.phegonbank.auth_users.services;

import com.phegon.phegonbank.account.entity.Account;
import com.phegon.phegonbank.auth_users.dtos.*;
import com.phegon.phegonbank.auth_users.entity.PasswordResetCode;
import com.phegon.phegonbank.auth_users.entity.User;
import com.phegon.phegonbank.auth_users.repo.PasswordResetCodeRepo;
import com.phegon.phegonbank.auth_users.repo.UserRepo;
import com.phegon.phegonbank.enums.AccountStatus;
import com.phegon.phegonbank.enums.AccountType;
import com.phegon.phegonbank.enums.Currency;
import com.phegon.phegonbank.exceptions.BadRequestException;
import com.phegon.phegonbank.exceptions.NotFoundException;
import com.phegon.phegonbank.notification.dtos.NotificationDTO;
import com.phegon.phegonbank.notification.services.NotificationService;
import com.phegon.phegonbank.role.entity.Role;
import com.phegon.phegonbank.role.repo.RoleRepo;
import com.phegon.phegonbank.security.AuthUser;
import com.phegon.phegonbank.security.TokenService;
import com.phegon.phegonbank.res.Response;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepo userRepo;
    private final RoleRepo roleRepo;
    private final PasswordResetCodeRepo resetRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final ModelMapper modelMapper;
    private final NotificationService notificationService;
    private final SecureRandom random = new SecureRandom();

    @Override @Transactional
    public Response<LoginResponse> register(RegistrationRequest request) {
        String email=request.getEmail().trim().toLowerCase();
        if(userRepo.findByEmail(email).isPresent()) throw new BadRequestException("Email is already registered");
        List<String> requested=request.getRoles();
        List<Role> roles;
        if(requested==null || requested.isEmpty()) {
            roles=List.of(getOrCreateRole("ROLE_CUSTOMER"));
        } else {
            roles=requested.stream().map(r->getOrCreateRole(normalizeRole(r))).toList();
        }
        User user=User.builder().firstName(request.getFirstName().trim()).lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber()).email(email).password(passwordEncoder.encode(request.getPassword()))
                .active(true).roles(roles).build();
        Account account=Account.builder().accountNumber(generateAccountNumber()).balance(BigDecimal.ZERO)
                .accountType(AccountType.SAVINGS).currency(Currency.USD).status(AccountStatus.ACTIVE).user(user).build();
        user.setAccounts(List.of(account));
        userRepo.save(user);
        String token=tokenService.generateToken(user.getEmail());
        return Response.<LoginResponse>builder().statusCode(201).message("Registration successful")
                .data(LoginResponse.builder().token(token).roles(roles.stream().map(Role::getName).toList()).build()).build();
    }

    @Override
    public Response<LoginResponse> login(LoginRequest request) {
        String email=request.getEmail().trim().toLowerCase();
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email,request.getPassword()));
        User user=userRepo.findByEmail(email).orElseThrow(()->new NotFoundException("User not found"));
        if(!user.isActive()) throw new BadRequestException("User account is inactive");
        String token=tokenService.generateToken(email);
        return Response.<LoginResponse>builder().statusCode(200).message("Login successful")
                .data(LoginResponse.builder().token(token).roles(user.getRoles().stream().map(Role::getName).toList()).build()).build();
    }

    @Override public Response<UserDTO> getCurrentUser() {
        User user=currentUser();
        return Response.<UserDTO>builder().statusCode(200).message("User fetched")
                .data(modelMapper.map(user,UserDTO.class)).build();
    }

    @Override @Transactional
    public Response<?> updatePassword(UpdatePasswordRequest request) {
        User user=currentUser();
        if(!passwordEncoder.matches(request.getOldPassword(),user.getPassword())) throw new BadRequestException("Old password is incorrect");
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepo.save(user);
        return Response.builder().statusCode(200).message("Password updated successfully").build();
    }

    @Override @Transactional
    public Response<?> forgotPassword(String email) {
        User user=userRepo.findByEmail(email.trim().toLowerCase()).orElseThrow(()->new NotFoundException("No account found for this email"));
        resetRepo.deleteByUserId(user.getId());
        String code=String.format("%06d",random.nextInt(1_000_000));
        resetRepo.save(PasswordResetCode.builder().Code(code).user(user).expiryDate(LocalDateTime.now().plusMinutes(10)).used(false).build());
        notificationService.sendEmail(NotificationDTO.builder().recipient(user.getEmail()).subject("Password Reset Code")
                .body("Your password reset code is "+code+". It expires in 10 minutes.").build(),user);
        return Response.builder().statusCode(200).message("Password reset code sent").build();
    }

    @Override @Transactional
    public Response<?> resetPassword(ResetPasswordRequest request) {
        PasswordResetCode reset=resetRepo.findByCode(request.getCode()).orElseThrow(()->new BadRequestException("Invalid reset code"));
        if(reset.isUsed() || reset.getExpiryDate().isBefore(LocalDateTime.now()) || !reset.getUser().getEmail().equalsIgnoreCase(request.getEmail()))
            throw new BadRequestException("Reset code is invalid or expired");
        User user=reset.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepo.save(user);
        reset.setUsed(true); resetRepo.save(reset);
        return Response.builder().statusCode(200).message("Password reset successfully").build();
    }

    private User currentUser() {
        if(!(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof AuthUser authUser))
            throw new BadRequestException("Authenticated user not available");
        return authUser.getUser();
    }
    private Role getOrCreateRole(String name){ return roleRepo.findByName(name).orElseGet(()->roleRepo.save(Role.builder().name(name).build())); }
    private String normalizeRole(String role){ String value=role.trim().toUpperCase(); return value.startsWith("ROLE_")?value:"ROLE_"+value; }
    private String generateAccountNumber(){
        String number;
        do { number=String.valueOf(100000000000000L + Math.abs(random.nextLong())%900000000000000L); }
        while(false);
        return number.substring(0,15);
    }
}
