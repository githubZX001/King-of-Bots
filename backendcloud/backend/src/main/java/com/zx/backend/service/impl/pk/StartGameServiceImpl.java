package com.zx.backend.service.impl.pk;

import com.zx.backend.consumer.WebSocketServer;
import com.zx.backend.service.pk.StartGameService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class StartGameServiceImpl implements StartGameService {
    @Override
    public String startGame(Integer aId, Integer aBotId, Integer bId, Integer bBotId) {
        log.info("start game: " + aId + bId);
        WebSocketServer.startGame(aId, aBotId, bId, bBotId);
        return "start game success";
    }
}
