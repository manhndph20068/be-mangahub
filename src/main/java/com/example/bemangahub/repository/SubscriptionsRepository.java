package com.example.bemangahub.repository;

import com.example.bemangahub.dto.res.SubscriptionsRes;
import com.example.bemangahub.entity.Subscriptions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface SubscriptionsRepository extends JpaRepository<Subscriptions, Integer> {
    Optional<Subscriptions> findByIdComicAndAccount_Id(String idComic, Integer accountId);

    @Query(nativeQuery = true, name = "SubscriptionsResponseByAccountIdMappingQuery")
    List<SubscriptionsRes> findSubByAccountId(@Param("accountId") Integer accountId);


}
