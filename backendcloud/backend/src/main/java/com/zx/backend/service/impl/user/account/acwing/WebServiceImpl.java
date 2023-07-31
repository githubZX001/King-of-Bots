package com.zx.backend.service.impl.user.account.acwing;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zx.backend.mapper.UserMapper;
import com.zx.backend.pojo.User;
import com.zx.backend.service.impl.user.account.acwing.utils.HttpClientUtil;
import com.zx.backend.service.user.account.acwing.WebService;
import com.zx.backend.utils.JwtUtil;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.Duration;
import java.util.LinkedList;
import java.util.List;
import java.util.NavigableMap;
import java.util.Random;

/**
 * @author li
 * @data 2023/3/20
 * @time 12:58
 */
@Service
public class WebServiceImpl implements WebService {
    private final static String appId = "4318";
    private final static String appSecret = "6aa3ec5adbae428b9c69a4f78409ccc5";
    private final static String redirectUri = "https://app4318.acapp.acwing.com.cn/user/account/acwing/web/receive_code";
    private final static String applyAccessTokenUrl = "https://www.acwing.com/third_party/api/oauth2/access_token";
    private final static String getUserInfoUrl = "https://www.acwing.com/third_party/api/meta/identity/getinfo";

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public JSONObject applyCode() {
        JSONObject resp = new JSONObject();
        String encodeUrl = "";
        try {
            encodeUrl = URLEncoder.encode(redirectUri, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            resp.put("result", "failed");
            return resp;
        }
        resp.put("scope", "userinfo");
        Random random = new Random();
        StringBuilder state = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            state.append((char)random.nextInt(10) + '0');
        }
        resp.put("result", "success");

        redisTemplate.opsForValue().set(state.toString(), "true");
        redisTemplate.expire(state.toString(), Duration.ofMinutes(10));

        String applyCodeUrl = "https://www.acwing.com/third_party/api/oauth2/web/authorize/?appid=" + appId
                + "&redirect_uri=" + encodeUrl + "&scope=" + "userinfo" + "&state=" + state;

        resp.put("apply_code_url", applyCodeUrl);
        return resp;
    }

    @Override
    public JSONObject receiveCode(String code, String state) {
        JSONObject resp = new JSONObject();
        resp.put("result", "failed");

        // 判断一下数据的可靠性
        if(code == null || state == null) return resp;

        if(Boolean.FALSE.equals(redisTemplate.hasKey(state))) return resp;
        redisTemplate.delete(state);

        // 设置参数
        LinkedList<NameValuePair> nameValuePairs = new LinkedList<>();
        nameValuePairs.add(new BasicNameValuePair("appid", appId));
        nameValuePairs.add(new BasicNameValuePair("secret", appSecret));
        nameValuePairs.add(new BasicNameValuePair("code", code));

        // 请求access_token
        String s = HttpClientUtil.get(applyAccessTokenUrl, nameValuePairs);
        if(s == null) return resp;

        // 取出信息
        JSONObject getResp = JSONObject.parseObject(s);
        String accessToken = getResp.getString("access_token");
        String openid = getResp.getString("openid");
        if(accessToken == null || openid == null) return resp;

        // 查询数据库中的openid的用户
        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
        lqw.eq(User::getOpenid, openid);
        List<User> users = userMapper.selectList(lqw);

        // 如果数据库中有要获取的用户就直接取出来即可
        if(!users.isEmpty()){
            User user = users.get(0);
            String jwt = JwtUtil.createJWT(user.getId().toString());
            resp.put("jwt_token", jwt);
            resp.put("result", "success");
            return resp;
        }

        // 如果没有的话，就要请求用户的信息，并保存到数据库中
        nameValuePairs = new LinkedList<>();
        nameValuePairs.add(new BasicNameValuePair("access_token", accessToken));
        nameValuePairs.add(new BasicNameValuePair("openid", openid));

        String info = HttpClientUtil.get(getUserInfoUrl, nameValuePairs);
        if(info == null) return resp;

        JSONObject jsonObject = JSONObject.parseObject(info);
        String username = jsonObject.getString("username");
        String photo = jsonObject.getString("photo");

        if(username == null || photo == null) return resp;

        Random random = new Random();

        for(int i = 0; i < 100; i++){
            QueryWrapper<User> usernamequeryWrapper = new QueryWrapper<>();
            usernamequeryWrapper.eq("username", username);
            if(userMapper.selectList(usernamequeryWrapper).isEmpty()) break;
            username += (char)(random.nextInt(10) + '0');
            if(i == 99) return resp;
        }

        User user = new User(
                null,
                username,
                null,
                photo,
                1500,
                openid
        );

        userMapper.insert(user);
        String jwt = JwtUtil.createJWT(user.getId().toString());
        resp.put("result", "success");
        resp.put("jwt_token", jwt);

        return resp;
    }
}
