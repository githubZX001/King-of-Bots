package com.zx.backend.service.impl.user.bot;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zx.backend.mapper.BotMapper;
import com.zx.backend.pojo.Bot;
import com.zx.backend.pojo.User;
import com.zx.backend.service.user.bot.AddService;
import com.zx.backend.utils.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author li
 * @data 2023/3/8
 * @time 12:16
 */
@Service
@Slf4j
public class AddServiceImpl implements AddService {

    @Autowired
    private BotMapper botMapper;

    @Override
    public Map<String, String> addBot(Map<String, String> map) {
        // 获取上下文中的用户信息
        User user = UserUtils.getUser();

        String title = map.get("title");
        String description = map.get("description");
        String content = map.get("content");

        Map<String, String> res = new HashMap<>();

        if(title == null || title.length() == 0){
            res.put("error_message", "标题不能为空");
            return res;
        }

        if(title.length() > 100){
            res.put("error_message", "标题长度太长");
            return res;
        }
        if(description == null || description.length() == 0){
            description = "这个用户什么也没写。";
        }

        if(description.length() > 300){
            res.put("error_message", "描述太长了");
            return res;
        }

        if(content == null || content.length() == 0){
            res.put("error_message", "代码不能为空");
            return res;
        }

        if(content.length() > 10000){
            res.put("error_message", "代码长度不能超过10000");
            return res;
        }

        LambdaQueryWrapper<Bot> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Bot::getUserId, user.getId());
        if(botMapper.selectCount(lqw) >= 10){
            res.put("error_message", "每个用户最多只能创建10个bot");
            return res;
        }

        Date now = new Date();
        Bot bot = new Bot(null, user.getId(), title, description, content, now, now);
        botMapper.insert(bot);
        log.info("新增bot成功");

        res.put("error_message", "success");
        return res;
    }
}
