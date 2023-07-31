package com.zx.backend.controller.user.account;

import com.zx.backend.service.user.account.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user/account")
public class LoginController {

    @Autowired
    private LoginService loginService;

    @PostMapping("/token")
    public Map<String, String> login(@RequestParam Map<String, String> map){
        String username = map.get("username");
        String password = map.get("password");
        String code = map.get("code");
        String uuid = map.get("uuid");
        System.out.println(code + " " + uuid);
        return loginService.login(username, password, code, uuid);
    }
}
