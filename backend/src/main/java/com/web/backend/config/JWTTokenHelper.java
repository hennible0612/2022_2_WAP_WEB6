package com.web.backend.config;

import com.web.backend.proconboard.ProConTopicEntity;
import com.web.backend.proconboard.ProConTopicRepository;
import com.web.backend.refreshToken.RefreshTokenEntity;
import com.web.backend.refreshToken.RefreshTokenRepository;
import com.web.backend.refreshToken.RefreshTokenService;
import com.web.backend.user.UserDetailsRepository;
import com.web.backend.user.UserEntity;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;

@Component
public class JWTTokenHelper {

    public static final long JWT_TOKEN_VALIDITY = 1000 * 60 * 60;
    @Value("${jwt.auth.app}")
    private String appName;
    // 비밀키
    @Value("${jwt.auth.secret_key}")
    private String secretKey;
    //만료 시간
    @Value("${jwt.auth.expires_in}")
    private int expiresIn;
    // 암호화 방법
    private SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS256;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    @Autowired
    private UserDetailsRepository userDetailsRepository;
    @Autowired
    private RefreshTokenService refreshTokenService;

    // token으로 사용자 속성정보 조회
    private Claims getAllClaimsFromToken(String token) {
        Claims claims;
        try {
            claims = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            claims = null;
        }
        return claims;
    }

    // token 에서 유저이름 가져옴
    public String getUsernameFromToken(String token) {
        String username;
        try {
            final Claims claims = this.getAllClaimsFromToken(token);
            username = claims.getSubject();
        } catch (Exception e) {
            username = null;
        }
        return username;
    }

    //토큰 생성
    public String generateToken(String username) throws InvalidKeySpecException, NoSuchAlgorithmException {

        return Jwts.builder() // 토큰 만들어줌
                .setIssuer(appName) // app 이름 toast-app
                .setSubject(username) // 사용자 이름
                .setIssuedAt(new Date()) // 현 시간
                .setExpiration(generateExpirationDate()) // 만료 날짜
                .signWith(SIGNATURE_ALGORITHM, secretKey) // 방식
                .compact();
    }

    // JWT accessToken 생성
    public String generateRefreshToken(String username) {
        return Jwts.builder()
                .setIssuer(appName) // app 이름 toast-app
                .setSubject(username)
                .setIssuedAt(new Date()) // 현 시간
//                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 5)) // 5시간
                .setExpiration(new Date(new Date().getTime() + expiresIn * 30)) // 5시간
                .signWith(SIGNATURE_ALGORITHM, secretKey)
                .compact();


    }

    // 만료 날짜 생성
    private Date generateExpirationDate() {
        return new Date(new Date().getTime() + expiresIn * 1000);
       // return new Date(new Date().getTime() + expiresIn * 100);/
    }

    // 토큰 유효성 검증
    public Boolean validateToken(String token, UserDetails userDetails) { 
        final String username = getUsernameFromToken(token);
        return (
                username != null &&
                        username.equals(userDetails.getUsername()) &&
                        !isTokenExpired(token)
        );
    }
    // accessToken 유효시간 알림(second)

    // 토큰 시간 만료 체크
    public boolean isTokenExpired(String token) {
        Date expireDate = getExpirationDate(token);
        System.out.println("-----------------------------------------");
        System.out.println("expireDate: " + expireDate);
//        System.out.println(expireDate.before(new Date()));
        System.out.println("-----------------------------------------");

        return expireDate.before(new Date());
//        return false;
    }
    // 토큰 만료 시간 리턴
    private Date getExpirationDate(String token) {
        Date expireDate;
        try {
            final Claims claims = this.getAllClaimsFromToken(token);
            System.out.println("-----------------------------------------");
            System.out.println("Claims: " + claims);
            System.out.println("-----------------------------------------");

            expireDate = claims.getExpiration();
        } catch (Exception e) {
            System.out.println("-----------------------------------------");
            System.out.println("expireDate is: " + "null");
            System.out.println("-----------------------------------------");
            expireDate = null;
        }
        return expireDate;
    }

    public Date getIssuedAtDateFromToken(String token) {
        Date issueAt;
        try {
            final Claims claims = this.getAllClaimsFromToken(token);
            issueAt = claims.getIssuedAt();
        } catch (Exception e) {
            issueAt = null;
        }
        return issueAt;
    }

    //get token from request
    public String getToken(HttpServletRequest request) {

        String authHeader = getAuthHeaderFromHeader(request);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7); //Bearer 짜르고 token return
        }

        return null;
    }

    // Authorization header
    public String getAuthHeaderFromHeader(HttpServletRequest request) {
        return request.getHeader("Authorization");
    }



    // refreshToken 만료 체크 후 재발급
    public Boolean reGenerateRefreshToken(String userName){
        UserEntity user = userDetailsRepository.findByUserName(userName);
        RefreshTokenEntity target = refreshTokenRepository.findById(user.getId()).get();
        String refreshToken = target.getRefreshToken();
        System.out.println(refreshToken);
//        isTokenExpired(refreshToken);

//        System.out.println(refreshToken);
//        UserDetails userDetails = userDetailsService.loadUserByUsername(userName);
//        refreshToken = refreshToken.substring(7);
//        System.out.println(refreshToken);
//        System.out.println(isTokenExpired(refreshToken));

        System.out.println("-----------------------------------------");
        System.out.println("isTokenExpired: " + isTokenExpired(refreshToken));
        System.out.println("-----------------------------------------");
        if (!isTokenExpired(refreshToken)) {

            System.out.println(refreshToken);
            String reGenRefreshToken = generateRefreshToken(user.getUsername());
            System.out.println(reGenRefreshToken);

            refreshTokenService.saveRefreshToken(user, reGenRefreshToken);
            return true;

        }
        return true;

//        isTokenExpired(refreshToken);
        // refreshToken 만료 여부 체크
//        try {
//            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(refreshToken);
//            return true;
//        }
//        catch(ExpiredJwtException e) {
//            String reGenRefreshToken = generateRefreshToken(user.getUsername());
//            refreshTokenService.saveRefreshToken(user, reGenRefreshToken);
//
//            return true;
//        }

//        catch(Exception e) {
////            log.error("[reGenerateRefreshToken] refreshToken 재발급 중 문제 발생 : {}", e.getMessage());
//            return false;
//        }
    }


    @Transactional
    public Map<String, Object> generateAccessToken(String refreshToken, String accessToken, String userId) throws InvalidKeySpecException, NoSuchAlgorithmException {
        Map<String, Object> returnMap = new HashMap<String, Object>();
        RefreshTokenEntity refreshTokenEntity = refreshTokenRepository.findById(Long.valueOf(userId)).get();
        String userName = "";

        // token 정보가 존재하지 않는 경우
        if(refreshTokenEntity == null) {
            returnMap.put("result", "fail");
            returnMap.put("msg", "refresh token 정보가 존재하지 않습니다.");
            return returnMap;
        }

        if (!isTokenExpired(refreshToken) && refreshTokenEntity.getRefreshToken().equals(refreshToken)) {
                userName = getUsernameFromToken(refreshToken);
                String newToken = generateToken(userName);
                returnMap.put("result", "success");
                returnMap.put("accessToken", newToken);


        }else {
            returnMap.put("result", "fail");
            returnMap.put("msgf", "refresh token이 만료되었거나 정보가 존재하지 않습니다.");
        }

        return returnMap;


    }

}