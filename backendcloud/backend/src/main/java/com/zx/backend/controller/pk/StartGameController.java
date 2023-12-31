package com.zx.backend.controller.pk;

import com.zx.backend.service.pk.StartGameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;


@RestController
@RequestMapping("/pk")
public class StartGameController {
    @Autowired
    private StartGameService startGameService;

    @PostMapping("/start/game")
    public String startGame(@RequestParam MultiValueMap<String, String> data){
        Integer aId = Integer.parseInt(Objects.requireNonNull(data.getFirst("a_id")));
        Integer aBotId = Integer.parseInt(Objects.requireNonNull(data.getFirst("a_bot_id")));
        Integer bId = Integer.parseInt(Objects.requireNonNull(data.getFirst("b_id")));
        Integer bBotId = Integer.parseInt(Objects.requireNonNull(data.getFirst("b_bot_id")));

        return startGameService.startGame(aId, aBotId, bId, bBotId);
    }

}
