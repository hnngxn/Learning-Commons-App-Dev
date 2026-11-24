package com.group3.learningcommonsproject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.group3.learningcommonsproject.repository.ProgramRepository;
import com.group3.learningcommonsproject.repository.StaffInfoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
    void seededProgramsShouldContainAllTenMockupPrograms() {
        assertEquals(10, programRepository.findAll().size());
    }
}