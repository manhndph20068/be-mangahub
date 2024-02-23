package com.example.bemangahub.repository;

import com.example.bemangahub.dto.res.UserInfo;
import com.example.bemangahub.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {

    @Query(nativeQuery = true, name = "UserCerdentialInfoMappingQuery")
    Optional<UserInfo> findCerdentialByEmail(@Param("email") String email);

    @Query(nativeQuery = true, name = "UserSocialInfoMappingQuery")
    Optional<UserInfo> findSocialByEmail(@Param("email") String email, @Param("type") String type);

    @Query("SELECT a FROM Account a JOIN a.role r JOIN a.type t WHERE a.email = :email AND t.name = :typeName")
    Optional<Account> findByEmailAndTypeName(@Param("email") String email, @Param("typeName") String typeName);
}
