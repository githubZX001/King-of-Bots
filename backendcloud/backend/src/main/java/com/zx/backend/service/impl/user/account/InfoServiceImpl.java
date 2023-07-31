package com.zx.backend.service.impl.user.account;

import com.zx.backend.mapper.UserMapper;
import com.zx.backend.pojo.User;
import com.zx.backend.service.user.account.InfoService;
import com.zx.backend.utils.UserDetailsUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author li
 * @data 2023/3/7
 * @time 12:35
 */
@Service
public class InfoServiceImpl implements InfoService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public Map<String, String> getInfo() {
        UsernamePasswordAuthenticationToken authenticationToken
                = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        UserDetailsUtils loginUser = (UserDetailsUtils)authenticationToken.getPrincipal();
        User user = loginUser.getUser();

        HashMap<String, String> map = new HashMap<>();

        map.put("error_message", "success");

        map.put("id", user.getId().toString());
        map.put("username", user.getUsername());
        map.put("photo", user.getPhoto());

        return map;

    }
}
