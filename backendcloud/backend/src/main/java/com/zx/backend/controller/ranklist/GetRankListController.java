package com.zx.backend.controller.ranklist;

import com.alibaba.fastjson.JSONObject;
import com.zx.backend.service.ranklist.GetRankListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/ranklist")
public class GetRankListController {
    @Autowired
    private GetRankListService getRankListService;

    @GetMapping("/getList")
    public JSONObject getList(@RequestParam Map<String, String> data){
        Integer page = Integer.parseInt(data.get("page"));
        return getRankListService.getRankList(page);
    }
}
