package com.bloodbank.bloodbank.service;

import com.bloodbank.bloodbank.dto.request.LoginRequest;
import com.bloodbank.bloodbank.dto.request.RegisterRequest;
import com.bloodbank.bloodbank.dto.response.AuthResponse;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}
