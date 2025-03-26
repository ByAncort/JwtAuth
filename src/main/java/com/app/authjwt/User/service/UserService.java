package com.app.authjwt.User.service;

import com.app.authjwt.User.Repository.UserRepository;
import com.app.authjwt.payload.request.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public RegisterRequest getInfoProfile() {

    }
}
