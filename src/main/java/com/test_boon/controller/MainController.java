package com.test_boon.controller;

import com.test_boon.domain.ItemEntity;
import com.test_boon.domain.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

@Scope("session")
@Controller
public class MainController {

    public static final String CONTROLLER_NAME = "main";

    private final User user;
    private final ItemRepository itemRepository;

    @Autowired
    public MainController(User user, ItemRepository itemRepository) {
        this.user = user;
        this.itemRepository = itemRepository;
    }

    @GetMapping(CONTROLLER_NAME)
    public String main(@RequestParam(name = "name", required = false, defaultValue = "World") String name,
                       Map<String, Object> model,
                       HttpSession session) {
        if (isNotUserAuthorized(session)) return "redirect:";
        model.put("items", itemRepository.findAll());
        return CONTROLLER_NAME;
    }

    @GetMapping("/main/loadImage/{itemName}")
    public void loadImage(@PathVariable String itemName,
                            HttpServletResponse response) throws IOException {
        byte[] image = itemRepository.findByName(itemName)
                .map(itemEntity -> {
                    String imagePath = itemEntity.getImagePath();
                    try {
                        return Files.readAllBytes(Paths.get(imagePath));
                    } catch (IOException e) {
                        e.printStackTrace();
                        return new byte[0];
                    }
                })
                .orElseThrow(() -> new IllegalArgumentException("Failed to find image with that name."));

        response.setContentType("image/jpeg, image/jpg, image/png, image/gif");
        try (ServletOutputStream out = response.getOutputStream()) {
            out.write(image);
        }
    }

    private boolean isNotUserAuthorized(HttpSession session) {
        if (user.getLogin() == null || user.getLogin().trim().isEmpty()) {
            session.invalidate();
            return true;
        }
        return false;
    }

    @PostMapping("/main/delete/{name}")
    public String remove(@PathVariable String name,
                         HttpSession session) {
        if (isNotUserAuthorized(session)) return "redirect:/";
        itemRepository.deleteByName(name);
        return "redirect:/" + CONTROLLER_NAME;
    }
    @PostMapping("add")
    public String add(
            @RequestParam String name,
            @RequestParam Integer count,
            @RequestParam MultipartFile image,
            HttpSession session) throws IOException {
        if (isNotUserAuthorized(session)) return "redirect:";
        byte[] bytes;
        try {
            bytes = image.getBytes();
        } catch (IOException e) {
            throw new IllegalArgumentException(e.getMessage());
        }

        if (itemRepository.findByName(name).isPresent()) {
            throw new IllegalArgumentException("Name already exists.");
        }

        String dirPath = "./images/";
        String imgPath = dirPath.concat(name);
        File dir = new File(dirPath);
        if (!dir.exists() && !dir.mkdir()) {
            throw new IllegalStateException("Failed to create directory: " + dirPath);
        }
        try (FileOutputStream fileOutputStream = new FileOutputStream(
                new File(imgPath)
        )) {
            fileOutputStream.write(bytes);
        }
        itemRepository.save(new ItemEntity(
                name,
                imgPath,
                count
        ));
        return "redirect:" + CONTROLLER_NAME;
    }

    @GetMapping("filter")
    public String filter(
            @RequestParam(required = false, defaultValue = "") String name,
            Map<String, Object> model,
            HttpSession session) {
        if (isNotUserAuthorized(session)) return "redirect:";
        if (!name.trim().isEmpty()) {
            model.put("items", itemRepository.findByName(name)
                    .orElse(null));
        } else {
            model.put("items", itemRepository.findAll());
        }
        return CONTROLLER_NAME;
    }

    @GetMapping("logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:";
    }

}