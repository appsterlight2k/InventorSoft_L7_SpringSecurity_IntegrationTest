package co.inventorsoft.academy.spring.restfull.service;

import co.inventorsoft.academy.spring.restfull.dao.ItemRepository;
import co.inventorsoft.academy.spring.restfull.model.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ItemService {
    private final ItemRepository itemRepository;

    @Autowired
    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Transactional(readOnly = true)
    public List<Item> findAll() {
        return itemRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Item> findAllByPrice(Double price) {
        return itemRepository.findAllByPrice(price);
    }

    @Transactional(readOnly = true)
    public Optional<Item> getById(Long id) {
        return itemRepository.findById(id);
    }

    @Transactional
    public void deleteById(Long id) {
        itemRepository.deleteById(id);
    }
    @Transactional
    public Item save(Item item) {
        return itemRepository.save(item);
    }

    @Transactional(readOnly = true)
    public List<Item> findAllByNameContains(String name) {
        return itemRepository.findAllByNameContains(name);
    }

}
