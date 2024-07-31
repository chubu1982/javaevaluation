package com.bci.javaevaluation.services;

import com.bci.javaevaluation.EmailAlreadyExistException;
import com.bci.javaevaluation.domain.User;
import com.bci.javaevaluation.domain.UserPhone;
import com.bci.javaevaluation.model.PhoneDTO;
import com.bci.javaevaluation.model.UserDTO;
import com.bci.javaevaluation.repositories.UserPhoneRepository;
import com.bci.javaevaluation.repositories.UserRepository;
import com.bci.javaevaluation.util.EncryptionUtil;
import com.bci.javaevaluation.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserPhoneRepository userPhoneRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private EncryptionUtil encryptionUtil;

    @Transactional
    public UserDTO createNewUser(UserDTO userDTO) throws Exception {
        if (!isValidEmail(userDTO.getEmail())) {
            throw new IllegalArgumentException("El email proporcionado no es válido.");
        }
        if (!isValidPassword(userDTO.getPassword())) {
            throw new IllegalArgumentException("La contraseña provista debe cumplir con lo siguiente:\n" +
                    "- Contener al menos una letra mayuscula.\n" +
                    "- Contener exactamente dos dígitos (no necesariamente consecutivos)\n" +
                    "- Contener solo letras minusculas, letras mayusculas y digitos.\n" +
                    "- Tener una longitud mínima de 8 caracteres y una longitud máxima de 12 caracteres.");
        }
        Optional<User> existingUserOptional = userRepository.findByEmail(userDTO.getEmail());
        if (existingUserOptional.isPresent()) {
            throw new EmailAlreadyExistException(userDTO.getEmail());
        } else {
            User user = mapToUser(userDTO);
            userRepository.save(user);
            List<UserPhone> userPhones = extractUserPhones(userDTO, user.getId());
            userPhoneRepository.saveAll(userPhones);
            return this.mapToUserDTO(user, userPhones);
        }
    }

    public UserDTO getUser(UserDTO userDTO) throws Exception {
        Optional<User> existingUserOptional = userRepository.findByEmail(userDTO.getEmail());
        if (existingUserOptional.isPresent()) {
            User existingUser = existingUserOptional.get();
            List<UserPhone> userPhones = this.userPhoneRepository.findAllByUserId(existingUser.getId());
            UserDTO recoveredUserDTO = this.mapToUserDTO(existingUser, userPhones);
            if (userDTO.getPassword().equals(recoveredUserDTO.getPassword())) {
                existingUser.setToken(jwtUtil.getJWTToken(existingUser.getEmail()));
                existingUser.setLastLogin(new Date());
                userRepository.save(existingUser);
                return recoveredUserDTO;
            }
        }
        throw new IllegalArgumentException("El email o la contraseña no son validos");
    }

    private User mapToUser(UserDTO userDTO) throws Exception {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setName(userDTO.getName());
        user.setPassword(encryptionUtil.encrypt(userDTO.getPassword()));
        user.setEmail(userDTO.getEmail());
        user.setToken(jwtUtil.getJWTToken(userDTO.getEmail()));
        user.setCreated(new Date());
        user.setIsActive(Boolean.TRUE);

        return user;
    }

    private UserDTO mapToUserDTO(User user, List<UserPhone> userPhones) throws Exception {
        UserDTO userDTO = new UserDTO();
        List<PhoneDTO> phoneDTOS = new ArrayList<>();
        userDTO.setId(user.getId());
        userDTO.setActive(user.getIsActive());
        userDTO.setCreated(user.getCreated());
        userDTO.setName(user.getName());
        userDTO.setEmail(user.getEmail());
        userDTO.setPassword(encryptionUtil.decrypt(user.getPassword()));
        userDTO.setToken(user.getToken());
        userDTO.setLastLogin(user.getLastLogin());
        for (UserPhone userPhone : userPhones) {
            PhoneDTO phoneDTO = new PhoneDTO();
            phoneDTO.setNumber(userPhone.getNumber());
            phoneDTO.setCitycode(userPhone.getCitycode());
            phoneDTO.setContrycode(userPhone.getContrycode());
            phoneDTOS.add(phoneDTO);
        }
        userDTO.setPhones(phoneDTOS);
        return userDTO;
    }

    private List<UserPhone> extractUserPhones(UserDTO userDTO, UUID userId) {
        List<UserPhone> userPhones = new ArrayList<>();
        if (userDTO.getPhones() != null) {
            for (PhoneDTO phoneDTO : userDTO.getPhones()) {
                UserPhone userPhone = new UserPhone();
                userPhone.setId(UUID.randomUUID());
                userPhone.setUserId(userId);
                userPhone.setNumber(phoneDTO.getNumber());
                userPhone.setCitycode(phoneDTO.getCitycode());
                userPhone.setContrycode(phoneDTO.getContrycode());
                userPhones.add(userPhone);
            }
        }
        return userPhones;
    }

    private boolean isValidEmail(String email) {
        String regex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private boolean isValidPassword(String password) {
        String regex = "^(?=(?:[^0-9]*[0-9]){2}[^0-9]*$)(?=(?:[^A-Z]*[A-Z]){1}[^A-Z]*$).{8,12}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }
}
