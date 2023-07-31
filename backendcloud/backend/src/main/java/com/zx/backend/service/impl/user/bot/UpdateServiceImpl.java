package com.zx.backend.service.impl.user.bot;

import com.zx.backend.mapper.BotMapper;
import com.zx.backend.pojo.Bot;
import com.zx.backend.pojo.User;
import com.zx.backend.service.user.bot.UpdateService;
import com.zx.backend.utils.UserUtils;
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
public class UpdateServiceImpl implements UpdateService {

    @Autowired
    private BotMapper botMapper;

    @Override
    public Map<String, String> updateBot(Map<String, String> map) {
        User user = UserUtils.getUser();

        int botId = Integer.parseInt( map.get("bot_id"));
        Bot bot = botMapper.selectById(botId);
        Map<String, String> res = new HashMap<>();
        if(bot == null){
            res.put("error_message", "Bot不存在");
            return res;
        }

        if(!bot.getUserId().equals(user.getId())){
            res.put("error_message", "没有权限修改");
            return res;
        }

        String title = map.get("title");
        String description = map.get("description");
        String content = map.get("content");

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

        Date date = new Date();
        Bot newBot = new Bot(bot.getId(), bot.getUserId(), title, description, content, bot.getCreatetime(), date);

        botMapper.updateById(newBot);
        res.put("error_message", "success");
        return res;
    }
}
