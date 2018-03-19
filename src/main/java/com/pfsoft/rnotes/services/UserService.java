package com.pfsoft.rnotes.services;


import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class UserService {

//    @Autowired
//    private UserDao userDao;
//
//    @PostConstruct
//    public void init() {
//
//        userDao.findByUsername("user").ifPresent(user -> {
//            user.setPassword(new BCryptPasswordEncoder().encode("123"));
//            userDao.save(user);
//        });

//        if (!userDao.findByUsername("user").isPresent()) {
//            userDao.save(User.builder()
//                    .username("user")
//                    .password("password")
//                    .authorities(ImmutableList.of(Role.USER))
//                    .accountNonExpired(true)
//                    .accountNonLocked(true)
//                    .credentialsNonExpired(true)
//                    .enabled(true)
//                    .build());
//        }
    }

//    @Override
//    public UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
//        return userDao.findByUsername(username).orElseThrow(() ->
//                new UsernameNotFoundException("user " + username + " was not found!"));
//    }
//}
