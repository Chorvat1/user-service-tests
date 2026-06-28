package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.CreateUserDto;
import org.example.dto.UpdateUserDto;
import org.example.model.User;
import org.example.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    // ===== POST /users =====

    @Test
    @DisplayName("POST /users — создание пользователя")
    void create_ShouldReturnCreatedUser() throws Exception {
        CreateUserDto dto = new CreateUserDto();
        dto.setName("Alice");
        dto.setEmail("alice@mail.com");
        dto.setAge(25);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Alice"))
                .andExpect(jsonPath("$.email").value("alice@mail.com"))
                .andExpect(jsonPath("$.age").value(25))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    @DisplayName("POST /users — пустое имя возвращает 400")
    void create_WithEmptyName_ShouldReturn400() throws Exception {
        CreateUserDto dto = new CreateUserDto();
        dto.setName("");
        dto.setEmail("alice@mail.com");
        dto.setAge(25);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    // ===== GET /users =====

    @Test
    @DisplayName("GET /users — получение всех пользователей")
    void findAll_ShouldReturnList() throws Exception {
        userRepository.save(new User("Alice", "alice@mail.com", 25));
        userRepository.save(new User("Bob", "bob@mail.com", 30));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Alice"))
                .andExpect(jsonPath("$[1].name").value("Bob"));
    }

    @Test
    @DisplayName("GET /users — пустой список")
    void findAll_WhenEmpty_ShouldReturnEmptyList() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // ===== GET /users/{id} =====

    @Test
    @DisplayName("GET /users/{id} — получение по ID")
    void findById_ShouldReturnUser() throws Exception {
        User saved = userRepository.save(
                new User("Alice", "alice@mail.com", 25));

        mockMvc.perform(get("/users/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Alice"))
                .andExpect(jsonPath("$.email").value("alice@mail.com"));
    }

    @Test
    @DisplayName("GET /users/{id} — несуществующий ID")
    void findById_WithNonExistingId_ShouldReturn404()
            throws Exception {
        mockMvc.perform(get("/users/999"))
                .andExpect(status().isNotFound());
    }

    // ===== PUT /users/{id} =====

    @Test
    @DisplayName("PUT /users/{id} — обновление пользователя")
    void update_ShouldReturnUpdatedUser() throws Exception {
        User saved = userRepository.save(
                new User("Alice", "alice@mail.com", 25));

        UpdateUserDto dto = new UpdateUserDto();
        dto.setName("Alicia");
        dto.setEmail("alicia@mail.com");
        dto.setAge(26);

        mockMvc.perform(put("/users/" + saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Alicia"))
                .andExpect(jsonPath("$.email").value("alicia@mail.com"))
                .andExpect(jsonPath("$.age").value(26));
    }

    @Test
    @DisplayName("PUT /users/{id} — несуществующий ID")
    void update_WithNonExistingId_ShouldReturn404()
            throws Exception {
        UpdateUserDto dto = new UpdateUserDto();
        dto.setName("Nobody");

        mockMvc.perform(put("/users/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    // ===== DELETE /users/{id} =====

    @Test
    @DisplayName("DELETE /users/{id} — удаление пользователя")
    void delete_ShouldReturn204() throws Exception {
        User saved = userRepository.save(
                new User("Alice", "alice@mail.com", 25));

        mockMvc.perform(delete("/users/" + saved.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /users/{id} — несуществующий ID")
    void delete_WithNonExistingId_ShouldReturn404()
            throws Exception {
        mockMvc.perform(delete("/users/999"))
                .andExpect(status().isNotFound());
    }
}