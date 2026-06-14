package org.example;

import org.example.dao.UserDao;
import org.example.dao.UserDaoImpl;
import org.example.model.User;
import org.example.util.HibernateUtil;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class App {

    private static final UserDao userDao = new UserDaoImpl();
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

        if (name.isEmpty() || email.isEmpty()) {
            System.out.println("Имя и email не могут быть пустыми!");
            return;
        }

        User user = new User(name, email, age);
        userDao.save(user);
        System.out.println("Пользователь успешно создан: " + user);
    }


    private static void findUserById() {
        System.out.println("\n--- Поиск пользователя ---");
        long id = getIntInput("Введите ID: ");

        Optional<User> user = userDao.findById(id);

        if (user.isPresent()) {
            System.out.println("Найден: " + user.get());
        } else {
            System.out.println("Пользователь с ID=" + id + " не найден.");
        }
    }


    private static void findAllUsers() {
        System.out.println("\n--- Все пользователи ---");
        List<User> users = userDao.findAll();

        if (users.isEmpty()) {
            System.out.println("Список пользователей пуст.");
        } else {
            users.forEach(System.out::println);
        }
    }


    private static void updateUser() {
        System.out.println("\n--- Обновление пользователя ---");
        long id = getIntInput("Введите ID пользователя: ");

        Optional<User> optionalUser = userDao.findById(id);

        if (optionalUser.isEmpty()) {
            System.out.println("Пользователь с ID=" + id + " не найден.");
            return;
        }

        User user = optionalUser.get();
        System.out.println("Текущие данные: " + user);

        System.out.print("Новое имя (Enter чтобы оставить '"
                + user.getName() + "'): ");
        String name = scanner.nextLine().trim();
        if (!name.isEmpty()) {
            user.setName(name);
        }

        System.out.print("Новый email (Enter чтобы оставить '"
                + user.getEmail() + "'): ");
        String email = scanner.nextLine().trim();
        if (!email.isEmpty()) {
            user.setEmail(email);
        }

        System.out.print("Новый возраст (0 чтобы оставить "
                + user.getAge() + "): ");
        int age = getIntInput("");
        if (age > 0) {
            user.setAge(age);
        }

        userDao.update(user);
        System.out.println("Пользователь обновлён: " + user);
    }

    private static void deleteUser() {
        System.out.println("\n--- Удаление пользователя ---");
        long id = getIntInput("Введите ID пользователя: ");

        Optional<User> user = userDao.findById(id);
        if (user.isEmpty()) {
            System.out.println("Пользователь с ID=" + id + " не найден.");
            return;
        }

        userDao.delete(id);
        System.out.println(
                "Пользователь с ID=" + id + " успешно удалён.");
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