package com.example.bemangahub.controller;

import com.example.bemangahub.ServiceResult;
import com.example.bemangahub.config.AppConstant;
import com.example.bemangahub.dto.AuthRequest;
import com.example.bemangahub.dto.RegisterRequest;
import com.example.bemangahub.dto.res.RefreshToken;
import com.example.bemangahub.dto.res.UserInfo;
import com.example.bemangahub.entity.Account;
import com.example.bemangahub.repository.AccountRepository;
import com.example.bemangahub.security.services.JwtService;
import com.example.bemangahub.security.services.UserDetailsServiceImpl;
import com.example.bemangahub.service.IAccountService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private IAccountService iAccountService;

    @PostMapping("/getInforCerdentialAccount")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getInforCerdentialAccount(@RequestBody RegisterRequest registerRequest) {
        try {
            // Thực hiện logic của phương thức và trả về thông tin tài khoản
            return ResponseEntity.ok(iAccountService.inforCerdentialAccount(registerRequest.getEmail()));
        } catch (Exception e) {
            // Bắt lỗi và trả về thông báo lỗi phù hợp
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Đã xảy ra lỗi: " + e.getMessage());
        }
    }

    @PostMapping("/signUp")
    public ResponseEntity<?> addNewUser(@RequestBody RegisterRequest registerRequest) {
        Boolean isExits = iAccountService.exitsEmail(registerRequest);
        if (isExits) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ServiceResult<>(AppConstant.BAD_REQUEST, "Email already exists"));
        } else {
            return ResponseEntity.ok(iAccountService.register(registerRequest));
        }
    }

    @PostMapping("/loginWithCerdential")
    public ResponseEntity<?> authenticateAndGetToken(@RequestBody AuthRequest authRequest, HttpServletResponse response) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));
            if (authentication.isAuthenticated()) {
                String accessToken = jwtService.generateAccessToken(authRequest.getEmail(), "SYSTEM");
                String refreshToken = jwtService.generateRefreshToken(authRequest.getEmail(),"SYSTEM");
                jwtService.setRefreshTokenCookie(refreshToken, response);
                UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getEmail());
                Optional<UserInfo> userInfoOptional = accountRepository.findCerdentialByEmail(authRequest.getEmail());
                return ResponseEntity.status(HttpStatus.OK).body("Đăng nhập thành công!, AccesToken: " + accessToken + " RefreshToken: " + refreshToken);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Đăng nhập that bai ");
            }
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Đăng nhập thất bại: Email hoặc mật khẩu không hợp lệ");
        }
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshToken refreshToken) {
        try {
            String email = jwtService.extractEmail(refreshToken.getToken());
            String accessToken = jwtService.generateAccessToken(email, refreshToken.getType());
            return ResponseEntity.status(HttpStatus.OK).body("AccesToken: " + accessToken);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Refresh token không hợp lệ");
        }
    }
}
