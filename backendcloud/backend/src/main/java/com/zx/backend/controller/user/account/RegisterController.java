package com.zx.backend.controller.user.account;

import com.zx.backend.service.user.account.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user/account")
public class RegisterController {

    @Autowired
    private RegisterService registerService;

    @PostMapping("/register")
    public Map<String, String> register(@RequestParam Map<String, String> map){
        String username = map.get("username");
        String password = map.get("password");
        String confirmedPassword = map.get("confirmedPassword");
        return registerService.register(username, password, confirmedPassword);
    }
}
