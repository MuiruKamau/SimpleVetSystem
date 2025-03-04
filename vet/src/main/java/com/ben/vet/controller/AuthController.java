package com.ben.vet.controller;

import com.ben.vet.dto.LoginRequestDTO;
import com.ben.vet.dto.UserRequestDTO;
import com.ben.vet.model.User;
import com.ben.vet.repository.UserRepository;
import com.ben.vet.security.JwtUtil; // Import JwtUtil - Assuming you will have this
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
@RestController
@RequestMapping("api/v1/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final JwtUtil jwtUtil; // Inject JwtUtil

    @Autowired
    public AuthController(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody UserRequestDTO userRequestDTO) {
        if (userRepository.findByUsername(userRequestDTO.getUsername()).isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("status", "error", "message", "Username already exists"));
        }

        User user = new User();
        user.setUsername(userRequestDTO.getUsername());
        user.setPassword(passwordEncoder.encode(userRequestDTO.getPassword()));
        user.setFirstName(userRequestDTO.getFirstName());
        user.setLastName(userRequestDTO.getLastName());
        // If you want to add email during registration as well, you can uncomment and use email from DTO
        // user.setEmail(userRequestDTO.getEmail());

        User savedUser = userRepository.save(user);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of(
                        "status", "success",
                        "message", "User registered successfully"
                ));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        Optional<User> existingUser = userRepository.findByUsername(loginRequestDTO.getUsername()); // Still using username for login

        if (existingUser.isEmpty() || !passwordEncoder.matches(loginRequestDTO.getPassword(), existingUser.get().getPassword())) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("status", "error", "message", "Invalid username or password")); // Message adjusted
        }

        String token = jwtUtil.generateToken(existingUser.get()); // Generate token using jwtUtil

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("status", "success");
        responseMap.put("message", "Login successful");
        responseMap.put("token", token);

        Map<String, Object> userDetailsMap = new HashMap<>();
        userDetailsMap.put("id", existingUser.get().getUserId());
        userDetailsMap.put("name", existingUser.get().getFirstName() + " " + existingUser.get().getLastName());
        userDetailsMap.put("username", existingUser.get().getUsername()); // Added username to response
        // If you want to include email in User entity and registration, uncomment below and in User entity/DTOs
        // userDetailsMap.put("email", existingUser.get().getEmail());
        // If you temporarily want to include a role (even if removing roles later), you can add a default or placeholder:
        userDetailsMap.put("role", "USER"); // Placeholder role

        responseMap.put("user", userDetailsMap);


        return ResponseEntity
                .status(HttpStatus.OK)
                .body(responseMap);
    }
}


//package com.ben.vet.controller;
//
//import com.ben.vet.dto.LoginRequestDTO;
//import com.ben.vet.dto.UserRequestDTO;
//import com.ben.vet.model.User;
//import com.ben.vet.repository.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Map;
//import java.util.Optional;
//
//@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
//@RestController
//@RequestMapping("api/v1/auth")
//public class AuthController {
//
//    private final UserRepository userRepository;
//    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
//
//    @Autowired
//    public AuthController(UserRepository userRepository) {
//        this.userRepository = userRepository;
//    }
//
//    @PostMapping("/register")
//    public ResponseEntity<Map<String, Object>> register(@RequestBody UserRequestDTO userRequestDTO) {
//        if (userRepository.findByUsername(userRequestDTO.getUsername()).isPresent()) {
//            return ResponseEntity
//                    .status(HttpStatus.BAD_REQUEST)
//                    .body(Map.of("status", "error", "message", "Username already exists"));
//        }
//
//        User user = new User();
//        user.setUsername(userRequestDTO.getUsername());
//        user.setPassword(passwordEncoder.encode(userRequestDTO.getPassword()));
//        user.setFirstName(userRequestDTO.getFirstName());
//        user.setLastName(userRequestDTO.getLastName());
//
//        userRepository.save(user);
//
//        return ResponseEntity
//                .status(HttpStatus.CREATED)
//                .body(Map.of(
//                        "status", "success",
//                        "message", "User registered successfully"
//                ));
//    }
//
//    @PostMapping("/login")
//    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequestDTO loginRequestDTO) {
//        Optional<User> userOptional = userRepository.findByUsername(loginRequestDTO.getUsername());
//
//        if (userOptional.isPresent()) {
//            User user = userOptional.get();
//            if (passwordEncoder.matches(loginRequestDTO.getPassword(), user.getPassword())) {
//                return ResponseEntity
//                        .status(HttpStatus.OK)
//                        .body(Map.of(
//                                "status", "success",
//                                "message", "Login successful"
//                        ));
//            }
//        }
//        return ResponseEntity
//                .status(HttpStatus.UNAUTHORIZED)
//                .body(Map.of(
//                        "status", "error",
//                        "message", "Invalid username or password"
//                ));
//    }
//}