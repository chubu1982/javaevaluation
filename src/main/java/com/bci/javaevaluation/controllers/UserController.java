package com.bci.javaevaluation.controllers;

import com.bci.javaevaluation.EmailAlreadyExistException;
import com.bci.javaevaluation.model.MessageDTO;
import com.bci.javaevaluation.model.UserDTO;
import com.bci.javaevaluation.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.HttpURLConnection;

@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping(value = "/sign-up", produces = {MediaType.APPLICATION_JSON_VALUE})
    public MappingJacksonValue signUp(@RequestBody UserDTO user) {
        try {
            UserDTO userDTO = userService.createNewUser(user);
            return new MappingJacksonValue(userDTO);
        } catch (Exception e) {
            MessageDTO messageDTO = new MessageDTO(e.getMessage(), HttpURLConnection.HTTP_BAD_REQUEST);
            return new MappingJacksonValue(messageDTO);
        }
    }

    @PostMapping(value = "/login", produces = {MediaType.APPLICATION_JSON_VALUE})
    public MappingJacksonValue login(@RequestBody UserDTO user) {
        try {
            UserDTO userDTO = userService.getUser(user);
            return new MappingJacksonValue(userDTO);
        } catch (Exception e) {
            MessageDTO messageDTO = new MessageDTO(e.getMessage(), HttpURLConnection.HTTP_BAD_REQUEST);
            return new MappingJacksonValue(messageDTO);
        }
    }
}
