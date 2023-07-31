package com.zx.backend.service.impl.user.account;

import com.zx.backend.pojo.User;
import com.zx.backend.service.user.account.LoginService;
import com.zx.backend.utils.JwtUtil;
import com.zx.backend.utils.UserDetailsUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author li
 * @data 2023/3/7
 * @time 12:34
 */

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public Map<String, String> login(String username, String password, String code, String uuid) {
        HashMap<String, String> map = new HashMap<>();

        String cache_code = redisTemplate.opsForValue().get("login_user:" + uuid);
        redisTemplate.delete("login_user:" + uuid);

        if("".equals(cache_code) || cache_code == null) {
            map.put("error_message", "验证码失效");
            return map;
        }

        if(!cache_code.equals(code)) {
            map.put("error_message", "验证码不正确");
            return map;
        }

        UsernamePasswordAuthenticationToken authenticationToken
                = new UsernamePasswordAuthenticationToken(username, password);

        // 这个方法的入参和返回都是Authentication类型的
        // 该接口的作用是对用户的未授信凭据进行认证，认证通过则返回授信状态的凭据，否则将抛出认证异常
        // 如果登录失败会自动处理

        Authentication authenticate = authenticationManager.authenticate(authenticationToken);

        UserDetailsUtils principal = (UserDetailsUtils)authenticate.getPrincipal();

        User user = principal.getUser();

        String token = JwtUtil.createJWT(user.getId().toString());

        map.put("error_message", "success");

        map.put("token", token);

        return map;
    }
}
