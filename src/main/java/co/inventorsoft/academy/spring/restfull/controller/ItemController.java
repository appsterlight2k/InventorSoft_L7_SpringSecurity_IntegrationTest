package co.inventorsoft.academy.spring.restfull.controller;

import co.inventorsoft.academy.spring.restfull.dto.ItemDto;
import co.inventorsoft.academy.spring.restfull.dto.WebResponse;
import co.inventorsoft.academy.spring.restfull.exception.ItemNotFoundException;
import co.inventorsoft.academy.spring.restfull.model.Item;
import co.inventorsoft.academy.spring.restfull.service.ItemService;
import co.inventorsoft.academy.spring.restfull.util.MapperUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
public class ItemController {
    private final ItemService itemService;
    @Autowired
    private MapperUtil mapperUtil;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping("/items")
    public WebResponse<ItemDto> create(@Valid @RequestBody ItemDto itemDto) {
        Item item = mapperUtil.convertToEntity(itemDto, Item.class);
        Item created = itemService.save(item);

        return getWebResponseEntity(created, "Item was created successfully");
    }

    @GetMapping("/items")
    public WebResponse<List<ItemDto>> getAllItems() {
        List<Item> items = itemService.findAll();

        return getWebResponseEntityForAll(items, "Items were fetched successfully");
    }

    //example: /items?price=100
    @GetMapping(value = "/items", params = "price")
    public WebResponse<List<ItemDto>> getItemsByPrice(@RequestParam Double price) {
        List<Item> items = itemService.findAllByPrice(price);

        return getWebResponseEntityForAll(items, "Items were fetched by price successfully");
    }
    @GetMapping("/items/{id}")
    public WebResponse<ItemDto> getItemById(@PathVariable Long id) {
        return itemService.getById(id)
                .map(item -> getWebResponseEntity(item, "Item was fetched successfully"))
                .orElseThrow(() -> new ItemNotFoundException("Item was not found"));
    }

    //example: /items?name=Jedi
    @GetMapping(value = "/items", params = "name")
    public WebResponse<List<ItemDto>> findItemsByNameContains(@RequestParam("name") String text) {
        List<Item> items = itemService.findAllByNameContains(text);

        return getWebResponseEntityForAll(items, "Items were fetched by name successfully");
    }

    @PutMapping("/items/{id}")
    public WebResponse<ItemDto> update(@PathVariable Long id, @Valid @RequestBody ItemDto itemDto) {
        Item item = mapperUtil.convertToEntity(itemDto, Item.class);
        item.setId(id);
        Item updated = itemService.save(item);

        return getWebResponseEntity(item, "Item was updated successfully");
    }

    @DeleteMapping("/items/{id}")
    public void deleteById(@PathVariable Long id) {
        itemService.deleteById(id);
    }

    private WebResponse<ItemDto> getWebResponseEntity(Item item, String successMessage) {
        ItemDto itemDto = mapperUtil.convertToDto(item, ItemDto.class);

        return new WebResponse<>(itemDto, successMessage, true, 1);
    }
    private WebResponse<List<ItemDto>> getWebResponseEntityForAll(List<Item> items, String successMessage) {
        List<ItemDto> itemsDto = mapperUtil.convertToDtoList(items, ItemDto.class);

        return new WebResponse<>(itemsDto, successMessage, true, itemsDto.size());
    }

}
