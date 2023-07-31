package com.zx.BotRunningSystem.service.impl;

import com.zx.BotRunningSystem.service.BotRunningService;
import com.zx.BotRunningSystem.service.impl.utls.BotPool;
import org.springframework.stereotype.Service;


@Service
public class BotRunningServiceImpl implements BotRunningService {
    public final static BotPool botPool = new BotPool();

    @Override
    public String addBot(Integer userId, String botCode, String input) {
        // printGeo(input);
        System.out.println();
        botPool.addBot(userId, botCode, input);
        return "add bot success";
    }

    private void printGeo(String input){
        int k = input.indexOf("#");
        input = input.substring(0, k);
        for(int i = 0 ; i < 13 * 14; i++){
           if(i % 14 == 0) System.out.println();
            System.out.print(input.charAt(i) + "  ");
        }
    }
}
