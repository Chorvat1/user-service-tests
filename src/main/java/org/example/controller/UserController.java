package org.example.controller;

import org.example.dto.CreateUserDto;
import org.example.dto.UpdateUserDto;
import org.example.dto.UserDto;
import org.example.mapper.UserMapper;
import org.example.model.User;
import org.example.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping
    public ResponseEntity<List<UserDto>> findAll() {
        List<UserDto> users = userService.findAll()
                .stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }


    @GetMapping("/{id}")
    public ResponseEntity<UserDto> findById(
            @PathVariable Long id) {
        Optional<User> user = userService.findById(id);
        return user
                .map(u -> ResponseEntity.ok(UserMapper.toDto(u)))
                .orElse(ResponseEntity.notFound().build());
    }


    @PostMapping
    public ResponseEntity<UserDto> create(
            @RequestBody CreateUserDto dto) {
        try {
            User user = userService.create(
                    dto.getName(),
                    dto.getEmail(),
                    dto.getAge());
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(UserMapper.toDto(user));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .badRequest()
                    .build();
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<UserDto> update(
            @PathVariable Long id,
            @RequestBody UpdateUserDto dto) {
        try {
            User user = userService.update(
                    id,
                    dto.getName(),
                    dto.getEmail(),
                    dto.getAge());
            return ResponseEntity.ok(UserMapper.toDto(user));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .notFound()
                    .build();
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {
        try {
            userService.delete(id);
            return ResponseEntity
                    .noContent()
                    .build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .notFound()
                    .build();
        }
    }
}