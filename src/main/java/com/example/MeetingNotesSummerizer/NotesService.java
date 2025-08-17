package com.example.MeetingNotesSummerizer;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotesService {

    private final JavaMailSender mailSender;

    // Hardcoded SMTP sender email
    private final String senderEmail = "@gmail.com";

    private static final String PROMPT = "Summarize the transcript into short meeting notes with key points and action items.";

    private final WebClient webClient = WebClient.create();

    public String processAndSend(TranscriptRequest req) {
        String transcript = req.getTranscript();
        List<String> chunks = splitTranscript(transcript, 5000);

        StringBuilder fullSummary = new StringBuilder();
        for (String chunk : chunks) {
            fullSummary.append(callGeminiAPI(chunk)).append("\n");
        }

        String summary = fullSummary.toString().trim();

        if (req.getEmails() != null && !req.getEmails().isEmpty()) {
            sendEmail(req.getEmails(), "📌 Meeting Notes Summary", summary);
        }

        return summary;
    }

    @SuppressWarnings("rawtypes")
    private String callGeminiAPI(String textChunk) {
        Map<String, Object> requestBody = Map.of(
                "contents", new Object[] {
                        Map.of("parts", new Object[] {
                                Map.of("text", PROMPT + "\n\n" + textChunk)
                        })
                });

        Map response = webClient.post()
                .uri(geminiUrl)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (response != null && response.get("candidates") != null) {
            List candidates = (List) response.get("candidates");
            if (!candidates.isEmpty()) {
                Map first = (Map) candidates.get(0);
                Map content = (Map) first.get("content");
                List parts = (List) content.get("parts");
                if (!parts.isEmpty()) {
                    return (String) ((Map) parts.get(0)).get("text");
                }
            }
        }
        return "[No summary returned]";
    }

    private void sendEmail(List<String> to, String subject, String body) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to.toArray(new String[0]));
        msg.setSubject(subject);
        msg.setText(body);
        msg.setFrom(senderEmail); // hardcoded sender
        mailSender.send(msg);
    }

    private List<String> splitTranscript(String text, int chunkSize) {
        List<String> chunks = new ArrayList<>();
        for (int i = 0; i < text.length(); i += chunkSize) {
            chunks.add(text.substring(i, Math.min(text.length(), i + chunkSize)));
        }
        return chunks;
    }
}
