package flo.no.kanji.web.controller;

import flo.no.kanji.business.model.conversation.ChatMessage;
import flo.no.kanji.business.model.conversation.ChatSession;
import flo.no.kanji.business.service.ChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/conversations")
@Slf4j
public class ConversationController {

    @Autowired
    private ChatService chatService;

    @GetMapping
    private List<ChatSession> getSessions() {
        return chatService.getSessionsList();
    }

    @PostMapping
    ChatSession createSession(@RequestBody final ChatSession session) {
        return chatService.createSession(session.getAgent());
    }

    @GetMapping("/{sessionId}/messages")
    List<ChatMessage> getMessages(@PathVariable("sessionId") final UUID sessionId) {
        return chatService.getSessionMessages(sessionId);
    }

    @PostMapping(path = "/{sessionId}/messages", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    SseEmitter sendMessage(@PathVariable("sessionId") final UUID sessionId,
                           @RequestBody ChatMessage message) {
        return chatService.sendMessage(sessionId, message);
    }

    @DeleteMapping("/{sessionId}")
    void deleteSession(@PathVariable("sessionId") final UUID sessionId) {
        chatService.deleteSession(sessionId);
    }
}
