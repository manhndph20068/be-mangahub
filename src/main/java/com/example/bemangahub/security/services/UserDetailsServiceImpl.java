package com.example.bemangahub.security.services;

import com.example.bemangahub.entity.Account;
import com.example.bemangahub.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private AccountRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Account> account = repository.findById(Integer.parseInt(username));
        return account.map(UserDetailsImpl::new)
                .orElseThrow(() -> new UsernameNotFoundException("user not found " + username));
    }

}
