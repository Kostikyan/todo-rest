package com.todorest.endpoint;

import com.todorest.dto.CreateUserRequestDto;
import com.todorest.dto.UserAuthRequestDto;
import com.todorest.dto.UserAuthResponseDto;
import com.todorest.dto.UserDto;
import com.todorest.entity.Type.UserType;
import com.todorest.entity.User;
import com.todorest.mapper.UserMapper;
import com.todorest.repository.UserRepository;
import com.todorest.security.CurrentUser;
import com.todorest.util.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * /user {POST} - user-ի գրանցվելն է
 * /user/auth {POST} - user-ի լոգինն է
 * /user/{id} {GET} - user-ին գետ կենենք իրա իդ-ով։ Բացի պառոլից սաղ դաշտերը ցույց կուդանք
 * /user/{id} {DELETE} - մենակ ադմինը իրավունք ունի ջնջե յուզեր
 * /user {PUT} - Փոփոխել յուզերի տվյալները, ամեն մեկը իրան կրա փոխե
 */

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserEndpoint {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil tokenUtil;
    private final UserMapper userMapper;

    @PostMapping()
    public ResponseEntity<UserDto> userRegistration(@RequestBody CreateUserRequestDto createUserRequestDto){
        Optional<User> byEmail = userRepository.findByEmail(createUserRequestDto.getEmail());
        if(byEmail.isPresent()){
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        User user = userMapper.map(createUserRequestDto);
        user.setPassword(passwordEncoder.encode(createUserRequestDto.getPassword()));
        user.setType(UserType.USER);
        userRepository.save(user);
        return ResponseEntity.ok(userMapper.mapToDto(user));
    }

    @PostMapping("/auth")
    public ResponseEntity<UserAuthResponseDto> userLogin(@RequestBody UserAuthRequestDto userAuthRequestDto){
        Optional<User> byEmail = userRepository.findByEmail(userAuthRequestDto.getEmail());
        if (byEmail.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = byEmail.get();
        if (!passwordEncoder.matches(userAuthRequestDto.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = tokenUtil.generateToken(userAuthRequestDto.getEmail());
        return ResponseEntity.ok(new UserAuthResponseDto(token));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable("id") int id) {
        Optional<User> byId = userRepository.findById(id);
        if(byId.isPresent()){
            return ResponseEntity.ok(userMapper.mapToDto(byId.get()));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUserById(@PathVariable("id") int id) {
        if(userRepository.existsById(id)){
            userRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping
    public ResponseEntity<UserDto> updateUserData(@AuthenticationPrincipal CurrentUser currentUser, @RequestBody User user) {
        Optional<User> byEmail = userRepository.findByEmail(user.getEmail());
        if(byEmail.isPresent()){
            return ResponseEntity.badRequest().build();
        }

        User getCurrentUser = currentUser.getUser();
        getCurrentUser.setName(user.getName());
        getCurrentUser.setSurname(user.getSurname());
        getCurrentUser.setEmail(user.getEmail());
        getCurrentUser.setPassword(passwordEncoder.encode(user.getPassword()));
        getCurrentUser.setType(UserType.USER);

        userRepository.save(getCurrentUser);
        return ResponseEntity.ok(userMapper.mapToDto(getCurrentUser));
    }
}
