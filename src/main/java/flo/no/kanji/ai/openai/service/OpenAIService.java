package flo.no.kanji.ai.openai.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import flo.no.kanji.ai.Agent;
import flo.no.kanji.ai.openai.model.MessageInput;
import flo.no.kanji.ai.openai.model.Thread;
import flo.no.kanji.ai.openai.model.run.Run;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import okhttp3.sse.EventSources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
@Slf4j
public class OpenAIService {

    private final Headers headers;
    @Autowired
    private ObjectMapper mapper;
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
        headers = new Headers.Builder()
                .add(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .add("OpenAI-Beta", "assistants=v2")
                .add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
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

    public void runTest(final String threadId, final Agent agent, EventSourceListener listener) throws JsonProcessingException {
        String assistantId = switch (agent) {
            case RESTAURANT -> env.getProperty("openai.api.assistant.restaurant.id");
        };
        var url = String.format(RUNS_ENDPOINT, threadId);
        var run = new Run(assistantId, true);
        var JSON = okhttp3.MediaType.parse("application/json; charset=utf-8");
        var requestBody = RequestBody.create(mapper.writeValueAsString(run), JSON);
        var request = new Request.Builder()
                .url(url)
                .headers(headers)
                .post(requestBody)
                .build();
        var okHttpClient = new OkHttpClient.Builder().build();
        EventSource.Factory factory = EventSources.createFactory(okHttpClient);
        factory.newEventSource(request, listener);
    }

    private <T> T post(String url, Object body, ParameterizedTypeReference<T> returnType) {
        HttpHeaders httpHeaders = new HttpHeaders();
        headers.names().forEach(name -> httpHeaders.put(name, headers.values(name)));
        var entity = new HttpEntity<>(body, new HttpHeaders(httpHeaders));

        return restTemplate.exchange(url, HttpMethod.POST, entity, returnType).getBody();
    }
}
