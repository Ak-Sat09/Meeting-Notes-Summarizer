package com.example.MeetingNotesSummerizer;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/notes")
@RequiredArgsConstructor
@CrossOrigin("*")
public class NotesController {

    private final NotesService notesService;

    @PostMapping("/convert-and-send")
    public Map<String, Object> handle(@RequestBody TranscriptRequest req) {
        String summary = notesService.processAndSend(req);
        return Map.of("ok", true, "summary", summary);
    }
}
