package com.example.task23.controller;


import com.example.task23.model.University;
import com.example.task23.model.User;
import com.example.task23.repository.UniversityRepository;
import com.example.task23.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class ApiController {
    private final UserRepository userRepository;
    private final UniversityRepository universityRepository;
    @Autowired
    public ApiController(UserRepository userRepository, UniversityRepository universityRepository) {
        this.userRepository = userRepository;
        this.universityRepository = universityRepository;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<List<User>> allUsers(){
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }
    @GetMapping("users/{id}")
    ResponseEntity<User> getUserById(@PathVariable Long id){
        User findUser = userRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("not found user"));
        return ResponseEntity.ok(findUser);
    }

    @PostMapping("/add")
    public ResponseEntity<User> createUser(@RequestBody User user){
        userRepository.save(user);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/add/{userId}/university/{universityId}")
    public ResponseEntity<?> addUniversityToUser( @PathVariable Long userId, @PathVariable Long universityId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь с id не найден"));
        University university = universityRepository.findById(universityId)
                .orElseThrow(() -> new RuntimeException("не найден университет с id" ));
        user.setUniversity(university);
        userRepository.save(user);
        return ResponseEntity.ok(user);
    }
    @PutMapping("/user/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User userDetails ){
        Optional<User> findUser =  userRepository.findById(id);
        if (findUser.isPresent()) {
            User user = findUser.get();
            user.setUsername(userDetails.getUsername());
            user.setPassword(userDetails.getPassword());
            user.setUniversity(userDetails.getUniversity());
            userRepository.save(user);
            return ResponseEntity.ok(user);
        }else {
            return ResponseEntity.badRequest().body("Пользователь с id " + id + " не найден");
        }
    }

    @DeleteMapping("/user/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id){
        userRepository.deleteById(id);
        return ResponseEntity.ok("Пользователь с id " + id + " успешно удален");
    }

}
