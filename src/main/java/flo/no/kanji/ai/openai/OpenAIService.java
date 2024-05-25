package flo.no.kanji.ai.openai;

import com.fasterxml.jackson.databind.ObjectMapper;
import flo.no.kanji.ai.AIService;
import flo.no.kanji.ai.Agent;
import flo.no.kanji.ai.Message;
import flo.no.kanji.ai.openai.model.MessageInput;
import flo.no.kanji.ai.openai.model.Thread;
import flo.no.kanji.ai.openai.model.run.MessageDelta;
import flo.no.kanji.ai.openai.model.run.MessageType;
import flo.no.kanji.ai.openai.model.run.Run;
import flo.no.kanji.ai.openai.model.run.ThreadMessage;
import flo.no.kanji.business.model.conversation.ChatMessage;
import flo.no.kanji.integration.entity.conversation.ChatSessionEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.Objects;

@Service
@Slf4j
public class OpenAIService implements AIService {

    private final HttpHeaders defaultHeaders;

    @Value("${openai.api.endpoint.thread}")
    private String THREADS_ENDPOINT;

    @Value("${openai.api.endpoint.messages}")
    private String MESSAGES_ENDPOINT;

    @Value("${openai.api.endpoint.runs}")
    private String RUNS_ENDPOINT;

    @Autowired
    private Environment env;

    @Autowired
    private WebClient webClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    public OpenAIService(@Value("${openai.api.key}") String apiKey) {
        defaultHeaders = new HttpHeaders();
        defaultHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey);
        defaultHeaders.add("OpenAI-Beta", "assistants=v2");
        defaultHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
    }

    @Override
    public String createSession(Agent agent) {
        var thread = post(THREADS_ENDPOINT, new Thread(), new ParameterizedTypeReference<Thread>() {
        });
        return thread.getId();
    }

    @Override
    public Flux<Message> sendMessage(ChatSessionEntity session, String message) {
        createMessage(session.getRemoteSessionId(), message);
        return run(session);
    }

    @Override
    public Flux<Message> retry(ChatSessionEntity session) {
        return run(session);
    }

    private void createMessage(final String threadId, final String message) {
        var messageInput = new MessageInput("user", message);
        var url = String.format(MESSAGES_ENDPOINT, threadId);

        post(url, messageInput, new ParameterizedTypeReference<>() {
        });
    }

    private Flux<Message> run(final ChatSessionEntity session) {
        String assistantId = switch (session.getAgent()) {
            case RESTAURANT -> env.getProperty("openai.api.assistant.restaurant.id");
        };
        var url = String.format(RUNS_ENDPOINT, session.getRemoteSessionId());
        var run = new Run(assistantId, true);

        var type = new ParameterizedTypeReference<ServerSentEvent<String>>() {
        };

        return webClient.post()
                .uri(url)
                .headers(httpHeaders -> httpHeaders.addAll(defaultHeaders))
                .bodyValue(run)
                .retrieve()
                .bodyToFlux(type)
                .filter(evt -> MessageType.getMessagesTypes.contains(evt.event()))
                .map(this::mapEventToMessage);

    }

    private <T> T post(String url, Object body, ParameterizedTypeReference<T> returnType) {
        return Objects.requireNonNull(webClient.post()
                .uri(url)
                .headers(httpHeaders -> httpHeaders.addAll(defaultHeaders))
                .bodyValue(body)
                .retrieve()
                .toEntity(returnType)
                .block()).getBody();
    }

    private Message mapEventToMessage(final ServerSentEvent<String> event) {
        try {
            assert event.event() != null;
            return switch (event.event()) {
                case MessageType.THREAD_MESSAGE -> {
                    var threadMessage = objectMapper.readValue(event.data(), ThreadMessage.class);
                    var messageValue = objectMapper.readValue(
                            threadMessage.getContent().getLast().getText().getValue(), ChatMessage.class);

                    yield Message.builder()
                            .id(threadMessage.getId())
                            .messageValue(messageValue)
                            .build();
                }
                case MessageType.THREAD_MESSAGE_DELTA -> {
                    var streamMessage = objectMapper.readValue(event.data(), MessageDelta.class);
                    var streamValue = streamMessage.getDelta()
                            .getContent().getFirst().getText().getValue();

                    yield Message.builder()
                            .id(streamMessage.getId())
                            .isStream(true)
                            .streamValue(streamValue)
                            .build();
                }
                case MessageType.DONE -> Message.builder().isDone(true).build();
                default -> throw new IllegalArgumentException("Unknown");
            };
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
