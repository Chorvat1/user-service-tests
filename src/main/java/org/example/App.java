package org.example;

import org.example.dao.UserDao;
import org.example.dao.UserDaoImpl;
import org.example.model.User;
import org.example.service.UserService;
import org.example.service.UserServiceImpl;
import org.example.util.HibernateUtil;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class App {

    private static final UserDao userDao = new UserDaoImpl();
    private static final UserService userService =
            new UserServiceImpl(userDao);
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("=== User Service ===");

        while (true) {
            printMenu();
            int choice = getIntInput("Выберите действие: ");

            switch (choice) {
                case 1 -> createUser();
                case 2 -> findUserById();
                case 3 -> findAllUsers();
                case 4 -> updateUser();
                case 5 -> deleteUser();
                case 0 -> {
                    System.out.println("Выход...");
                    HibernateUtil.shutdown();
                    return;
                }
                default -> System.out.println(
                        "Неверный выбор. Попробуйте снова.");
            }
        }
    }

    private static void printMenu() {
        System.out.println("\n--- МЕНЮ ---");
        System.out.println("1. Создать пользователя");
        System.out.println("2. Найти пользователя по ID");
        System.out.println("3. Показать всех пользователей");
        System.out.println("4. Обновить пользователя");
        System.out.println("5. Удалить пользователя");
        System.out.println("0. Выход");
    }

    private static void createUser() {
        System.out.println("\n--- Создание пользователя ---");
        System.out.print("Имя: ");
        String name = scanner.nextLine().trim();
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        int age = getIntInput("Возраст: ");

        try {
            User user = userService.create(name, email, age);
            System.out.println("Создан: " + user);
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private static void findUserById() {
        System.out.println("\n--- Поиск пользователя ---");
        long id = getIntInput("Введите ID: ");

        try {
            Optional<User> user = userService.findById(id);
            if (user.isPresent()) {
                System.out.println("Найден: " + user.get());
            } else {
                System.out.println("Пользователь не найден.");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private static void findAllUsers() {
        System.out.println("\n--- Все пользователи ---");
        List<User> users = userService.findAll();
        if (users.isEmpty()) {
            System.out.println("Список пуст.");
        } else {
            users.forEach(System.out::println);
        }
    }

    private static void updateUser() {
        System.out.println("\n--- Обновление пользователя ---");
        long id = getIntInput("Введите ID: ");

        System.out.print("Новое имя (Enter чтобы пропустить): ");
        String name = scanner.nextLine().trim();

        System.out.print("Новый email (Enter чтобы пропустить): ");
        String email = scanner.nextLine().trim();

        int age = getIntInput("Новый возраст (0 чтобы пропустить): ");

        try {
            User updated = userService.update(id, name, email, age);
            System.out.println("Обновлён: " + updated);
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private static void deleteUser() {
        System.out.println("\n--- Удаление пользователя ---");
        long id = getIntInput("Введите ID: ");

        try {
            userService.delete(id);
            System.out.println("Пользователь удалён.");
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private static int getIntInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Ошибка! Введите целое число.");
            }
        }
    }
}