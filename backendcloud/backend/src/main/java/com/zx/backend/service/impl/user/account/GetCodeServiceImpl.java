package com.zx.backend.service.impl.user.account;

import com.zx.backend.service.user.account.GetCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author li
 * @data 2023/3/22
 * @time 16:18
 */
@Service
public class GetCodeServiceImpl implements GetCodeService {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private final static Random random = new Random();
    @Override
    public Map<String, String> getCode() {
        String uuid = UUID.randomUUID().toString();
        String key = "login_user:" + uuid;
        StringBuilder code = new StringBuilder();

        for(int i = 0; i < 4; i++) {
            code.append(random.nextInt(10));
        }

        redisTemplate.opsForValue().set(key, code.toString(), 1, TimeUnit.MINUTES);
        Map<String, String> data = new HashMap<>();

        data.put("error_message", "success");
        data.put("uuid", uuid);
        data.put("code", code.toString());

        return data;
    }
}
