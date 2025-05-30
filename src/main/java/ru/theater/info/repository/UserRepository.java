package ru.theater.info.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.theater.info.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByMail(String mail);
    Optional<User> findByPhone(String phone);
}
