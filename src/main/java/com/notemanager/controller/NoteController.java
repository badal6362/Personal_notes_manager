package com.notemanager.controller;

import com.notemanager.model.Note;
import com.notemanager.service.NoteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping
    public ResponseEntity<?> getAllNotes(@RequestParam String username) {
        try {
            List<Note> notes = noteService.getNotesByUsername(username);
            return ResponseEntity.ok(notes);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    @PostMapping
    public ResponseEntity<?> createNote(@RequestParam String username, @RequestBody Note note) {
        try {
            Note savedNote = noteService.createNote(username, note);
            return ResponseEntity.ok(savedNote);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateNote(@PathVariable Long id,
                                        @RequestParam String username,
                                        @RequestBody Note note) {
        try {
            Note updatedNote = noteService.updateNote(username, id, note);
            return ResponseEntity.ok(updatedNote);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteNote(@PathVariable Long id, @RequestParam String username) {
        try {
            noteService.deleteNote(username, id);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Note deleted successfully"
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }
}
