package org.example.service;

import org.example.dao.UserDao;
import org.example.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    // Создаём мок (заглушку) вместо реального UserDao
    @Mock
    private UserDao userDao;

    // Создаём UserServiceImpl и автоматически
    // передаём туда наш мок
    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        // Этот метод запускается перед каждым тестом
        // Создаём тестового пользователя
        testUser = new User("Alice", "alice@mail.com", 25);
        testUser.setId(1L);
    }

    // ===== ТЕСТЫ ДЛЯ create() =====

    @Test
    @DisplayName("Создание пользователя с валидными данными")
    void create_WithValidData_ShouldSaveUser() {
        // ARRANGE (подготовка)
        // Говорим моку: при вызове save() ничего не делай
        doNothing().when(userDao).save(any(User.class));

        // ACT (действие)
        User result = userService.create("Alice", "alice@mail.com", 25);

        // ASSERT (проверка)
        assertNotNull(result);
        assertEquals("Alice", result.getName());
        assertEquals("alice@mail.com", result.getEmail());
        assertEquals(25, result.getAge());

        // Проверяем что save() был вызван ровно 1 раз
        verify(userDao, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Создание с пустым именем должно бросить исключение")
    void create_WithEmptyName_ShouldThrowException() {
        // Проверяем что при пустом имени
        // выбрасывается IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () ->
                userService.create("", "alice@mail.com", 25)
        );

        // Проверяем что save() вообще не вызывался
        verify(userDao, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Создание с пустым email должно бросить исключение")
    void create_WithEmptyEmail_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () ->
                userService.create("Alice", "", 25)
        );

        verify(userDao, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Создание с возрастом 0 должно бросить исключение")
    void create_WithZeroAge_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () ->
                userService.create("Alice", "alice@mail.com", 0)
        );

        verify(userDao, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Создание с отрицательным возрастом должно бросить исключение")
    void create_WithNegativeAge_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () ->
                userService.create("Alice", "alice@mail.com", -5)
        );

        verify(userDao, never()).save(any(User.class));
    }

    // ===== ТЕСТЫ ДЛЯ findById() =====

    @Test
    @DisplayName("Поиск существующего пользователя по ID")
    void findById_WithExistingId_ShouldReturnUser() {
        // Говорим моку: при вызове findById(1L)
        // верни нашего тестового пользователя
        when(userDao.findById(1L))
                .thenReturn(Optional.of(testUser));

        Optional<User> result = userService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals("Alice", result.get().getName());
        verify(userDao, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Поиск несуществующего пользователя по ID")
    void findById_WithNonExistingId_ShouldReturnEmpty() {
        when(userDao.findById(99L))
                .thenReturn(Optional.empty());

        Optional<User> result = userService.findById(99L);

        assertFalse(result.isPresent());
        verify(userDao, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Поиск с невалидным ID должен бросить исключение")
    void findById_WithInvalidId_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () ->
                userService.findById(0L)
        );

        verify(userDao, never()).findById(anyLong());
    }

    // ===== ТЕСТЫ ДЛЯ findAll() =====

    @Test
    @DisplayName("Получение всех пользователей")
    void findAll_ShouldReturnAllUsers() {
        User user2 = new User("Bob", "bob@mail.com", 30);
        user2.setId(2L);

        when(userDao.findAll())
                .thenReturn(List.of(testUser, user2));

        List<User> result = userService.findAll();

        assertEquals(2, result.size());
        verify(userDao, times(1)).findAll();
    }

    @Test
    @DisplayName("Получение всех пользователей когда список пуст")
    void findAll_WhenEmpty_ShouldReturnEmptyList() {
        when(userDao.findAll()).thenReturn(List.of());

        List<User> result = userService.findAll();

        assertTrue(result.isEmpty());
        verify(userDao, times(1)).findAll();
    }

    // ===== ТЕСТЫ ДЛЯ update() =====

    @Test
    @DisplayName("Обновление существующего пользователя")
    void update_WithExistingUser_ShouldUpdateAndReturn() {
        when(userDao.findById(1L))
                .thenReturn(Optional.of(testUser));
        doNothing().when(userDao).update(any(User.class));

        User result = userService.update(
                1L, "Alicia", "alicia@mail.com", 26);

        assertEquals("Alicia", result.getName());
        assertEquals("alicia@mail.com", result.getEmail());
        assertEquals(26, result.getAge());

        verify(userDao, times(1)).update(any(User.class));
    }

    @Test
    @DisplayName("Обновление несуществующего пользователя должно бросить исключение")
    void update_WithNonExistingUser_ShouldThrowException() {
        when(userDao.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                userService.update(99L, "Bob", "bob@mail.com", 30)
        );

        verify(userDao, never()).update(any(User.class));
    }

    // ===== ТЕСТЫ ДЛЯ delete() =====

    @Test
    @DisplayName("Удаление существующего пользователя")
    void delete_WithExistingUser_ShouldDelete() {
        when(userDao.findById(1L))
                .thenReturn(Optional.of(testUser));
        doNothing().when(userDao).delete(1L);

        assertDoesNotThrow(() -> userService.delete(1L));

        verify(userDao, times(1)).delete(1L);
    }

    @Test
    @DisplayName("Удаление несуществующего пользователя должно бросить исключение")
    void delete_WithNonExistingUser_ShouldThrowException() {
        when(userDao.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                userService.delete(99L)
        );

        verify(userDao, never()).delete(anyLong());
    }
}