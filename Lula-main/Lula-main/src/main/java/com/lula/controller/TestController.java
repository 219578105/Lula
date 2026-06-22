package com.lula.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class TestController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/test-db")
    public Map<String, Object> testDatabase() {
        Map<String, Object> result = jdbcTemplate.queryForMap("SELECT DATABASE() as db, NOW() as time, VERSION() as version");
        return result;
    }
}