package com.example.task23.controller;
import com.example.task23.model.University;
import com.example.task23.repository.UniversityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/university")
public class UniversityController {

    private final UniversityRepository universityRepository;
    @Autowired
    public UniversityController(UniversityRepository universityRepository) {
        this.universityRepository = universityRepository;
    }


    @GetMapping()
    public ResponseEntity<List<University>> getAllUni(){
        List<University> universities =  universityRepository.findAll();
        return ResponseEntity.ok(universities);
    }

    @PostMapping("/add")
    public ResponseEntity<University> addUni(@RequestBody University university){
        University savedUniversity  = universityRepository.save(university);
        return ResponseEntity.ok(savedUniversity);
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateUni(@RequestBody University university, @PathVariable Long id){
        University findUni = universityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("not found university"));
        findUni.setTitle(university.getTitle());
        universityRepository.save(findUni);
        return ResponseEntity.ok("Success");
    }

}
