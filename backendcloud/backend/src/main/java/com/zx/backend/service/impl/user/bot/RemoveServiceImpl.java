package com.zx.backend.service.impl.user.bot;

import com.zx.backend.mapper.BotMapper;
import com.zx.backend.pojo.Bot;
import com.zx.backend.pojo.User;
import com.zx.backend.service.user.bot.RemoveService;
import com.zx.backend.utils.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author li
 * @data 2023/3/8
 * @time 12:16
 */
@Service
@Slf4j
public class RemoveServiceImpl implements RemoveService {
    @Autowired
    private BotMapper botMapper;

    @Override
    public Map<String, String> removeBot(Map<String, String> map) {
        User user = UserUtils.getUser();

        int botId = Integer.parseInt( map.get("bot_id"));
        Bot bot = botMapper.selectById(botId);
        Map<String, String> res = new HashMap<>();
        if(bot == null){
            res.put("error_message", "Bot不存在或已经被删除");
            return res;
        }

        if(!bot.getUserId().equals(user.getId())){
            res.put("error_message", "没有权限删除");
            return res;
        }

        botMapper.deleteById(botId);
        res.put("error_message", "success");
        log.info("删除bot成功");
        return res;
    }
}
