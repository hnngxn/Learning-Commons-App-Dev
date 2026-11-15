package com.group3.learningcommonsproject;

import com.group3.learningcommonsproject.entities.Program;
import com.group3.learningcommonsproject.repositories.ProgramRepository;
import com.group3.learningcommonsproject.repositories.StaffInfoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class LearningCommonsProjectApplicationTests {

    @Autowired
    private ProgramRepository programRepository;

    @Autowired
    private StaffInfoRepository staffInfoRepository;

    @Test
    void contextLoads() {
        assertNotNull(programRepository);
        assertNotNull(staffInfoRepository);
    }

    @Test
    void seededProgramsShouldExist() {
        List<Program> programs = programRepository.findAll();
        assertFalse(programs.isEmpty(), "Seeded programs should be loaded by Flyway");
    }
}