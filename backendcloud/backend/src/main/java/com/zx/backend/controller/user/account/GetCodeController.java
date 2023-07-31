package com.zx.backend.controller.user.account;

import com.zx.backend.service.user.account.GetCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/user/account")
public class GetCodeController {
    @Autowired
    private GetCodeService getCodeService;

    @PostMapping("/getCode")
    public Map<String, String> getCode(){
        return getCodeService.getCode();
    }
}
