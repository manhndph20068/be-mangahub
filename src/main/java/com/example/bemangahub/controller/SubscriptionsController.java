package com.example.bemangahub.controller;

import com.example.bemangahub.ServiceResult;
import com.example.bemangahub.config.AppConstant;
import com.example.bemangahub.dto.req.SubscriptionsReq;
import com.example.bemangahub.service.ISubscriptionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/subscriptions")
public class SubscriptionsController {
    @Autowired
    private ISubscriptionsService iSubscriptionsService;

    @PostMapping("/findSubscriptionsByAccountId")
    ResponseEntity<?> findSubscriptionsByAccountId(@RequestBody SubscriptionsReq subscriptionsReq) {
        Boolean isExits = iSubscriptionsService.accountExits(subscriptionsReq.getIdAccount());
        if (!isExits) {
            return ResponseEntity.badRequest().body(new ServiceResult<>(
                    AppConstant.NOT_FOUND,
                    "Account not found",
                    null
            ));
        }
        return ResponseEntity.ok(new ServiceResult<>(
                AppConstant.SUCCESS,
                "Get success",
                iSubscriptionsService.findSubscriptionsByAccountId(subscriptionsReq.getIdAccount()))
        );
    }

    @PostMapping("/saveSubscription")
    ResponseEntity<?> saveSubscription(@RequestBody SubscriptionsReq subscriptionsReq) {
        Boolean isExits = iSubscriptionsService.accountExits(subscriptionsReq.getIdAccount());
        if (!isExits) {
            return ResponseEntity.badRequest().body(new ServiceResult<>(
                    AppConstant.NOT_FOUND,
                    "Account not found",
                    null
            ));
        }
        Boolean isSave = iSubscriptionsService.saveSubscription(subscriptionsReq);
        if (!isSave) {
            return ResponseEntity.badRequest().body(new ServiceResult<>(
                    AppConstant.NOT_FOUND,
                    "Comic is already subscribed",
                    null
            ));
        }
        return ResponseEntity.ok(new ServiceResult<>(
                AppConstant.SUCCESS,
                "Save success",
                null
        ));
    }

    @PostMapping("/deleteSubscription")
    ResponseEntity<?> deleteSubscription(@RequestBody SubscriptionsReq subscriptionsReq) {
        Boolean isExits = iSubscriptionsService.accountExits(subscriptionsReq.getIdAccount());
        if (!isExits) {
            return ResponseEntity.badRequest().body(new ServiceResult<>(
                    AppConstant.NOT_FOUND,
                    "Account not found",
                    null
            ));
        }
        Boolean isDelete = iSubscriptionsService.deleteSubscription(subscriptionsReq);
        if (!isDelete) {
            return ResponseEntity.badRequest().body(new ServiceResult<>(
                    AppConstant.NOT_FOUND,
                    "Comic is not subscribed",
                    null
            ));
        }
        return ResponseEntity.ok(new ServiceResult<>(
                AppConstant.SUCCESS,
                "Delete success",
                null
        ));
    }
}
