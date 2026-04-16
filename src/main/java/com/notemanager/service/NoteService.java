package com.notemanager.service;

import com.notemanager.model.Note;
import com.notemanager.model.User;
import com.notemanager.repository.NoteRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NoteService {

    private final NoteRepository noteRepository;
    private final UserService userService;

    public NoteService(NoteRepository noteRepository, UserService userService) {
        this.noteRepository = noteRepository;
        this.userService = userService;
    }

    public List<Note> getNotesByUsername(String username) {
        User user = userService.getUserByUsername(username);
        return noteRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
    }

    public Note createNote(String username, Note note) {
        User user = userService.getUserByUsername(username);
        validateNote(note);

        Note newNote = new Note();
        newNote.setTitle(note.getTitle().trim());
        newNote.setContent(note.getContent().trim());
        newNote.setUserId(user.getId());
        newNote.setCreatedAt(LocalDateTime.now());

        return noteRepository.save(newNote);
    }

    public Note updateNote(String username, Long id, Note updatedNote) {
        User user = userService.getUserByUsername(username);
        validateNote(updatedNote);

        Note existingNote = noteRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Note not found"));

        existingNote.setTitle(updatedNote.getTitle().trim());
        existingNote.setContent(updatedNote.getContent().trim());

        return noteRepository.save(existingNote);
    }

    public void deleteNote(String username, Long id) {
        User user = userService.getUserByUsername(username);

        Note note = noteRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Note not found"));

        noteRepository.delete(note);
    }

    private void validateNote(Note note) {
        if (note == null) {
            throw new IllegalArgumentException("Note data is required");
        }

        if (isBlank(note.getTitle()) || isBlank(note.getContent())) {
            throw new IllegalArgumentException("Title and content are required");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
