package com.elms.employee_service.service;

import com.elms.employee_service.enums.EmployeeStatus;
import com.elms.employee_service.exceptions.ApiException;
import com.elms.employee_service.enums.ErrorCode;
import com.elms.employee_service.exceptions.ResourceNotFoundException;
import com.elms.employee_service.model.Employee;
import com.elms.employee_service.model.RefreshToken;
import com.elms.employee_service.dto.AuthResponse;
import com.elms.employee_service.dto.EmployeeLoginDto;
import com.elms.employee_service.repository.EmployeeRepository;
import com.elms.employee_service.repository.RefreshTokenRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AuthService {

    @Autowired
    JwtService jwtService;

    @Autowired
    AuthenticationManager authManager;

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.RefreshExpiration}")
    private long jwtRefreshExpiration;

    private static final Logger logger = LogManager.getLogger(AuthService.class);

    public AuthResponse authenticate(EmployeeLoginDto dto) {
        logger.info("Authenticating user with email: {}", dto.getEmail());
        try {
            Authentication authentication = authManager.authenticate(new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword()));

            if(!authentication.isAuthenticated()) {
                logger.warn("Authentication failed for user: {}", dto.getEmail());
                throw new ApiException(ErrorCode.UNAUTHENTICATED, "Invalid Credentials");
            }

            Employee emp= employeeRepository.findByEmail(dto.getEmail())
                    .orElseThrow(() -> {
                        logger.error("Employee not found with email: {}", dto.getEmail());
                        return new ResourceNotFoundException("Employee", dto.getEmail());
                    });

            if(emp.getAccountStatus()== EmployeeStatus.PENDING) {
                logger.warn("Account is pending approval for user: {}", dto.getEmail());
                throw new ApiException(ErrorCode.UNAUTHORIZED, "Account not approved!");
            }
            else if(emp.getAccountStatus()==EmployeeStatus.REJECTED){
                logger.warn("Account is rejected for user: {}", dto.getEmail());
                throw new ApiException(ErrorCode.UNAUTHORIZED, "Account rejected, Contact Admin Team");
            }

            logger.debug("Deleting existing refresh tokens for employee ID: {}", emp.getId());
            refreshTokenRepository.deleteByEmployeeId(emp);

            String accessToken = jwtService.generateToken(dto.getEmail(), false);
            String refreshToken = jwtService.generateToken(dto.getEmail(), true);

            RefreshToken rt = new RefreshToken();
            rt.setToken(refreshToken);
            rt.setExpiry(new Date(System.currentTimeMillis()+jwtRefreshExpiration));
            rt.setEmployeeId(emp);
            refreshTokenRepository.save(rt);

            logger.info("Authentication successful for user: {}", dto.getEmail());
            return new AuthResponse(accessToken, refreshToken);

        } catch (BadCredentialsException ex) {
            logger.error("Bad credentials provided for email: {}", dto.getEmail());
            throw new ApiException(ErrorCode.UNAUTHORIZED, "Invalid email or password");
        } catch (Exception ex) {
            logger.error("Unexpected error during authentication for user {}: {}", dto.getEmail(), ex.getMessage(), ex);
            throw new ApiException(ErrorCode.INTERNAL_ERROR, ex.getMessage());
        }
    }

    public AuthResponse refreshToken(String refreshToken) {
        logger.info("Refreshing access token using refresh token");
        if(!jwtService.validateToken(refreshToken)) {
            logger.warn("Invalid or expired refresh token");
            throw new ApiException(ErrorCode.UNAUTHORIZED, "Invalid or Expired Token");
        }

        String email = jwtService.getUsernameFromToken(refreshToken);
        RefreshToken token = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> {
                    logger.error("Refresh token not found in DB");
                    return new ApiException(ErrorCode.UNAUTHORIZED, "Refresh token not found");
                });
        if(token.getExpiry().before(new Date())) {
            logger.warn("Refresh token expired for user: {}", email);
            throw new ApiException(ErrorCode.UNAUTHORIZED, "Refresh token expired, Login Again!!");
        }

        String newAccessToken = jwtService.generateToken(email, false);
        logger.info("Access token refreshed successfully for user: {}", email);
        return new AuthResponse(newAccessToken, refreshToken);
    }
}
