package com.zx.backend.controller.record;

import com.alibaba.fastjson.JSONObject;
import com.zx.backend.service.record.GetRecordListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/record")
public class GetRecordListController {

    @Autowired
    private GetRecordListService getRecordListService;

    @GetMapping("/getList")
    public JSONObject getList(@RequestParam Map<String, String> data){
        Integer page = Integer.parseInt(data.get("page"));
        return getRecordListService.getList(page);
    }
}
