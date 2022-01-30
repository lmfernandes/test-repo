package com.challenge1.item.repository;

import com.challenge1.item.entities.Item;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Collection;
import java.util.List;

public interface ItemRepository extends PagingAndSortingRepository<Item, Long> {

    @Query("SELECT i FROM Item i WHERE i.name = ?1")
    Collection<Item> findByName(String name);

    @Query("SELECT i FROM Item i WHERE i.groupId = ?1")
    Collection<Item> findByGroup(long group);
}
