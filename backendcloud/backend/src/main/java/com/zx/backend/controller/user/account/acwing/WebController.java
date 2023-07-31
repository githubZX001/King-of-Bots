package com.zx.backend.controller.user.account.acwing;

import com.alibaba.fastjson.JSONObject;
import com.zx.backend.service.user.account.acwing.WebService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/user/account/acwing/web")
public class WebController {
    @Autowired
    private WebService webService;

    @GetMapping("/apply_code")
    public JSONObject applyCode(){
        return webService.applyCode();
    }

    @GetMapping("/receive_code")
    public JSONObject receiveCode(@RequestParam Map<String, String> data){
        String code = data.get("code");
        String state = data.get("state");
        return webService.receiveCode(code, state);
    }
}
