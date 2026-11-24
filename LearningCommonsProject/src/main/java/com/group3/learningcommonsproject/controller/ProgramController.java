package com.group3.learningcommonsproject.controller;

import com.group3.learningcommonsproject.entity.Program;
import com.group3.learningcommonsproject.repository.ProgramRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/programs")
public class ProgramController {

    private final ProgramRepository programRepository;

    public ProgramController(ProgramRepository programRepository) {
        this.programRepository = programRepository;
    }

    @GetMapping
    public ResponseEntity<List<Program>> getAllPrograms() {
        return ResponseEntity.ok(programRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<Program> createProgram(@RequestBody Program program) {
        Program saved = programRepository.save(program);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
}