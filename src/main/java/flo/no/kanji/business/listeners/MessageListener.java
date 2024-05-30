package flo.no.kanji.business.listeners;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import flo.no.kanji.ai.Message;
import flo.no.kanji.business.mapper.ChatMessageMapper;
import flo.no.kanji.business.model.conversation.ChatMessage;
import flo.no.kanji.integration.entity.conversation.ChatMessageEntity;
import flo.no.kanji.integration.entity.conversation.ChatMessageMistakeEntity;
import flo.no.kanji.integration.repository.ChatMessageRepository;
import flo.no.kanji.util.CharacterUtils;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@Setter
@Slf4j
public class MessageListener {

    private AtomicBoolean streamData;

    private SseEmitter emitter;

    private ChatMessageEntity response;

    private ChatMessageEntity originMessage;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ChatMessageMapper chatMessageMapper;


    public void onNext(Message message) {
        if (message.isDone()) {
            completeStream();
        } else if (message.isStream()) {
            var token = message.getStreamValue();
            toggleStream(token, streamData);
            if (token != null && streamData.get() && CharacterUtils.isJapanese(token)) {
                emitMessage(SseEmitter.event()
                        .id(message.getId())
                        .data(token)
                        .name("ASSISTANT_MESSAGE_DELTA")
                        .build());
            }
        } else {
            try {
                var response = saveResponse(message);
                var stringValue = objectMapper.writeValueAsString(response);
                emitMessage(SseEmitter.event()
                        .id(message.getId())
                        .data(stringValue)
                        .name("ASSISTANT_MESSAGE")
                        .build());
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void onError(Throwable e) {
        completeStream(e);
    }

    private void toggleStream(String token, AtomicBoolean streamData) {
        if ("message".equals(token)) {
            streamData.set(true);
        } else if (token != null && token.contains(",")) {
            streamData.set(false);
        }
    }

    private void completeStream() {
        completeStream(null);
    }

    private void completeStream(final Throwable e) {
        if (e != null) {
            emitter.completeWithError(e);
        } else {
            emitter.complete();
        }
    }

    private void emitMessage(Set<ResponseBodyEmitter.DataWithMediaType> event) {
        try {
            if (event != null) {
                emitter.send(event);
            }
        } catch (IOException e) {
            completeStream(e);
        }
    }

    private ChatMessage saveResponse(Message message) throws JsonProcessingException {
        var messageValue = message.getMessageValue();
        response.setMessage(messageValue.getMessage());
        response.setIsCommand(false);
        response.setIsGenerating(false);
        response = chatMessageRepository.save(response);
        var mistakes = buildMistakes(messageValue);
        if (!mistakes.isEmpty()) {
            response.setMistakes(mistakes);
            originMessage.setMistakes(mistakes);
            originMessage = chatMessageRepository.save(originMessage);
        }

        return chatMessageMapper.toBusinessObject(response);
    }

    private List<ChatMessageMistakeEntity> buildMistakes(final ChatMessage message) {
        if (message.getMistakes() == null) {
            return Collections.emptyList();
        }
        return message.getMistakes().stream()
                .map(m -> ChatMessageMistakeEntity.builder()
                        .input(m.getInput())
                        .reason(m.getReason())
                        .correction(m.getCorrection())
                        .message(originMessage)
                        .build())
                .toList();
    }

}
