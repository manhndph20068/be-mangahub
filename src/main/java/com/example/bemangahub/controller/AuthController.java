package com.example.bemangahub.controller;

import com.example.bemangahub.ServiceResult;
import com.example.bemangahub.config.AppConstant;
import com.example.bemangahub.dto.AuthRequest;
import com.example.bemangahub.dto.req.RegisterRequest;
import com.example.bemangahub.dto.res.LoginResponse;
import com.example.bemangahub.dto.res.RefreshToken;
import com.example.bemangahub.dto.res.UserInfo;
import com.example.bemangahub.entity.Account;
import com.example.bemangahub.security.services.JwtService;
import com.example.bemangahub.service.IAccountService;
import com.example.bemangahub.service.ITypeService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private ITypeService iTypeService;

    @Autowired
    private IAccountService iAccountService;

    @PostMapping("/getInforCerdentialAccount")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getInforCerdentialAccount(@RequestBody RegisterRequest registerRequest) {
        try {
            return ResponseEntity.ok(iAccountService.inforCerdentialAccount(registerRequest.getEmail()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Đã xảy ra lỗi: " + e.getMessage());
        }
    }

    @PostMapping("/getInforSocialAccount")
    public ResponseEntity<?> getInforSocialAccount(@RequestBody RefreshToken refreshToken) {
        try {
            return ResponseEntity.ok(iAccountService.inforSocialAccount(refreshToken.getEmail(), refreshToken.getType()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Đã xảy ra lỗi: " + e.getMessage());
        }
    }


    @PostMapping("/signUpCredential")
    public ResponseEntity<?> signUp(@RequestBody AuthRequest authRequest) {
        Boolean isExits = iAccountService.exitsEmailCerdential(authRequest.getEmail());
        if (isExits) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ServiceResult<>(AppConstant.BAD_REQUEST, "Email already exists", null));
        } else {
            Account account = iAccountService.createCredentialAccount(authRequest.getEmail(),authRequest.getPassword(), "SYSTEM");
            if (account != null) {
                return ResponseEntity.status(HttpStatus.OK).body(new ServiceResult<>(AppConstant.SUCCESS, "Register success", null));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ServiceResult<>(AppConstant.ERROR, "Register failed", null));
            }
        }
    }

    @PostMapping("/loginWithSocial")
    public ResponseEntity<?> loginWithSocial(@RequestBody AuthRequest authRequest, HttpServletResponse response) {
        Boolean isEmailSocialExits = iAccountService.exitsEmailSocial(authRequest.getEmail(), authRequest.getType());
        Boolean isTypeExits = iTypeService.isTypeExist(authRequest.getType());
        if (isTypeExits) {
            if (isEmailSocialExits) {
                String accessToken = jwtService.generateAccessToken(authRequest.getEmail(), authRequest.getType());
                String refreshToken = jwtService.generateRefreshToken(authRequest.getEmail(), authRequest.getType());
                jwtService.setRefreshTokenCookie(refreshToken, response);
                ServiceResult<UserInfo> userInfoOptional = iAccountService.inforSocialAccount(authRequest.getEmail(), authRequest.getType());
                return ResponseEntity.status(HttpStatus.OK).body(new ServiceResult<>(AppConstant.SUCCESS, "Login success", LoginResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .userInfo(userInfoOptional.getData())
                        .build()));
            } else {
                Account account = iAccountService.createSocialAccount(authRequest.getEmail(), authRequest.getType());
                String accessToken = jwtService.generateAccessToken(authRequest.getEmail(), authRequest.getType());
                String refreshToken = jwtService.generateRefreshToken(authRequest.getEmail(), authRequest.getType());
                jwtService.setRefreshTokenCookie(refreshToken, response);
                ServiceResult<UserInfo> userInfoOptional = iAccountService.inforSocialAccount(authRequest.getEmail(), authRequest.getType());
                if (account != null) {
                    return ResponseEntity.status(HttpStatus.OK).body(new ServiceResult<>(AppConstant.SUCCESS, "Login success", LoginResponse.builder()
                            .accessToken(accessToken)
                            .refreshToken(refreshToken)
                            .userInfo(userInfoOptional.getData())
                            .build()));
                } else {
                    return ResponseEntity.status(HttpStatus.OK).body(new ServiceResult<>(AppConstant.BAD_REQUEST, "Create account fail"));
                }
            }
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(new ServiceResult<>(AppConstant.BAD_REQUEST, "Type not exists"));
        }
    }

    @PostMapping("/loginWithCredential")
    public ResponseEntity<?> loginWithCredential(@RequestBody AuthRequest authRequest, HttpServletResponse response) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));
            if (authentication.isAuthenticated()) {
                String accessToken = jwtService.generateAccessToken(authRequest.getEmail(), "SYSTEM");
                String refreshToken = jwtService.generateRefreshToken(authRequest.getEmail(), "SYSTEM");
                jwtService.setRefreshTokenCookie(refreshToken, response);
                ServiceResult<UserInfo> userInfoOptional = iAccountService.inforCerdentialAccount(authRequest.getEmail());
                return ResponseEntity.status(HttpStatus.OK).body(
                        new ServiceResult<>(AppConstant.SUCCESS, "Login success"
                                , LoginResponse.builder()
                                .accessToken(accessToken)
                                .refreshToken(refreshToken)
                                .userInfo(userInfoOptional.getData())
                                .build()));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        new ServiceResult<>(
                                AppConstant.BAD_REQUEST,
                                "Đăng nhập that bai",
                                null));
            }
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ServiceResult<>(
                    AppConstant.BAD_REQUEST,
                    "Đăng nhập thất bại: Email hoặc mật khẩu không hợp lệ",
                    null));
        }
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshToken refreshToken) {
        try {
            String email = jwtService.extractEmail(refreshToken.getToken());
            String accessToken = jwtService.generateAccessToken(email, refreshToken.getType());
            if (refreshToken.getType() == "SYSTEM") {
                ServiceResult<UserInfo> userInfoOptional = iAccountService.inforCerdentialAccount(email);
                return ResponseEntity.status(HttpStatus.OK).body(LoginResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken.getToken())
                        .userInfo(userInfoOptional.getData())
                        .build());
            } else if (refreshToken.getType() != "SYSTEM" && iTypeService.isTypeExist(refreshToken.getType())) {
                ServiceResult<UserInfo> userInfoOptional = iAccountService.inforSocialAccount(email, refreshToken.getType());
                return ResponseEntity.status(HttpStatus.OK).body(LoginResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken.getToken())
                        .userInfo(userInfoOptional.getData())
                        .build());
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Type không hợp lệ");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Refresh token không hợp lệ");
        }
    }
}
