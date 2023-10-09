package ir.aut.ce.cloud.serverlessauthentication.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ir.aut.ce.cloud.serverlessauthentication.model.User;

public interface UserRepository extends JpaRepository<User, String> {
    List<User> findByState(String state);

    List<User> findByEmail(String email);

    List<User> findByLastname(String name);

    List<User> findByNationalId(String nationalId);
}
