package org.example.dao;

import org.example.model.User;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UserDaoImplTest {

    private static SessionFactory sessionFactory;
    private static UserDao userDao;

    @BeforeAll
    static void setUpAll() {
        sessionFactory = new Configuration()
                .setProperty("hibernate.connection.driver_class", "org.postgresql.Driver")
                .setProperty("hibernate.connection.url", "jdbc:postgresql://localhost:5432/testdb")
                .setProperty("hibernate.connection.username", "postgres")
                .setProperty("hibernate.connection.password", "1234")
                .setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect")
                .setProperty("hibernate.hbm2ddl.auto", "create-drop")
                .setProperty("hibernate.show_sql", "false")
                .addAnnotatedClass(User.class)
                .buildSessionFactory();

        userDao = new UserDaoImpl(sessionFactory);
    }

    @AfterAll
    static void tearDownAll() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    @BeforeEach
    void setUp() {
        try (var session = sessionFactory.openSession()) {
            var tx = session.beginTransaction();
            session.createMutationQuery("DELETE FROM User").executeUpdate();
            tx.commit();
        }
    }

    @Test
    @DisplayName("Сохранение пользователя в БД")
    void save_ShouldPersistUser() {
        User user = new User("Alice", "alice@mail.com", 25);

        userDao.save(user);

        assertNotNull(user.getId());
    }

    @Test
    @DisplayName("Поиск пользователя по ID")
    void findById_ShouldReturnUser() {
        User user = new User("Alice", "alice@mail.com", 25);
        userDao.save(user);

        Optional<User> found = userDao.findById(user.getId());

        assertTrue(found.isPresent());
        assertEquals("Alice", found.get().getName());
        assertEquals("alice@mail.com", found.get().getEmail());
        assertEquals(25, found.get().getAge());
    }

    @Test
    @DisplayName("Поиск несуществующего пользователя")
    void findById_WithNonExistingId_ShouldReturnEmpty() {
        Optional<User> found = userDao.findById(999L);

        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("Получение всех пользователей")
    void findAll_ShouldReturnAllUsers() {
        userDao.save(new User("Alice", "alice@mail.com", 25));
        userDao.save(new User("Bob", "bob@mail.com", 30));
        userDao.save(new User("Charlie", "charlie@mail.com", 22));

        List<User> users = userDao.findAll();

        assertEquals(3, users.size());
    }

    @Test
    @DisplayName("Получение всех пользователей когда БД пуста")
    void findAll_WhenEmpty_ShouldReturnEmptyList() {
        List<User> users = userDao.findAll();

        assertTrue(users.isEmpty());
    }

    @Test
    @DisplayName("Обновление пользователя в БД")
    void update_ShouldUpdateUser() {
        User user = new User("Alice", "alice@mail.com", 25);
        userDao.save(user);

        user.setName("Alicia");
        user.setAge(26);
        userDao.update(user);

        Optional<User> updated = userDao.findById(user.getId());
        assertTrue(updated.isPresent());
        assertEquals("Alicia", updated.get().getName());
        assertEquals(26, updated.get().getAge());
    }

    @Test
    @DisplayName("Удаление пользователя из БД")
    void delete_ShouldRemoveUser() {
        User user = new User("Alice", "alice@mail.com", 25);
        userDao.save(user);
        Long id = user.getId();

        userDao.delete(id);

        Optional<User> deleted = userDao.findById(id);
        assertFalse(deleted.isPresent());
    }

    @Test
    @DisplayName("Удаление несуществующего пользователя")
    void delete_WithNonExistingId_ShouldNotThrow() {
        assertDoesNotThrow(() -> userDao.delete(999L));
    }
}