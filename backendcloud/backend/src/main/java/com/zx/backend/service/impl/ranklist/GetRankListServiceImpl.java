package com.zx.backend.service.impl.ranklist;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zx.backend.mapper.UserMapper;
import com.zx.backend.pojo.User;
import com.zx.backend.service.ranklist.GetRankListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;


@Service
public class GetRankListServiceImpl implements GetRankListService {
    @Autowired
    private UserMapper userMapper;
    @Override
    public JSONObject getRankList(Integer page) {
        Page<User> userPage = new Page<>(page, 10);
        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
        lqw.orderByDesc(User::getRating);
        List<User> records = userMapper.selectPage(userPage, lqw).getRecords();
        JSONObject resp = new JSONObject();
        for (User record : records) {
            record.setPassword("");
        }
        resp.put("users", records);
        resp.put("users_count", userMapper.selectCount(null));
        return resp;
    }
}
