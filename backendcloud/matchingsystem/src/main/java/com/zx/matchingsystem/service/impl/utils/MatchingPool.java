package com.zx.matchingsystem.service.impl.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;



@Component
@Slf4j
public class MatchingPool extends Thread {

    private static ArrayList<Player> players = new ArrayList<>();
    private ReentrantLock lock = new ReentrantLock();
    private static RestTemplate restTemplate;
    private final static String startGameUrl = "http://127.0.0.1:3000/pk/start/game";
    @Autowired
    private void setRestTemplate(RestTemplate restTemplate){
        MatchingPool.restTemplate = restTemplate;
    }

    public void addPlayer(Integer userId, Integer rating, Integer botId){
        lock.lock();
        try{
            for(Player play : players){
                if (play.getUserId() == userId){
                    players = ArrayListUtils.removePlayerArray(players, userId);
                }
            }
            players.add(new Player(userId, rating, botId,0));

        }finally {
            lock.unlock();
        }
    }

    public void removePlayer(Integer userId){
        lock.lock();
        try{
            players = ArrayListUtils.removePlayerArray(players, userId);
        }finally {
            lock.unlock();
        }
    }

    private void increaseWaitingTime(){ // 所有玩家等待时间加一
        for(Player player : players){
            player.setWaitingTime(player.getWaitingTime() + 1);
        }
    }

    private boolean checkMatched(Player a, Player b){ // 判断两名玩家是否匹配
        int ratingDelta = Math.abs(a.getRating() - b.getRating());
        int waitingTime = Math.min(a.getWaitingTime(), b.getWaitingTime());
        return ratingDelta <= waitingTime * 10;
    }

    private void sendResult(Player a, Player b){ // 返回a和b的匹配结果
        log.info("matching success " + a.getUserId() + " " + b.getUserId());
        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("a_id", a.getUserId().toString());
        data.add("a_bot_id", a.getBotId().toString());
        data.add("b_id", b.getUserId().toString());
        data.add("b_bot_id", b.getBotId().toString());
        restTemplate.postForObject(startGameUrl, data, String.class);
    }

    private void mathingPlayer(){ // 尝试匹配所有玩家
        boolean[] booleans = new boolean[players.size()];
        for (int i = 0; i < players.size(); i++) {
            if(booleans[i]) continue;
            for (int j = i + 1; j < players.size() ; j++) {
                if(booleans[j]) continue;
                Player a = players.get(i), b = players.get(j);
                if(checkMatched(a, b)){
                    booleans[i] = booleans[j] = true;
                    sendResult(a, b);
                    break;
                }
            }
        }
        ArrayList<Player> newPlayers = new ArrayList<>();
        for (int i = 0; i < players.size(); i++) {
            if(!booleans[i]){
                newPlayers.add(players.get(i));
            }
        }
        players = newPlayers;
    }

    @Override
    public void run() {
        while(true) {
            try {
                Thread.sleep(1000);
                lock.lock();
                try {
                    increaseWaitingTime();
                    mathingPlayer();
                } finally {
                    lock.unlock();
                }

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
