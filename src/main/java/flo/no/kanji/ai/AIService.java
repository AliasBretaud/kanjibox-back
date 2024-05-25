package flo.no.kanji.ai;

import flo.no.kanji.integration.entity.conversation.ChatSessionEntity;
import reactor.core.publisher.Flux;

public interface AIService {

    String createSession(final Agent agent);

    Flux<Message> sendMessage(final ChatSessionEntity session, final String message);

    Flux<Message> retry(final ChatSessionEntity session);
}
