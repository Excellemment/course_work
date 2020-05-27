package com.test_boon.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class ItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String name;
    private String imagePath;
    private Integer count;

    public ItemEntity() {
        count = 0;
    }

    public ItemEntity(String name, String imagePath, Integer count) {
        this.name = name;
        this.imagePath = imagePath;
        this.count = count;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImagePath() {
        return imagePath;
    }

    public Integer getCount() {
        return count;
    }
}
