package com.phegon.phegonbank.auth_users.controllers;

import com.phegon.phegonbank.auth_users.dtos.*;
import com.phegon.phegonbank.auth_users.services.AuthService;
import com.phegon.phegonbank.res.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    @PostMapping("/register") public Response<LoginResponse> register(@Valid @RequestBody RegistrationRequest r){return authService.register(r);}
    @PostMapping("/login") public Response<LoginResponse> login(@Valid @RequestBody LoginRequest r){return authService.login(r);}
    @GetMapping("/me") public Response<UserDTO> me(){return authService.getCurrentUser();}
    @PutMapping("/password") public Response<?> password(@Valid @RequestBody UpdatePasswordRequest r){return authService.updatePassword(r);}
    @PostMapping("/forgot-password") public Response<?> forgot(@RequestParam String email){return authService.forgotPassword(email);}
    @PostMapping("/reset-password") public Response<?> reset(@Valid @RequestBody ResetPasswordRequest r){return authService.resetPassword(r);}
}
