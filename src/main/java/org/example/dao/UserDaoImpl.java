package org.example.dao;

import org.example.model.User;
import org.example.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class UserDaoImpl implements UserDao {

    private static final Logger logger =
            LoggerFactory.getLogger(UserDaoImpl.class);

    @Override
    public void save(User user) {
        Transaction transaction = null;
        try (Session session =
                     HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(user);
            transaction.commit();
            logger.info("Пользователь сохранён: {}", user);
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Ошибка при сохранении пользователя", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<User> findById(Long id) {
        try (Session session =
                     HibernateUtil.getSessionFactory().openSession()) {
            User user = session.get(User.class, id);
            return Optional.ofNullable(user);
        } catch (Exception e) {
            logger.error("Ошибка при поиске пользователя с id={}", id, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<User> findAll() {
        try (Session session =
                     HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM User", User.class).getResultList();
        } catch (Exception e) {
            logger.error("Ошибка при получении всех пользователей", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(User user) {
        Transaction transaction = null;
        try (Session session =
                     HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(user);
            transaction.commit();
            logger.info("Пользователь обновлён: {}", user);
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Ошибка при обновлении пользователя", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Long id) {
        Transaction transaction = null;
        try (Session session =
                     HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            User user = session.get(User.class, id);
            if (user != null) {
                session.remove(user);
                logger.info("Пользователь удалён с id={}", id);
            } else {
                logger.warn(
                        "Пользователь с id={} не найден для удаления", id);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error(
                    "Ошибка при удалении пользователя с id={}", id, e);
            throw new RuntimeException(e);
        }
    }
}