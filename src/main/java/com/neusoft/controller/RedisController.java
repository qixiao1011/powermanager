package com.neusoft.controller;

import com.neusoft.bean.Manager;
import com.neusoft.dao.ManagerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("redis")
public class RedisController {
    @Autowired
    RedisTemplate template;
    @Autowired
    ManagerMapper mapper;

    @RequestMapping("manager")
    @ResponseBody
    public String initRedis(){
        List<Manager> list=mapper.selectAll(new HashMap());

        ValueOperations<String,Manager> valueOperations=template.opsForValue();
        for (Manager m : list){
            valueOperations.set(m.getManagerPhone(),m);
        }
        return "OK";
        //            HashOperations hashOperations=template.opsForHash();
//        HashMap map=new HashMap();
//            map.put("1","瓜皮");
//            map.put("2","大瓜批");
//            map.put("3","瓜皮");
//            hashOperations.putAll("testhash",map);
//
//            hashOperations.put("testhash","4","gau");
//            Object obj =hashOperations.get("testhash","1");
//
//        SetOperations setOperations=template.opsForSet();
//        Set<String> set=new HashSet<>();
//        set.add("李四");
//        set.add("王五");
//
//        setOperations.add("users","李四","张三","王五");

    }
}
