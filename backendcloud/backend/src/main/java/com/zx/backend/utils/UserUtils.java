package com.zx.backend.utils;

import com.zx.backend.pojo.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;


public class UserUtils {
    public static User getUser(){
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        UserDetailsUtils userUtils = (UserDetailsUtils) usernamePasswordAuthenticationToken.getPrincipal();
        return userUtils.getUser();
    }
}
