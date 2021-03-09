package com.example.demo;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.Cookie;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserControllerTest {
    @Autowired
    MockMvc mvc;

    @Autowired
    UserRepository repository;

    @Test
    @Transactional
    @Rollback
    public void testList() throws Exception{
        User user = new User();
        user.setEmail("davon@hotmail");
        repository.save(user);

        MockHttpServletRequestBuilder request = get("/users")
                .contentType(MediaType.APPLICATION_JSON);

        this.mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(user.getId().intValue())))
                .andExpect(jsonPath("$.[0].email", is("davon@hotmail")));
    }

    @Test
    @Transactional
    @Rollback
    public void testCreate() throws Exception{
        User user = new User();
        user.setEmail("jimmy@hotmail");
        user.setPassword("reallysecretstuff");

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(user);

        MockHttpServletRequestBuilder request = post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        this.mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("jimmy@hotmail") ));
    }

    @Test
    @Transactional
    @Rollback
    public void testGetUser() throws Exception {
        User user = new User();
        user.setEmail("winston@hotmail");
        user.setPassword("password123");
        repository.save(user);

        String json = getJSON("/data.json");

        MockHttpServletRequestBuilder request = get("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        this.mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId().intValue())))
                .andExpect(jsonPath("$.email", is("winston@hotmail")));

    }

    @Test
    @Transactional
    @Rollback
    public void testUpdate() throws Exception{
        MockHttpServletRequestBuilder request = patch("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"newemailvalue\"}");

        this.mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("newemailvalue") ));
    }

    @Test
    @Transactional
    @Rollback
    public void testDelete() throws Exception {
        User user1 = new User();
        user1.setEmail("pico@hotmail");
        user1.setPassword("password123");

        User user2 = new User();
        user2.setEmail("chico@hotmail");
        user2.setPassword("password123");

        User user3 = new User();
        user3.setEmail("rico@hotmail");
        user3.setPassword("password123");

        repository.save(user1);
        repository.save(user2);
        repository.save(user3);

        MockHttpServletRequestBuilder request = delete("/users/1")
                .contentType(MediaType.APPLICATION_JSON);

        this.mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count", is(2)));
    }

    @Test
    @Transactional
    @Rollback
    public void testAuthenticate() throws Exception {
        User user = new User();
        user.setEmail("pico@hotmail");
        user.setPassword("password123");

        repository.save(user);

        Map<String, Object> myMap = new HashMap<>();
        myMap.put("email", "pico@hotmail");
        myMap.put("password", "password123");

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(myMap);

        MockHttpServletRequestBuilder request = post("/users/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        this.mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authenticated", is(true)));
    }



    private String getJSON(String path) throws Exception {
        URL url = this.getClass().getResource(path);
        return new String(Files.readAllBytes(Paths.get(url.getFile())));
    }
}
