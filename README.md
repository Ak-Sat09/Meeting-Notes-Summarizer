# Meeting Notes Summarizer

[YouTube Demo 🎥](https://youtu.be/iIjA2DUcw5Q)

---

## **Project Overview**
Meeting Notes Summarizer is an AI-powered tool that converts long meeting transcripts into concise summaries, highlighting **key points** and **action items**. It can optionally send the summary via **email** to recipients.

This project is built with **Spring Boot (Java 17)**, **WebClient** for API calls, and **JavaMailSender** for email automation.

---

## **Requirements**

### **Functional Requirements**
- Input: Raw meeting transcript (text)
- Output: Short summary with key points and action items
- Email feature: Send summary to multiple recipients
- Integration with AI API for summarization
- Split large transcripts into manageable chunks for API processing

### **Non-Functional Requirements**
- Performance: Summarization for large transcripts should be handled efficiently
- Reliability: Email must be sent only if valid recipients exist
- Security: API keys and email credentials handled securely

---

## **High-Level Design (HLD)**

      +---------------------+
      |  User / Frontend    |
      +---------------------+
                |
                v
      +---------------------+
      |  NotesController    |
      +---------------------+
                |
                v
      +---------------------+
      |    NotesService     |
      | - Transcript split  |
      | - AI Summarization  |
      | - Email sending     |
      +---------------------+
                |
                v
   +-------------------------+
   |   Gemini API / Email    |
   +-------------------------+

- **Controller Layer:** Handles API requests and responses  
- **Service Layer:** Contains main logic (split, summarize, email)  
- **External Services:** Gemini AI API and SMTP Email server  

---

## **Low-Level Design (LLD)**

### **Classes & Responsibilities**
1. **NotesController**
   - Endpoint: `/api/notes/convert-and-send`
   - Handles POST requests with transcript and email list
2. **NotesService**
   - `processAndSend(TranscriptRequest req)`: Main method
   - `splitTranscript(String text, int chunkSize)`: Splits long transcript
   - `callGeminiAPI(String chunk)`: Calls AI API and retrieves summary
   - `sendEmail(List<String> to, String subject, String body)`: Sends summary via email
3. **TranscriptRequest**
   - Fields: `String transcript`, `List<String> emails`

---

## **System Design**

- **Architecture:** Monolithic Spring Boot application
- **External Integrations:** 
  - Gemini AI API for summarization
  - SMTP server for sending emails
- **Concurrency Handling:** Transcript split allows chunked API calls
- **Scalability Considerations:** Can move to microservices if transcript processing or email volume increases
- **Security:** API keys can be managed via `application.properties` or environment variables (avoid hardcoding)

---

## **Setup & Run**

### **1. Clone Repo**
```bash
git clone https://github.com/Ak-Sat09/Meeting-Notes-Summarizer
cd MeetingNotesSummerizer
