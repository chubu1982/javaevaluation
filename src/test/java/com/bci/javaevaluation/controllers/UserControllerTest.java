package com.bci.javaevaluation.controllers;

import com.bci.javaevaluation.model.MessageDTO;
import com.bci.javaevaluation.model.UserDTO;
import com.bci.javaevaluation.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.converter.json.MappingJacksonValue;

import java.net.HttpURLConnection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSignUp_Success() throws Exception {
        UserDTO userDTO = new UserDTO();
        UserDTO returnedUserDTO = new UserDTO();

        when(userService.createNewUser(any(UserDTO.class))).thenReturn(returnedUserDTO);

        MappingJacksonValue response = userController.signUp(userDTO);

        assertEquals(returnedUserDTO, response.getValue());
    }

    @Test
    public void testSignUp_Failure() throws Exception {
        UserDTO userDTO = new UserDTO();
        String errorMessage = "El email proporcionado no es válido";

        when(userService.createNewUser(any(UserDTO.class)))
                .thenThrow(new IllegalArgumentException(errorMessage));

        MappingJacksonValue response = userController.signUp(userDTO);

        MessageDTO messageDTO = (MessageDTO) response.getValue();
        assertEquals(errorMessage, messageDTO.getError().get(0).getDetail());
        assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, messageDTO.getError().get(0).getCodigo());
    }

    @Test
    public void testLogin_Success() throws Exception {
        UserDTO userDTO = new UserDTO();
        UserDTO returnedUserDTO = new UserDTO();

        when(userService.getUser(any(UserDTO.class))).thenReturn(returnedUserDTO);

        MappingJacksonValue response = userController.login(userDTO);

        assertEquals(returnedUserDTO, response.getValue());
    }

    @Test
    public void testLogin_Failure() throws Exception {
        UserDTO userDTO = new UserDTO();
        String errorMessage = "El email o la contraseña no son validos";

        when(userService.getUser(any(UserDTO.class)))
                .thenThrow(new IllegalArgumentException(errorMessage));

        MappingJacksonValue response = userController.login(userDTO);

        MessageDTO messageDTO = (MessageDTO) response.getValue();
        assertEquals(errorMessage, messageDTO.getError().get(0).getDetail());
        assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, messageDTO.getError().get(0).getCodigo());
    }
}
