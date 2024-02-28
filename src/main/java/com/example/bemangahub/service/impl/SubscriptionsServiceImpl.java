package com.example.bemangahub.service.impl;

import com.example.bemangahub.dto.req.SubscriptionsReq;
import com.example.bemangahub.dto.res.SubscriptionsRes;
import com.example.bemangahub.entity.Account;
import com.example.bemangahub.entity.Subscriptions;
import com.example.bemangahub.repository.AccountRepository;
import com.example.bemangahub.repository.SubscriptionsRepository;
import com.example.bemangahub.service.ISubscriptionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SubscriptionsServiceImpl implements ISubscriptionsService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private SubscriptionsRepository subscriptionsRepository;

    @Override
    public List<SubscriptionsRes> findSubscriptionsByAccountId(Integer accountId) {
        Optional<Account> accountOptional = accountRepository.findById(accountId);
            List<SubscriptionsRes> subscriptionsResList = subscriptionsRepository.findSubByAccountId(accountOptional.get().getId());
            return subscriptionsResList;
    }

    @Override
    public Boolean accountExits(Integer accountId) {
        Optional<Account> accountOptional = accountRepository.findById(accountId);
        if (accountOptional.isPresent()) {
            return true;
        }else {
            return false;
        }
    }

    @Override
    public Boolean saveSubscription(SubscriptionsReq subscriptionsReq) {
        Optional<Subscriptions> subscriptionsOptional =
                subscriptionsRepository.findByIdComicAndAccount_Id(subscriptionsReq.getIdComic(),subscriptionsReq.getIdAccount());
        if (subscriptionsOptional.isPresent()) {
            return false;
        }
        Subscriptions subscriptions = new Subscriptions();
        subscriptions.setIdComic(subscriptionsReq.getIdComic());
        subscriptions.setAccount(accountRepository.findById(subscriptionsReq.getIdAccount()).get());
        subscriptions.setImage(subscriptionsReq.getImage());
        subscriptions.setName(subscriptionsReq.getName());
        subscriptionsRepository.save(subscriptions);
        return true;
    }

    @Override
    public Boolean deleteSubscription(SubscriptionsReq subscriptionsReq) {
        Optional<Subscriptions> subscriptionsOptional =
                subscriptionsRepository.findByIdComicAndAccount_Id(subscriptionsReq.getIdComic(),subscriptionsReq.getIdAccount());
        if (subscriptionsOptional.isPresent()) {
            subscriptionsRepository.delete(subscriptionsOptional.get());
            return true;
        }else {
            return false;
        }


    }
}
