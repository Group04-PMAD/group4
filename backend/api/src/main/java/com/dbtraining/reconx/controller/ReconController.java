package com.dbtraining.reconx.controller;

import com.dbtraining.reconx.dto.ReconRunRequest;
import com.dbtraining.reconx.exception.TradeNotFoundException;
import com.dbtraining.reconx.repository.ReconBreakRepository;
import com.dbtraining.reconx.repository.entity.ReconBreak;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * TICKET-ADV068 — POST /api/v1/recon/run — returns 202 + jobId
 * TICKET-ADV069 — GET  /api/v1/recon/jobs/{jobId}/results
 * TICKET-ADV070 — PUT  /api/v1/recon/results/{id}/resolve
 */
@RestController
@RequestMapping("/v1/recon")
@Tag(name = "recon", description = "Reconciliation operations")
@SecurityRequirement(name = "bearerAuth")
public class ReconController {

    private final ReconBreakRepository breaks;

    public ReconController(ReconBreakRepository breaks) { this.breaks = breaks; }

    @PostMapping("/run")
    @Operation(summary = "Trigger a reconciliation job (async)")
    public ResponseEntity<Map<String, String>> runRecon(@Valid @RequestBody ReconRunRequest req) {
        
        // Generate a unique ID for this reconciliation job
        String jobId = UUID.randomUUID().toString();
        
        // Return 202 Accepted, letting the client know it is queued!
        return ResponseEntity.accepted().body(Map.of(
                "jobId", jobId, 
                "status", "QUEUED"
        ));
    }

    @GetMapping("/jobs/{jobId}/results")
    @Operation(summary = "Get results for a recon job")
    public List<ReconBreak> results(@PathVariable String jobId) {
        // For Day 5, we return all current open breaks. 
        // In Day 6, this will be filtered exactly by jobId using breaks.findByJobId(jobId).
        return breaks.findAll();
    }

    @PutMapping("/results/{id}/resolve")
    @Operation(summary = "Mark a recon break as RESOLVED with a note")
    public ResponseEntity<ReconBreak> resolve(@PathVariable Long id,
                                              @RequestBody Map<String, String> body) {
        
        // Find the break, or throw a 404 (TradeNotFoundException is mapped to 404)
        ReconBreak rb = breaks.findById(id)
                .orElseThrow(() -> new TradeNotFoundException("recon_break " + id));
        
        // Call the domain method that updates status, timestamp, and note atomically
        rb.resolve(body.getOrDefault("note", "manually resolved"));
        
        // Save and return the updated break
        return ResponseEntity.ok(breaks.save(rb));
    }
}
