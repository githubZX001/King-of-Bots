package com.zx.backend.consumer.utils;

import com.alibaba.fastjson.JSONObject;
import com.zx.backend.consumer.WebSocketServer;
import com.zx.backend.pojo.Bot;
import com.zx.backend.pojo.Record;
import com.zx.backend.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.LinkedMultiValueMap;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;


@Slf4j
public class Game extends Thread{
    private final Integer rows;
    private final Integer cols;
    private final Integer inner_walls_count;

    private final int[][] g;
    private final int [] dx = {-1, 0, 1, 0}, dy = {0, 1, 0, -1};

    private final Player playerA, playerB;
    private Integer nextStepA = null;
    private Integer nextStepB = null;

    private ReentrantLock lock = new ReentrantLock();
    private String status = "playing"; // playing -> finished
    private String loser = ""; // all: 平局 A：A输 B：B输

    private final static String addBotUrl = "http://127.0.0.1:3002/bot/add";

    public Game(
            Integer rows,
            Integer cols,
            Integer inner_walls_count,
            Integer idA,
            Bot botA,
            Integer idB,
            Bot botB){
        this.rows = rows;
        this.cols = cols;
        this.inner_walls_count = inner_walls_count;
        this.g = new int[rows][cols];

        Integer botIdA = -1, botIdB = -1;
        String botCodeA = "", botCodeB = "";

        if(botA != null){
            botIdA = botA.getId();
            botCodeA = botA.getContent();
        }
        if(botB != null){
            botIdB = botB.getId();
            botCodeB = botB.getContent();
        }

        this.playerA = new Player(idA, botIdA, botCodeA, rows - 2, 1, new ArrayList<>());
        this.playerB = new Player(idB, botIdB, botCodeB, 1, cols - 2, new ArrayList<>());
    }

    public Player getPlayerA() {
        return playerA;
    }

    public void setNextStepA(Integer nextStepA) {
        lock.lock();
        try {
            this.nextStepA = nextStepA;
        }finally {
            lock.unlock();
        }

    }

    public Player getPlayerB() {
        return playerB;
    }

    public void setNextStepB(Integer nextStepB) {
        lock.lock();
        try {
            this.nextStepB = nextStepB;
        }finally {
            lock.unlock();
        }
    }

    public int[][] getG(){
        return g;
    }

    private boolean check_connectivity(int sx, int sy, int tx, int ty){
        if(sx == tx && sy == ty) return true;

        g[sx][sy] = 1;

        for(int i = 0; i < 4; i++){
            int x = sx + dx[i], y = sy + dy[i];
            if(x >= 0 && x < this.rows && y >= 0 && y < this.cols && g[x][y] == 0){
                if(check_connectivity(x, y, tx, ty)) {
                    g[sx][sy] = 0;
                    return true;
                }
            }
        }
        g[sx][sy] = 0;
        return false;
    }
    private boolean draw(){
        for(int i = 0; i < this.rows; i++){
            for(int j = 0; j < this.cols; j++){
                // 0表示空地，1表示墙
                g[i][j] = 0;
            }
        }

        // 给左右加上障碍物
        for(int r = 0; r < this.rows; r++){
            g[r][0] = g[r][this.cols - 1] = 1;
        }

        // 给上下加上障碍物
        for(int c = 0; c < this.cols; c++){
            g[0][c] = g[this.rows - 1][c] = 1;
        }

        // 中间随机生成障碍物
        Random random = new Random();
        for(int i = 0; i < this.inner_walls_count / 2; i++){
            for(int j = 0; j < 1000; j++){
                int r = random.nextInt(this.rows);
                int c = random.nextInt(this.cols);

                // 如果该格子已经有障碍物的话
                if(g[r][c] == 1 || g[this.rows - 1 - r][this.cols - 1 - c] == 1) continue;

                // 如果左上角和右上角有障碍的的话
                if(r == this.rows - 2 && c == 1 || r == 1 && c == this.cols - 2) continue;

                // 因为这个是成一个中心对称的，所以要对位放置障碍物
                g[r][c] = g[this.rows - 1 - r][this.cols - 1 - c] = 1;
                break;
            }
        }

        return check_connectivity(this.rows - 2, 1, 1, this.cols - 2);
    }

    public void createMap(){
        for(int i = 0; i < 1000; i++){
            if(draw()) break;
        }
    }

    private String getInput(Player player){ // 将当前的局面信息，编码成字符串
        Player me, you;
        if(playerA.getId().equals(player.getId())){
            me = playerA;
            you = playerB;
        }else{
            you = playerA;
            me = playerB;
        }

        return getMapString() + "#" +
                me.getSx() + "#" +
                me.getSy() + "#(" +
                me.getStepsString() + ")#" +
                you.getSx() + "#" +
                you.getSy() + "#(" +
                you.getStepsString() + ")";
    }

    private void sendBotCode(Player player){
        if(player.getBotId() == -1) return;
        LinkedMultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("user_id", player.getId().toString());
        data.add("bot_code", player.getBotCode());
        data.add("input", getInput(player));

        WebSocketServer.restTemplate.postForObject(addBotUrl, data, String.class);
    }

    private boolean nextStep(){ // 等待两名玩家的下一步操作

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        sendBotCode(playerA);
        sendBotCode(playerB);

        for(int i = 0; i < 50; i++){
            try {
                Thread.sleep(100);
                // 这里涉及到了  一个线程通过setStepA/B来去写数据，一个线程通过add去读数据，可能产生一些读写问题，所以要使用锁
                lock.lock();
                try{
                    if(nextStepA != null && nextStepB != null) {
                        playerA.getSteps().add(nextStepA);
                        playerB.getSteps().add(nextStepB);
                        return true;
                    }
                }finally {
                    lock.unlock();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    private boolean check_valid(List<Cell> cellsA, List<Cell> cellsB){
        int n = cellsA.size();

        Cell cell = cellsA.get(n - 1);

        // 如果蛇头撞墙就返回false
        if(g[cell.x][cell.y] == 1) return false;

        // 如果蛇头撞身体就返回false
        for(int i = 0; i < n - 1; i++){
            if(cellsA.get(i).x == cell.x && cellsA.get(i).y == cell.y){
                return false;
            }
        }

        // 如果蛇头撞B的身体返回false
        for(int i = 0; i < n - 1; i++){
            if(cellsB.get(i).x == cell.x && cellsB.get(i).y == cell.y){
                return false;
            }
        }
        return true;
    }

    private void judge(){ // 判断两名玩家下一步操作是否合法
        List<Cell> cellsA = playerA.getCells();
        List<Cell> cellsB = playerB.getCells();

        boolean validA = check_valid(cellsA, cellsB);
        boolean validB = check_valid(cellsB, cellsA);

        if(!validA || !validB){
            status = "finished";
            if(!validA && !validB){
                loser = "all";
            }else if(!validA){
                loser = "A";
            }else if(!validB){
                loser = "B";
            }
        }

    }

    private void sendAllMessage(String message){
        if(WebSocketServer.users.get(playerA.getId()) != null){
            WebSocketServer.users.get(playerA.getId()).sendMessage(message);
        }
        if(WebSocketServer.users.get(getPlayerB().getId()) != null){
            WebSocketServer.users.get(playerB.getId()).sendMessage(message);
        }

    }

    private void sendMove(){ // 向两个Client传递移动信息
        lock.lock();
        try {
            // 将移动信息发送了
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("event", "move");
            jsonObject.put("a_direction", nextStepA);
            jsonObject.put("b_direction", nextStepB);
            // 清空两个玩家的下一步
            sendAllMessage(jsonObject.toJSONString());
            nextStepA = nextStepB = null;
        }finally {
            lock.unlock();
        }

    }

    private String getMapString(){
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                res.append(g[i][j]);
            }
        }
        return res.toString();
    }

    private void updateUserRating(Player player, Integer rating){
        User user = WebSocketServer.userMapper.selectById(player.getId());
        user.setRating(rating);
        WebSocketServer.userMapper.updateById(user);
    }

    private void saveToDatebase(){

        Integer ratingA = WebSocketServer.userMapper.selectById(playerA.getId()).getRating();
        Integer ratingB = WebSocketServer.userMapper.selectById(playerB.getId()).getRating();

        if("A".equals(loser)){
            ratingA -= 2;
            ratingB += 5;
        }else if ("B".equals(loser)){
            ratingA += 5;
            ratingB -= 2;
        }

        updateUserRating(playerA, ratingA);
        updateUserRating(playerB, ratingB);

        Record record = new Record(
                null,
                playerA.getId(),
                playerA.getSx(),
                playerA.getSy(),
                playerB.getId(),
                playerB.getSx(),
                playerB.getSy(),
                playerA.getStepsString(),
                playerB.getStepsString(),
                getMapString(),
                loser,
                new Date()
        );

        WebSocketServer.recordMapper.insert(record);
    }

    private void sendResult(){ // 向两个Client公布结果
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("event", "result");
        jsonObject.put("loser", loser);
        saveToDatebase();
        sendAllMessage(jsonObject.toJSONString());
    }


    @Override
    public void run() {
        for (int i = 0; i < 1000; i++) {
            if(nextStep()){ // 是否获取两条蛇的下一步操作
                judge();
                if("playing".equals(status)){
                    sendMove();
                }else{
                    sendResult();
                    break;
                }
            }else{
                status = "finished";
                // 这里加锁是为了避免，当5秒中过去了，执行到这里了才写入下一步的操作，不合理
                lock.lock();
                try {
                    if(nextStepA == null && nextStepB == null){
                        loser = "all";
                    }else if(nextStepA == null){
                        loser = "A";
                    }else if(nextStepB == null){
                        loser = "B";
                    }
                }finally {
                    lock.unlock();
                }
                sendResult();
                break;
            }
        }
    }
}
