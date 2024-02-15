package com.example.bemangahub.repository;

import com.example.bemangahub.dto.res.UserInfo;
import com.example.bemangahub.entity.Account;
import jakarta.persistence.ColumnResult;
import jakarta.persistence.ConstructorResult;
import jakarta.persistence.SqlResultSetMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {

    @Query(nativeQuery = true, name = "UserCerdentialInfoMappingQuery")
    Optional<UserInfo> findCerdentialByEmail(@Param("email") String email);

    @Query("SELECT a FROM Account a JOIN a.role r WHERE a.email = :email")
    Optional<Account> findByEmail(@Param("email") String email);
}
