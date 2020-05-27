package com.test_boon.domain.repository;

import com.test_boon.domain.ItemEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

//https://docs.spring.io/spring-data/jpa/docs/1.5.0.RELEASE/reference/html/jpa.repositories.html#jpa.query-methods.query-creation
@Transactional(readOnly = true)
public interface ItemRepository extends CrudRepository<ItemEntity, Integer> {

    Optional<ItemEntity> findByName(String name);

    @Modifying
    @Transactional
    @Query("delete from ItemEntity i where i.name = ?1")
    void deleteByName(String name);
}
