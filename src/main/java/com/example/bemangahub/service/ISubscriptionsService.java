package com.example.bemangahub.service;

import com.example.bemangahub.dto.req.SubscriptionsReq;
import com.example.bemangahub.dto.res.SubscriptionsRes;

import java.util.List;

public interface ISubscriptionsService {
    List<SubscriptionsRes> findSubscriptionsByAccountId(Integer accountId);

    Boolean accountExits(Integer accountId);

    Boolean saveSubscription(SubscriptionsReq subscriptionsReq);

    Boolean deleteSubscription(SubscriptionsReq subscriptionsReq);
}
