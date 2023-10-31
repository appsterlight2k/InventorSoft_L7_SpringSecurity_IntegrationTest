package co.inventorsoft.academy.spring.restfull.dao;

import co.inventorsoft.academy.spring.restfull.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long>, JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
