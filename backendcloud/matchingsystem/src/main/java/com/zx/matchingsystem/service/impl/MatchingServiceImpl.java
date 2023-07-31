package com.zx.matchingsystem.service.impl;

import com.zx.matchingsystem.service.MatchingService;
import com.zx.matchingsystem.service.impl.utils.MatchingPool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class MatchingServiceImpl implements MatchingService {

    public final static MatchingPool matchingPool = new MatchingPool();

    @Override
    public String addPlayer(Integer userId, Integer rating, Integer botId) {
        log.info("start matching " + userId + " " + rating);
        matchingPool.addPlayer(userId, rating, botId);
        return "add player" + userId;
    }

    @Override
    public String removePlayer(Integer userId) {
        log.info("stop matching " + userId);
        matchingPool.removePlayer(userId);
        return "stop player " + userId;
    }
}
