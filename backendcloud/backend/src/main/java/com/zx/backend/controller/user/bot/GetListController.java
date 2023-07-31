package com.zx.backend.controller.user.bot;

import com.zx.backend.pojo.Bot;
import com.zx.backend.service.user.bot.GetListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/user/bot")
public class GetListController {

    @Autowired
    private GetListService getListService;

    @GetMapping("/getlist")
    public List<Bot> getlist(){
        return getListService.getBots();
    }
}
