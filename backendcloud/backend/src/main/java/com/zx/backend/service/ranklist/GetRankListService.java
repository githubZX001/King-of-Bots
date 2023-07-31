package com.zx.backend.service.ranklist;

import com.alibaba.fastjson.JSONObject;


public interface GetRankListService {
    JSONObject getRankList(Integer page);
}
