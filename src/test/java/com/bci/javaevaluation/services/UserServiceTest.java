package com.bci.javaevaluation.services;

import com.bci.javaevaluation.EmailAlreadyExistException;
import com.bci.javaevaluation.domain.User;
import com.bci.javaevaluation.model.PhoneDTO;
import com.bci.javaevaluation.model.UserDTO;
import com.bci.javaevaluation.repositories.UserPhoneRepository;
import com.bci.javaevaluation.repositories.UserRepository;
import com.bci.javaevaluation.util.EncryptionUtil;
import com.bci.javaevaluation.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserPhoneRepository userPhoneRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private EncryptionUtil encryptionUtil;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateNewUser_Success() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("test@example.com");
        userDTO.setPassword("Martin12");
        userDTO.setName("Test User");
        PhoneDTO phoneDTO = new PhoneDTO();
        phoneDTO.setNumber(1234567890);
        phoneDTO.setCitycode(1);
        phoneDTO.setContrycode("1");
        userDTO.setPhones(List.of(phoneDTO));

        User user = new User();
        user.setId(UUID.randomUUID());

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(encryptionUtil.encrypt(anyString())).thenReturn("encryptedPassword");
        when(jwtUtil.getJWTToken(anyString())).thenReturn("token");
        when(encryptionUtil.decrypt(anyString())).thenReturn("Valid1234");

        UserDTO result = userService.createNewUser(userDTO);

        assertNotNull(result);
        assertEquals(userDTO.getEmail(), result.getEmail());
        verify(userRepository).save(any(User.class));
        verify(userPhoneRepository).saveAll(anyList());
    }

    @Test
    public void testCreateNewUser_InvalidEmail() {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("invalid-email");
        userDTO.setPassword("Valid1234");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.createNewUser(userDTO);
        });

        assertEquals("El email proporcionado no es válido.", exception.getMessage());
    }

    @Test
    public void testCreateNewUser_InvalidPassword() {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("test@example.com");
        userDTO.setPassword("invalid");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.createNewUser(userDTO);
        });

        assertEquals("La contraseña provista debe cumplir con lo siguiente:\n" +
                "- Contener al menos una letra mayuscula.\n" +
                "- Contener exactamente dos dígitos (no necesariamente consecutivos)\n" +
                "- Contener solo letras minusculas, letras mayusculas y digitos.\n" +
                "- Tener una longitud mínima de 8 caracteres y una longitud máxima de 12 caracteres.", exception.getMessage());
    }

    @Test
    public void testCreateNewUser_EmailAlreadyExists() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("test@example.com");
        userDTO.setPassword("Martin12");

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(new User()));

        Exception exception = assertThrows(EmailAlreadyExistException.class, () -> {
            userService.createNewUser(userDTO);
        });

        assertEquals("El correo test@example.com ya fue registrado.", exception.getMessage());
    }

    @Test
    public void testGetUser_Success() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("test@example.com");
        userDTO.setPassword("Valid1234");

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setPassword("encryptedPassword");
        user.setToken("token");
        user.setEmail("test@example.com");

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(userPhoneRepository.findAllByUserId(any(UUID.class))).thenReturn(Collections.emptyList());
        when(encryptionUtil.decrypt(anyString())).thenReturn("Valid1234");
        when(jwtUtil.getJWTToken(anyString())).thenReturn("token");

        UserDTO result = userService.getUser(userDTO);

        assertNotNull(result);
        assertEquals(userDTO.getEmail(), result.getEmail());
        assertEquals("token", result.getToken());
    }

    @Test
    public void testGetUser_InvalidCredentials() {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("test@example.com");
        userDTO.setPassword("InvalidPassword");

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(new User()));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.getUser(userDTO);
        });

        assertEquals("El email o la contraseña no son validos", exception.getMessage());
    }
}
