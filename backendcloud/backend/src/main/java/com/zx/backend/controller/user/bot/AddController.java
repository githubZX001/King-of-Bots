package com.zx.backend.controller.user.bot;

import com.zx.backend.service.user.bot.AddService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


@RestController
@RequestMapping("/api/user/bot")
public class AddController {

    @Autowired
    private AddService addService;

    @PostMapping("/add")
    public Map<String, String> addBot(@RequestParam Map<String, String> map){
        return addService.addBot(map);
    }
}
