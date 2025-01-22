package com.example.bemangahub.controller;

import com.example.bemangahub.ServiceResult;
import com.example.bemangahub.config.AppConstant;
import com.example.bemangahub.dto.res.UserInfo;
import com.example.bemangahub.service.IBaseRedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/api-be/v1/redis")
public class RedisController {

    @Autowired
    private IBaseRedisService baseRedisService;

    @PostMapping
    public void addNew() {
        UserInfo userInfo = new UserInfo();
        userInfo.setId(334);
        userInfo.setEmail("mail2");
        baseRedisService.hashSet("prod", "u2", userInfo);
    }

    @PostMapping("/getOne")
    ResponseEntity<?> getOne() {
        Object result = baseRedisService.hashGetByFieldPrefix("prod", "u1");
        if (result != null) {
            return ResponseEntity.ok(new ServiceResult(0,"succes",result));
        } else {
            return ResponseEntity.status(AppConstant.NOT_FOUND).body(new ServiceResult(0,"fail"));
        }
    }
}
