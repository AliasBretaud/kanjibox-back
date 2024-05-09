package flo.no.kanji.web.controller;

import flo.no.kanji.business.model.conversation.ChatMessage;
import flo.no.kanji.business.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/conversations")
public class ChatController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ChatService chatService;

    @MessageMapping("/chat")
    public void processMessage(@Payload ChatMessage chatMessage) {
        var session = chatMessage.getConversationId();
        var userMessage = chatService.sendMessage(session, chatMessage);
        messagingTemplate.convertAndSendToUser(session.toString(), "/messages", userMessage);
    }

}
