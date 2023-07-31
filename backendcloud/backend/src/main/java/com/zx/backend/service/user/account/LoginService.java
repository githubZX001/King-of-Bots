package com.zx.backend.service.user.account;

import java.util.Map;


public interface LoginService {
    public Map<String, String> login(String username, String password, String code, String uuid);
}
