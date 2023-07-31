package com.zx.backend.controller.user.account;

import com.zx.backend.service.user.account.InfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/user/account")
public class InfoController {

    @Autowired
    private InfoService infoService;

    @GetMapping("/info")
    public Map<String, String> getInfo(){
        return infoService.getInfo();
    }
}
