package flo.no.kanji.web.controller;

import flo.no.kanji.business.model.conversation.ChatMessage;
import flo.no.kanji.business.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/messages")
public class MessageController {

    @Autowired
    private ChatService chatService;

    @GetMapping("/{id}")
    ChatMessage getMessage(@PathVariable("id") final Long id) {
        return chatService.getMessage(id);
    }

}