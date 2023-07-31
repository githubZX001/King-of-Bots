package com.zx.BotRunningSystem.service.impl.utls;

import org.joor.Reflect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.UUID;
import java.util.function.Supplier;


@Component
public class Consumer extends Thread{
    private final static String receiveUrl = "http://127.0.0.1:3000/pk/receive/bot/move";

    private static RestTemplate restTemplate;

    @Autowired
    public void setResTemplate(RestTemplate restTemplate){
        Consumer.restTemplate = restTemplate;
    }

    private Bot bot;
    public void startTimeout(long timeout, Bot bot){
        this.bot = bot;
        this.start();

        try {
            this.join(timeout); // 最多等待多少秒，如果提前结束的话就直接中断
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            this.interrupt(); // 中断当前线程
        }
    }

    private String addUid(String code, String uid){ // 在code中的Bot类名 后添加id
        int k = code.indexOf(" implements java.util.function.Supplier<Integer>");
        return code.substring(0, k) + uid + code.substring(k);
    }

    private void sendDirection(Integer userId, Integer direction){
        LinkedMultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("user_id", userId.toString());
        data.add("direction", direction.toString());
        restTemplate.postForObject(receiveUrl, data, String.class);
    }

    @Override
    public void run() {
        String uid = UUID.randomUUID().toString().substring(0, 8);

        Supplier<Integer> botInterface = Reflect.compile(
                "com.lzy.BotRunningSystem.utils.Bot" + uid,
                addUid(bot.getBotCode(), uid)
        ).create().get();

        File file = new File("input.txt");
        try(PrintWriter fout = new PrintWriter(file)){
            fout.println(bot.getInput());
            fout.flush();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }


        Integer direction = botInterface.get();
        System.out.println("move = " + " " + bot.getUserId() + " " + direction);

        sendDirection(bot.getUserId(), direction);
    }
}
