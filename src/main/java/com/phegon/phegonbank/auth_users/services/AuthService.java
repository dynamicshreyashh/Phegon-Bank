package com.phegon.phegonbank.auth_users.services;

import com.phegon.phegonbank.auth_users.dtos.*;
import com.phegon.phegonbank.res.Response;

public interface AuthService {
    Response<LoginResponse> register(RegistrationRequest request);
    Response<LoginResponse> login(LoginRequest request);
    Response<UserDTO> getCurrentUser();
    Response<?> updatePassword(UpdatePasswordRequest request);
    Response<?> forgotPassword(String email);
    Response<?> resetPassword(ResetPasswordRequest request);
}
