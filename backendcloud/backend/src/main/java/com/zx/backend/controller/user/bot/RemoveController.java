package com.zx.backend.controller.user.bot;

import com.zx.backend.service.user.bot.RemoveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


@RestController
@RequestMapping("/api/user/bot")
public class RemoveController {
    @Autowired
    private RemoveService removeService;

    @GetMapping("/remove")
    public Map<String, String> removeBot(@RequestParam Map<String, String> map){
        return removeService.removeBot(map);
    }
}
