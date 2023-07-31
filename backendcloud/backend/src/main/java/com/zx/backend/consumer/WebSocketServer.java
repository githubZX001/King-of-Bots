package com.zx.backend.consumer;

import com.alibaba.fastjson.JSONObject;
import com.zx.backend.consumer.utils.Game;
import com.zx.backend.consumer.utils.JwtAuthentication;
import com.zx.backend.mapper.BotMapper;
import com.zx.backend.mapper.RecordMapper;
import com.zx.backend.mapper.UserMapper;
import com.zx.backend.pojo.Bot;
import com.zx.backend.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;


@Component
@ServerEndpoint("/websocket/{token}")  // 注意不要以'/'结尾
@Slf4j
public class WebSocketServer {

    // 使用一个线程安全的HashMap保存用户id和websocket通讯
    public final static ConcurrentHashMap<Integer, WebSocketServer> users = new ConcurrentHashMap<>();

    // websocket里面还是用session来建立通讯的
    private Session session;

    // 这里要知道这个连接是谁的
    private User user;

    // 因为websocket不是单例模式，不能直接使用注解进行注入，使用set方式注入
    public static UserMapper userMapper;
    public static RecordMapper recordMapper;
    public static RestTemplate restTemplate;
    public static BotMapper botMapper;

    public Game game = null;


    private String addPlayerUrl = "http://127.0.0.1:3001/player/add";

    private String removePlayerUrl = "http://127.0.0.1:3001/player/remove";

    @Autowired
    public void setBotMapper(BotMapper botMapper){
        WebSocketServer.botMapper = botMapper;
    }

    @Autowired
    public void setUserMapper(UserMapper userMapper) {
        WebSocketServer.userMapper = userMapper;
    }

    @Autowired
    public void setRecordMapper(RecordMapper recordMapper){
        WebSocketServer.recordMapper = recordMapper;
    }

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate){ WebSocketServer.restTemplate = restTemplate; }


    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) throws IOException {
        // 建立连接
        this.session = session;
        Integer userId = JwtAuthentication.getUserId(token);
        this.user = userMapper.selectById(userId);
        if(this.user != null){
            users.put(userId, this);
            log.info("connected! userId = " + userId);
        }else{
            this.session.close();
        }
    }

    @OnClose
    public void onClose() {
        // 关闭链接
        log.info("disconnected!");
        if(this.user != null){
            users.remove(this.user.getId());
        }
    }

    public static void startGame(Integer aId, Integer aBotId, Integer bId, Integer bBotId){
        User a = userMapper.selectById(aId);
        User b = userMapper.selectById(bId);
        Bot botA = botMapper.selectById(aBotId);
        Bot botB = botMapper.selectById(bBotId);

        Game game = new Game(
                13,
                14,
                20,
                a.getId(),
                botA,
                b.getId(),
                botB);
        game.createMap();

        game.start();

        if(users.get(a.getId()) != null){
            users.get(a.getId()).game = game;
        }

        if(users.get(b.getId()) != null){
            users.get(b.getId()).game = game;
        }

        JSONObject respGame = new JSONObject();
        respGame.put("a_id", game.getPlayerA().getId());
        respGame.put("a_sx", game.getPlayerA().getSx());
        respGame.put("a_sy", game.getPlayerA().getSy());
        respGame.put("b_id", game.getPlayerB().getId());
        respGame.put("b_sx", game.getPlayerB().getSx());
        respGame.put("b_sy", game.getPlayerB().getSy());
        respGame.put("map", game.getG());

        // 通过a的websocket给a返回json格式的信息
        JSONObject respA = new JSONObject();
        respA.put("event", "start-matching");
        respA.put("opponent_username", b.getUsername());
        respA.put("opponent_photo", b.getPhoto());
        respA.put("game", respGame);
        if(users.get(a.getId()) != null)
            users.get(a.getId()).sendMessage(respA.toJSONString());

        // 通过b的websocket给b返回json格式的信息
        JSONObject respB = new JSONObject();
        respB.put("event", "start-matching");
        respB.put("opponent_username", a.getUsername());
        respB.put("opponent_photo", a.getPhoto());
        respB.put("game", respGame);
        if(users.get(b.getId()) != null)
            users.get(b.getId()).sendMessage(respB.toJSONString());
    }

    private void startMatching(Integer botId){
        log.info("start matching!");
        LinkedMultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("user_id", this.user.getId().toString());
        data.add("rating", this.user.getRating().toString());
        data.add("bot_id", botId.toString());
        restTemplate.postForObject(addPlayerUrl, data, String.class);
    }

    private void stopMatching(){
        log.info("stop matching!");
        LinkedMultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("user_id", this.user.getId().toString());
        restTemplate.postForObject(removePlayerUrl, data, String.class);
    }

    private void move(Integer d){
        if(game.getPlayerA().getId().equals(user.getId())){
            if(game.getPlayerA().getBotId().equals(-1))
                game.setNextStepA(d);
        }else if(game.getPlayerB().getId().equals(user.getId())){
            if(game.getPlayerB().getBotId().equals(-1))
                game.setNextStepB(d);
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        // 从Client接收消息

        JSONObject jsonObject = JSONObject.parseObject(message);
        String event = jsonObject.getString("event");
        if("start_matching".equals(event)){
            startMatching(jsonObject.getInteger("bot_id"));
            //log.info("start matching message!");
        }else if("stop_matching".equals(event)){
            stopMatching();
            //log.info("stop matching message!");
        }else if("move".equals(event)){
            move(jsonObject.getInteger("direction"));
            String str = game.getPlayerA().getId().equals(user.getId()) ? "playerA" : "playerB";
            //log.info( str + "向" + jsonObject.getInteger("direction") + "方向移动");
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }

    public void sendMessage(String message){
        synchronized (this.session){
            try {
                this.session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

