package com.zx.backend.service.impl.record;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zx.backend.mapper.RecordMapper;
import com.zx.backend.mapper.UserMapper;
import com.zx.backend.pojo.Record;
import com.zx.backend.pojo.User;
import com.zx.backend.service.record.GetRecordListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.util.LinkedList;
import java.util.List;


@Service
public class GetRecordListServiceImpl implements GetRecordListService {
    @Autowired
    private RecordMapper recordMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public JSONObject getList(Integer page) {
        IPage<Record> recordPage = new Page<>(page, 10);
        LambdaQueryWrapper<Record> lqw = new LambdaQueryWrapper<>();
        lqw.orderByDesc(Record::getId);
        List<Record> records = recordMapper.selectPage(recordPage, lqw).getRecords();
        JSONObject resp = new JSONObject();
        LinkedList<JSONObject> items = new LinkedList<>();

        for (Record record : records) {
            User userA = userMapper.selectById(record.getAId());
            User userB = userMapper.selectById(record.getBId());
            JSONObject item = new JSONObject();
            item.put("a_photo", userA.getPhoto());
            item.put("a_username", userA.getUsername());
            item.put("b_photo", userB.getPhoto());
            item.put("b_username", userB.getUsername());

            String result = "平局";
            if("A".equals(record.getLoser())) result = "B胜";
            else if ("B".equals(record.getLoser())) result = "A胜";
            item.put("result", result);
            item.put("record", record);

            items.add(item);
        }

        resp.put("records", items);
        resp.put("records_count", recordMapper.selectCount(null));

        return resp;
    }
}