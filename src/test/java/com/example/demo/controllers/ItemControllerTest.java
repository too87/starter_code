package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {

    private ItemController itemController;
    private final ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void setUp() {
        itemController = new ItemController();
        TestUtils.injectObjects(itemController, "itemRepository", itemRepository);
    }

    @Test
    public void getItemsShouldReturnItemList() {
        ResponseEntity<List<Item>> response = itemController.getItems();
        assertThat(response).isNotNull();
        assertEquals(0, response.getBody().size());
    }

    @Test
    public void getItemByIdShouldReturnItem() {
        Item item = new Item();
        item.setId(1L);
        item.setPrice(BigDecimal.valueOf(2.99));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        ResponseEntity<Item> response = itemController.getItemById(1L);
        Item itemById = response.getBody();

        assertNotNull(itemById);
        assertThat(itemById.getId()).isEqualTo(1L);
    }

    @Test
    public void getItemsByNameShouldReturnItemList() {
        Item item = new Item();
        item.setId(1L);
        item.setName("item");
        item.setPrice(BigDecimal.valueOf(2.99));
        when(itemRepository.findByName("item")).thenReturn(Collections.singletonList(item));

        ResponseEntity<List<Item>> response = itemController.getItemsByName("item");
        List<Item> itemList = response.getBody();

        assertNotNull(itemList);
        assertThat(itemList.get(0).getId()).isEqualTo(1L);
    }
}
