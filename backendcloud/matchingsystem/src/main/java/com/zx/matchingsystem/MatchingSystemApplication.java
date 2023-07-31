package com.zx.matchingsystem;

import com.zx.matchingsystem.service.impl.MatchingServiceImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author li
 * @data 2023/3/14
 * @time 17:21
 */

@SpringBootApplication
public class MatchingSystemApplication {
    public static void main(String[] args) {
        // 在启动前启动而启动
        MatchingServiceImpl.matchingPool.start();
        SpringApplication.run(MatchingSystemApplication.class, args);
    }
}
