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

    private static String CLAIM_KEY = "systemRoles";
    @Value("${jwt.secret}")
    private String SECRET ;
    @Value("${jwt.secret}")
    private String INVITATION_SECRET ;

    @Value("${jwt.expiredDay}")
    private Long expiredDay ;
    private SecretKey SECRET_KEY ;
    private SecretKey INVITATION_SECRET_KEY ;

    @PostConstruct
    public void init() {
        SECRET_KEY = Keys.hmacShaKeyFor(Base64.getDecoder().decode(SECRET));
        INVITATION_SECRET_KEY = Keys.hmacShaKeyFor(Base64.getDecoder().decode(SECRET));
    }

    public String generateToken(String email, List<SystemRole> roles) {
        return Jwts.builder()
                .setSubject(email)
                .claim(CLAIM_KEY, roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiredDay)) // 1 day
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
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
        return getClaimFromToken(token, Claims::getSubject);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        JwtParser parser = Jwts.parser()
                .verifyWith(SECRET_KEY)
                .build();
        StringBuilder builder = new StringBuilder();
        return parser.parseSignedClaims(token).getPayload();
    }

    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY).build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    public Claims getClaims(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).build().parseClaimsJws(token).getBody();
    }





    public List<SimpleGrantedAuthority> getAuthorities(String token) {
        List<String> roles = Jwts.parser()
                .setSigningKey(SECRET_KEY).build()
                .parseClaimsJws(token)
                .getBody()
                .get(CLAIM_KEY, List.class);
        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(SECRET_KEY).build().parseClaimsJws(token);
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

    public String generateInvitationToken(String email, String projectId) {
        Date now = new Date();
        Date expiryDate = Date.from(LocalDateTime.now().plusHours(1).atZone(ZoneId.systemDefault()).toInstant());

        return Jwts.builder()
                .setSubject(email)
                .claim("projectId", projectId)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
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

}
