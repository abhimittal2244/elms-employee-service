package com.elms.employee_service.service;

import com.elms.employee_service.exceptions.ApiException;
import com.elms.employee_service.enums.ErrorCode;
import com.elms.employee_service.exceptions.ExpiredTokenException;
import com.elms.employee_service.exceptions.InvalidTokenException;
import com.elms.employee_service.repository.EmployeeRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.function.Function;

@Service
public class JwtService {

    @Autowired
    EmployeeRepository employeeRepository;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    @Value("${jwt.RefreshExpiration}")
    private long jwtRefreshExpiration;

    @Value("${jwt.secret}")
    private String secretKey;


    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String email, boolean isRefresh) {
        Map<String, Object> claims = new HashMap<>();
        employeeRepository.findByEmail(email)
            .ifPresent(employee -> claims.put("role", employee.getRoleId().getName()));
        long expiration = isRefresh ? jwtRefreshExpiration : jwtExpiration;
        return buildToken(claims, email, expiration);
    }

    public String buildToken(Map<String, Object> claims, String email, long expiration) {
        return Jwts.builder()
                .claims()
                .add(claims)
                .subject(email)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .and()
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        try {
            final Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claimsResolver.apply(claims);
        } catch (ExpiredJwtException ex) {
            throw new ExpiredTokenException(ex.getMessage());
        } catch (JwtException ex) {
            throw new InvalidTokenException(ex.getMessage());
        } catch (Exception ex) {
            throw new ApiException(ErrorCode.INTERNAL_ERROR, "Unexpected error while parsing JWT: "+ex.getMessage());
        }
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String userName = getUsernameFromToken(token);
        return (userName.equals(userDetails.getUsername()) && isTokenExpired(token));
    }

    public boolean validateToken(String token) {
        try {
            return isTokenExpired(token);
        } catch (ExpiredJwtException ex) {
            throw new ExpiredTokenException("Token Expired");
        } catch (JwtException ex) {
            throw new InvalidTokenException("Invalid token");
        }
    }

    private boolean isTokenExpired(String token) {
        return !extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }


}
