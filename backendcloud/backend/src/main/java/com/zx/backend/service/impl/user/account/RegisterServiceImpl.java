package com.zx.backend.service.impl.user.account;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zx.backend.mapper.UserMapper;
import com.zx.backend.pojo.User;
import com.zx.backend.service.user.account.RegisterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author li
 * @data 2023/3/7
 * @time 12:35
 */

@Service
@Slf4j

public class RegisterServiceImpl implements RegisterService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Map<String, String> register(String username, String password, String confirmedPassword) {
        HashMap<String, String> map = new HashMap<>();
        username = username.trim();
        if(username == null || username.length() == 0){
            map.put("error_message", "用户名不能为空");
            return map;
        }
        if(password == null ||  confirmedPassword == null){
            map.put("error_message", "密码不能为空");
            return map;
        }

        username = username.trim();

        if(password.length() == 0 || confirmedPassword.length() == 0){
            map.put("error_message", "密码不能为空");
            return map;
        }

        if(username.length() > 100 || password.length() > 100 || confirmedPassword.length() > 100){
            map.put("error_message", "账号或者密码长度不能太长");
            return map;
        }

        if(!confirmedPassword.equals(password)){
            map.put("error_message", "两次密码输入不一致");
            return map;
        }

        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
        lqw.eq(User::getUsername, username);
        List<User> users = userMapper.selectList(lqw);
        if(!users.isEmpty()){
            map.put("error_message", "用户名已经存在");
            return map;
        }

        String encode = passwordEncoder.encode(password);
        String photo = "https://cdn.acwing.com/media/user/profile/photo/266179_lg_206033388b.jpg";
        User user = new User(null, username, encode, photo, 1500, null);
        userMapper.insert(user);
        log.info("注册用户成功:" + username);
        map.put("error_message", "success");
        return map;
    }

}
