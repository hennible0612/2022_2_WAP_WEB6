package com.web.backend.refreshToken;

import com.web.backend.config.JWTTokenHelper;
import com.web.backend.proconboard.ProConTopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;

@RestController
public class refreshTokenController {
    @Autowired
    private RefreshTokenService refreshTokenService;
    @Autowired
    private JWTTokenHelper jwtTokenHelper;
    @PostMapping("token/refresh")
    public Map<String, Object> JwtTokenRefresh(@RequestBody HashMap<String, Object> param) throws InvalidKeySpecException, NoSuchAlgorithmException {

        Map<String, Object> returnMap = jwtTokenHelper.generateAccessToken(param.get("refreshToken").toString(),
                param.get("accessToken").toString(), param.get("userId").toString());


        return returnMap;
    }
}
