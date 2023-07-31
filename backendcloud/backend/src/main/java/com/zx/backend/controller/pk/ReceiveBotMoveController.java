package com.zx.backend.controller.pk;

import com.zx.backend.service.pk.ReceiveBotMoveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping("/pk")
public class ReceiveBotMoveController {
    @Autowired
    private ReceiveBotMoveService receiveBotMoveService;

    @PostMapping("/receive/bot/move")
    public String receiveBotMove(@RequestParam MultiValueMap<String, String> data){
        int userId = Integer.parseInt(Objects.requireNonNull(data.getFirst("user_id")));
        int direction = Integer.parseInt(Objects.requireNonNull(data.getFirst("direction")));
        return receiveBotMoveService.receiveBotMove(userId, direction);
    }
}
