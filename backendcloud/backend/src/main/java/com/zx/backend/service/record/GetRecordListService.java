package com.zx.backend.service.record;

import com.alibaba.fastjson.JSONObject;


public interface GetRecordListService {
    public JSONObject getList(Integer page);
}
