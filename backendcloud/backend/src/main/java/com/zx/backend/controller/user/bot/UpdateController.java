package com.zx.backend.controller.user.bot;

import com.zx.backend.service.user.bot.UpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


@RestController
@RequestMapping("/api/user/bot")
public class UpdateController {
    @Autowired
    private UpdateService updateService;

    @GetMapping("/update")
    public Map<String, String> update(@RequestParam Map<String, String> map){
        return updateService.updateBot(map);
    }
}
