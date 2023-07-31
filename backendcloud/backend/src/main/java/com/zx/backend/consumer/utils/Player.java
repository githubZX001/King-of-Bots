package com.zx.backend.consumer.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.security.cert.CertSelector;
import java.util.ArrayList;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Player {
    private Integer id;
    private Integer botId; // -1表示亲自出马，否则表示用ai打
    private String botCode;
    private Integer sx;
    private Integer sy;
    private List<Integer> steps;

    private boolean check_tail_increasing(int steps){// 检验当前回合蛇的长度是否增加
        if(steps <= 10) return true;
        return steps % 3 == 1;
    }
    // 获取蛇的身体，是因为这些判断谁输谁赢的不能让用户自己去做，统一放到后端去判断
    // 蛇的身体碰到障碍物就算失败
    public List<Cell> getCells(){
        List<Cell> res = new ArrayList<>();
        int dx[] = {-1, 0, 1, 0}, dy[] = {0, 1, 0, -1};
        int x = sx, y = sy;
        int step = 0;
        res.add(new Cell(x, y));
        for(int d : steps){
            x += dx[d];
            y += dy[d];
            res.add(new Cell(x, y));
            step ++;
            if(!check_tail_increasing(++ step)){
                res.remove(0);
            }
        }
        return res;
    }


    public String getStepsString(){
        StringBuilder res = new StringBuilder();
        for(int d : steps){
            res.append(d);
        }
        return res.toString();
    }
}
