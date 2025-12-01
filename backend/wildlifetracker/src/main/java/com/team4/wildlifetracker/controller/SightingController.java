package com.team4.wildlifetracker.controller;

import com.team4.wildlifetracker.model.Sighting;
import com.team4.wildlifetracker.service.SightingService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sightings")
@CrossOrigin(origins = "*")
public class SightingController {

    private final SightingService sightingService;

    public SightingController(SightingService sightingService) {
        this.sightingService = sightingService;
    }

    // CREATE
    @PostMapping
    public Sighting createSighting(@RequestBody Sighting sighting) {
        return sightingService.createSighting(sighting);
    }

    // READ (single)
    @GetMapping("/{id}")
    public Sighting getSighting(@PathVariable Long id) {
        return sightingService.findById(id);
    }

    // READ (all)
    @GetMapping
    public List<Sighting> getAllSightings() {
        return sightingService.findAll();
    }

    // UPDATE
    @PutMapping("/{id}")
    public Sighting updateSighting(@PathVariable Long id, @RequestBody Sighting updated) {
        return sightingService.update(id, updated);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public String deleteSighting(@PathVariable Long id) {
        sightingService.delete(id);
        return "Deleted sighting " + id;
    }
}
