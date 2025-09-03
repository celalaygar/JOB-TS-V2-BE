package com.tracker.job_ts.auth.config;

import com.tracker.job_ts.auth.entity.SystemRole;
import com.tracker.job_ts.auth.exception.*;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JWTProvider {

    private static final String CLAIM_KEY = "systemRoles";

    @Value("${jwt.secret}")
    private String SECRET;
    @Value("${jwt.invitation-secret}")
    private String INVITATION_SECRET;
    @Value("${jwt.email-change-secret}")
    private String EMAIL_CHANGE_SECRET;
    @Value("${jwt.password-reset-secret}")
    private String PASSWORD_RESET_SECRET;

    @Value("${jwt.expiredDay}")
    private Long expiredDay;

    @Value("${jwt.email-change-expiredDay}")
    private Long emailExpiredDay;

    @Value("${jwt.invitation-expiredDay}")
    private Long invitationExpiredDay;

    @Value("${jwt.password-reset-expiredDay}")
    private Long passwordResetExpiredDay;

    private SecretKey SECRET_KEY;
    private SecretKey INVITATION_SECRET_KEY;
    private SecretKey EMAIL_CHANGE_SECRET_KEY;
    private SecretKey PASSWORD_RESET_SECRET_KEY;

    @PostConstruct
    public void init() {
        SECRET_KEY = Keys.hmacShaKeyFor(Base64.getDecoder().decode(SECRET));
        INVITATION_SECRET_KEY = Keys.hmacShaKeyFor(Base64.getDecoder().decode(INVITATION_SECRET));
        EMAIL_CHANGE_SECRET_KEY = Keys.hmacShaKeyFor(Base64.getDecoder().decode(EMAIL_CHANGE_SECRET));
        PASSWORD_RESET_SECRET_KEY = Keys.hmacShaKeyFor(Base64.getDecoder().decode(PASSWORD_RESET_SECRET));
    }

    public String generateToken(String email, List<SystemRole> roles) {
        return Jwts.builder()
                .setSubject(email)
                .claim(CLAIM_KEY, roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiredDay))
                .signWith(SECRET_KEY, SignatureAlgorithm.HS512)
                .compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public String getUsernameFromToken(String token) {
        try {
            return getClaimFromToken(token, Claims::getSubject);
        } catch (ExpiredJwtException e) {
            throw new ExpiredJwtTokenException("Token süresi dolmuş.");
        }
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        JwtParser parser = Jwts.parser()
                .verifyWith(SECRET_KEY)
                .build();
        return parser.parseSignedClaims(token).getPayload();
    }

    public List<SimpleGrantedAuthority> getAuthorities(String token) {
        List<String> roles = Jwts.parser()
                .verifyWith(SECRET_KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get(CLAIM_KEY, List.class);
        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(SECRET_KEY).build().parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            throw new ExpiredJwtTokenException("Token süresi dolmuş.");
        } catch (MalformedJwtException e) {
            throw new MalformedJwtTokenException("Geçersiz token formatı.");
        } catch (UnsupportedJwtException e) {
            throw new UnsupportedJwtTokenException("Desteklenmeyen token türü.");
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentTokenException("Token boş ya da hatalı.");
        } catch (SecurityException e) {
            throw new SecurityJwtTokenException("Token imzası doğrulanamadı.");
        } catch (Exception e) {
            throw new UnauthorizedException("Token doğrulama işleminde bir hata oluştu.");
        }
    }

    public boolean isTokenExpiredOnly(String token) {
        if (token == null || token.isBlank()) {
            return false;
        }

        try {
            Jwts.parser()
                    .verifyWith(SECRET_KEY)
                    .build()
                    .parseSignedClaims(token);

            // Eğer buraya kadar geldiyse, token geçerli ama expired olmayabilir
            // Dolayısıyla expired DEĞİL → false
            return false;

        } catch (ExpiredJwtException e) {
            // Token var ama expired → true
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // Diğer tüm hatalar (malformed, signature invalid, null vs.) → false
            return false;
        }
    }

    public String generateInvitationToken(String email, String projectId) {
        Date now = new Date();
        Date expiryDate = Date.from(LocalDateTime.now().plusHours(1).atZone(ZoneId.systemDefault()).toInstant());

        return Jwts.builder()
                .setSubject(email)
                .claim("projectId", projectId)
                .setIssuedAt(now)
                .setExpiration((new Date(System.currentTimeMillis() + invitationExpiredDay))) // 15 dk
                .signWith(SignatureAlgorithm.HS512, INVITATION_SECRET_KEY) // farklı key
                .compact();
    }
    public Claims parseInvitationToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(SECRET_KEY)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            throw new ExpiredJwtTokenException("Davet token süresi dolmuş.");
        } catch (MalformedJwtException e) {
            throw new MalformedJwtTokenException("Geçersiz token formatı.");
        } catch (UnsupportedJwtException e) {
            throw new UnsupportedJwtTokenException("Desteklenmeyen token türü.");
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentTokenException("Token boş ya da hatalı.");
        } catch (SecurityException e) {
            throw new SecurityJwtTokenException("Token imzası doğrulanamadı.");
        } catch (Exception e) {
            throw new UnauthorizedException("Token doğrulama işleminde bir hata oluştu.");
        }
    }


    /**
     * Generates a special token for the email change confirmation link.
     *
     * @param userId The ID of the user whose email is being changed.
     * @param newEmail The new email address.
     * @return The signed JWT string.
     */
    public String generateEmailChangeToken(String userId, String newEmail) {
        return Jwts.builder()
                .setSubject(userId)
                .claim("newEmail", newEmail)
                .setIssuedAt(new Date())
                .setExpiration((new Date(System.currentTimeMillis() + emailExpiredDay))) // 15 dk
                .signWith(SignatureAlgorithm.HS512, EMAIL_CHANGE_SECRET_KEY)
                .compact();
    }

    // You'll also need a method to parse this token, similar to parseInvitationToken
    public Claims parseEmailChangeToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(EMAIL_CHANGE_SECRET_KEY)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            throw new ExpiredJwtTokenException("Email change link has expired.");
        } catch (MalformedJwtException e) {
            throw new MalformedJwtTokenException("Invalid token format.");
        } catch (UnsupportedJwtException e) {
            throw new UnsupportedJwtTokenException("Unsupported token type.");
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentTokenException("Token is empty or invalid.");
        } catch (SecurityException e) {
            throw new SecurityJwtTokenException("Token signature could not be verified.");
        } catch (Exception e) {
            throw new UnauthorizedException("An error occurred during token validation.");
        }
    }





    public String generateResetPasswordToken(String userId) {
        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + passwordResetExpiredDay)) // örn. 15 dk
                .signWith(SignatureAlgorithm.HS512, PASSWORD_RESET_SECRET_KEY)
                .compact();
    }

    public boolean validateResetPasswordToken(String token) {
        try {
            Jwts.parser().setSigningKey(PASSWORD_RESET_SECRET_KEY).build().parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public String getUserIdFromResetPasswordToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(PASSWORD_RESET_SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }
}
