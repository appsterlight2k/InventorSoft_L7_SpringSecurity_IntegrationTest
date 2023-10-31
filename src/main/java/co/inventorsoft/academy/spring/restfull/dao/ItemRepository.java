package co.inventorsoft.academy.spring.restfull.dao;

import co.inventorsoft.academy.spring.restfull.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByPrice(Double price);
    List<Item> findAllByNameContains(String text);
}
