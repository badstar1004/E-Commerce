package com.zerobase.domain.config;


import com.zerobase.domain.common.UserType;
import com.zerobase.domain.common.UserVo;
import com.zerobase.domain.util.Aes256Util;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import java.util.Objects;

public class JwtAuthenticationProvider {
    private final String secretKey = "secretKey";
    private final long tokenValidTime = 1000L * 60 * 60 * 24;  // 하루

    /**
     * 토큰 발행
     * @param userPK
     * @param id
     * @param userType
     * @return
     */
    public String createToken(String userPK, Long id, UserType userType){
        Claims claims
             = Jwts.claims().setSubject(Aes256Util.encrypt(userPK)).setId(Aes256Util.encrypt(id.toString()));

        claims.put("roles", userType);
        Date now = new Date();

        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(new Date(now.getTime() + tokenValidTime))
            .signWith(SignatureAlgorithm.HS256, secretKey)
            .compact();
    }

    /**
     * 토큰 유효성 검사
     * @param jwToken
     * @return
     */
    public boolean validateToken(String jwToken) {
        try{
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwToken);
            return !claimsJws.getBody().getExpiration().before(new Date());
        } catch (Exception ex){
            return false;
        }
    }

    /**
     * User 정보 반환
     * @param token
     * @return
     */
    public UserVo getUserVo(String token) {
        Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
        return new UserVo(
            Long.valueOf(Objects.requireNonNull(Aes256Util.decrypt(claims.getId()))),
            Aes256Util.decrypt(claims.getSubject())
        );
    }
}
