package com.zx.backend.service.impl.user.bot;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zx.backend.mapper.BotMapper;
import com.zx.backend.pojo.Bot;
import com.zx.backend.pojo.User;
import com.zx.backend.service.user.bot.GetListService;
import com.zx.backend.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author li
 * @data 2023/3/8
 * @time 12:16
 */
@Service
public class GetListServiceImpl implements GetListService {
    @Autowired
    private BotMapper botMapper;
    @Override
    public List<Bot> getBots() {
        User user = UserUtils.getUser();
        LambdaQueryWrapper<Bot> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Bot::getUserId, user.getId());
        List<Bot> bots = botMapper.selectList(lqw);
        return bots;
    }
}
