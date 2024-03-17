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
@RequestMapping("/api-be/v1/auth")
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
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
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
                ServiceResult<UserInfo> userInfoOptional = iAccountService.inforSocialAccount(authRequest.getEmail(), authRequest.getType());
                String accessToken = jwtService.generateAccessToken(authRequest.getEmail(), authRequest.getType(),userInfoOptional.getData().getId());
                String refreshToken = jwtService.generateRefreshToken(authRequest.getEmail(), authRequest.getType(),userInfoOptional.getData().getId());
                jwtService.setRefreshTokenCookie(refreshToken, response);
                return ResponseEntity.status(HttpStatus.OK).body(new ServiceResult<>(AppConstant.SUCCESS, "Login success", LoginResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .userInfo(userInfoOptional.getData())
                        .build()));
            } else {
                Account account = iAccountService.createSocialAccount(authRequest.getEmail(), authRequest.getType());
                ServiceResult<UserInfo> userInfoOptional = iAccountService.inforSocialAccount(authRequest.getEmail(), authRequest.getType());
                String accessToken = jwtService.generateAccessToken(authRequest.getEmail(), authRequest.getType(),userInfoOptional.getData().getId());
                String refreshToken = jwtService.generateRefreshToken(authRequest.getEmail(), authRequest.getType(),userInfoOptional.getData().getId());
                jwtService.setRefreshTokenCookie(refreshToken, response);
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
            ServiceResult<UserInfo> userInfoOptional = iAccountService.inforSocialAccount(authRequest.getEmail(), "SYSTEM");
                    if (userInfoOptional.getData() != null) {
                        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userInfoOptional.getData().getId(), authRequest.getPassword()));
                        if (authentication.isAuthenticated()) {
                            String accessToken = jwtService.generateAccessToken(authRequest.getEmail(), "SYSTEM", userInfoOptional.getData().getId());
                            String refreshToken = jwtService.generateRefreshToken(authRequest.getEmail(), "SYSTEM", userInfoOptional.getData().getId());
                            jwtService.setRefreshTokenCookie(refreshToken, response);
                            return ResponseEntity.status(HttpStatus.OK).body(
                                    new ServiceResult<>(AppConstant.SUCCESS, "Login success"
                                            , LoginResponse.builder()
                                            .accessToken(accessToken)
                                            .refreshToken(refreshToken)
                                            .userInfo(userInfoOptional.getData())
                                            .build()));

                        } else {
                            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                                    new ServiceResult<>(AppConstant.FAIL, "Invalid credentials", null));
                        }
                    }else {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ServiceResult<>(
                                AppConstant.BAD_REQUEST, "account not found", null));
                    }
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ServiceResult<>(
                    AppConstant.BAD_REQUEST, e.toString(), null));
        }
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshToken refreshToken) {
        try {
            String email = jwtService.extractTokenToEmail(refreshToken.getToken());
            if (refreshToken.getType() == "SYSTEM") {
                ServiceResult<UserInfo> userInfoOptional = iAccountService.inforCerdentialAccount(email);
                String accessToken = jwtService.generateAccessToken(email, refreshToken.getType(),userInfoOptional.getData().getId());
                return ResponseEntity.status(HttpStatus.OK).body(new ServiceResult<>(
                        AppConstant.SUCCESS,
                        "refresh",
                        LoginResponse.builder()
                                .accessToken(accessToken)
                                .refreshToken(refreshToken.getToken())
                                .userInfo(userInfoOptional.getData())
                                .build()));
            } else if (refreshToken.getType() != "SYSTEM" && iTypeService.isTypeExist(refreshToken.getType())) {
                ServiceResult<UserInfo> userInfoOptional = iAccountService.inforSocialAccount(email, refreshToken.getType());
                String accessToken = jwtService.generateAccessToken(email, refreshToken.getType(),userInfoOptional.getData().getId());
                return ResponseEntity.status(HttpStatus.OK).body(new ServiceResult<>(
                        AppConstant.ERROR,
                        "refresh",
                        LoginResponse.builder()
                                .accessToken(accessToken)
                                .refreshToken(refreshToken.getToken())
                                .userInfo(userInfoOptional.getData())
                                .build()) );
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ServiceResult<>(
                        AppConstant.ERROR, "Type không hợp lệ", null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ServiceResult<>(
                    AppConstant.BAD_REQUEST, "Refresh token không hợp lệ", null));
        }
    }
}
