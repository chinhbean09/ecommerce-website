package com.chinhbean.shopapp.components;

import com.chinhbean.shopapp.exceptions.InvalidParamException;
import com.chinhbean.shopapp.models.Token;
import com.chinhbean.shopapp.repositories.TokenRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.security.SecureRandom;
import java.util.*;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class JwtTokenUtils {
    @Value("${jwt.expiration}")
    private int expiration; //save to an environment variable
    @Value("${jwt.secretKey}")
    private String secretKey;
    private final TokenRepository tokenRepository;
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenUtils.class);

    public String generateToken(com.chinhbean.shopapp.models.User user) throws Exception{
        //properties => claims
        //cac thuộc tính của các đối tượng mà ta đưa vào security (user => security), các thuộc tính đó gọi là claims
        //nên ta phải lưu 1 đối tượng để chứa các claim, mã hóa thuộc tính bằng Map(key,value)
        Map<String, Object> claims = new HashMap<>();
        //this.generateSecretKey();
        claims.put("phoneNumber", user.getPhoneNumber());
        //thêm claims Id để đưa vào bên trong token
        claims.put("userId",user.getId());
        try {
            String token = Jwts.builder()
                    .setClaims(claims)
                    .setSubject(user.getPhoneNumber())
                    .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000L))
                    .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                    .compact();
            return token;
        }catch (Exception e) {
            throw new InvalidParamException("Cannot create jwt token, error: "+ e.getMessage());
            //return null;
        }
    }
//key được sử dụng để ký và giải mã token được tạo ra từ chuỗi base64 được cung cấp trong secretKey.
    private Key getSignInKey() {
        byte[] bytes = Decoders.BASE64.decode(secretKey);
        //Keys.hmacShaKeyFor(Decoders.BASE64.decode("TaqlmGv1iEDMRiFp/pHuID1+T84IABfuA0xXh4GhiUI="));
        return Keys.hmacShaKeyFor(bytes);
    }

    private String generateSecretKey() {
        SecureRandom random = new SecureRandom();
        byte[] keyBytes = new byte[32]; // 256-bit key
        random.nextBytes(keyBytes);
        String secretKey = Encoders.BASE64.encode(keyBytes);
        return secretKey;
    }
    //xây dựng một đối tượng xử lý token JWT. (JwtParserBuilder)
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey()) //giải mã token
                .build()
                .parseClaimsJws(token) //Parse chuỗi token để lấy ra tất cả các claims
                .getBody(); //Lấy phần payload của token, nơi chứa các claims
    }

    //extract riêng claim mà mình muốn,
    public  <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = this.extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    //check expiration
    public boolean isTokenExpired(String token) {
        Date expirationDate = this.extractClaim(token, Claims::getExpiration);
        return expirationDate.before(new Date());
    }
    public String extractPhoneNumber(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    public boolean validateToken(String token, UserDetails userDetails) {
        //trích xuất thôn tin(phoneNumber) từ token và thông tin userDetails(Username = phoneNumber) từ spring security
        try {
            String phoneNumber = extractPhoneNumber(token);
            Token existingToken = tokenRepository.findByToken(token);
            if(existingToken == null || existingToken.isRevoked() == true) {
                return false;
            }
            return (phoneNumber.equals(userDetails.getUsername()))
                    && !isTokenExpired(token);
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }
}
