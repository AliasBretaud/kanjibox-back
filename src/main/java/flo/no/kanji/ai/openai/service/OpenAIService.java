package flo.no.kanji.ai.openai.service;

import flo.no.kanji.ai.Agent;
import flo.no.kanji.ai.openai.model.MessageInput;
import flo.no.kanji.ai.openai.model.Thread;
import flo.no.kanji.ai.openai.model.run.Run;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;


@Service
@Slf4j
public class OpenAIService {

    private final HttpHeaders defaultHeaders;

    private final WebClient webClient = WebClient.builder().build();

    @Value("${openai.api.endpoint.thread}")
    private String THREADS_ENDPOINT;

    @Value("${openai.api.endpoint.messages}")
    private String MESSAGES_ENDPOINT;

    @Value("${openai.api.endpoint.runs}")
    private String RUNS_ENDPOINT;

    @Autowired
    private Environment env;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    public OpenAIService(@Value("${openai.api.key}") String apiKey) {
        defaultHeaders = new HttpHeaders();
        defaultHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey);
        defaultHeaders.add("OpenAI-Beta", "assistants=v2");
        defaultHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
    }

    public Thread createThread() {
        return post(THREADS_ENDPOINT, new Thread(), new ParameterizedTypeReference<>() {
        });
    }

    public void createMessage(final String threadId, final String message) {
        var messageInput = new MessageInput("user", message);
        var url = String.format(MESSAGES_ENDPOINT, threadId);

        post(url, messageInput, new ParameterizedTypeReference<>() {
        });
    }

    public Flux<ServerSentEvent<String>> run(final String threadId, final Agent agent) {
        String assistantId = switch (agent) {
            case RESTAURANT -> env.getProperty("openai.api.assistant.restaurant.id");
        };
        var url = String.format(RUNS_ENDPOINT, threadId);
        var run = new Run(assistantId, true);

        var type = new ParameterizedTypeReference<ServerSentEvent<String>>() {
        };

        return webClient.post()
                .uri(url)
                .headers(httpHeaders -> httpHeaders.addAll(defaultHeaders))
                .bodyValue(run)
                .retrieve()
                .bodyToFlux(type);

    }

    private <T> T post(String url, Object body, ParameterizedTypeReference<T> returnType) {
        var entity = new HttpEntity<>(body, defaultHeaders);
        return restTemplate.exchange(url, HttpMethod.POST, entity, returnType).getBody();
    }
}
