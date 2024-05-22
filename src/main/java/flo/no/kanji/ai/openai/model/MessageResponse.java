package flo.no.kanji.ai.openai.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageResponse {
    private String id;

    private String role;

    @JsonProperty("thread_id")
    private String treadId;

    private java.util.List<Content> content;

    @JsonProperty("assistant_id")
    private String assistantId;

    @JsonProperty("run_id")
    private String runId;

    @Data
    public static class Content {
        private String type;
        private TextContent text;
    }

    @Data
    public static class TextContent {
        private String value;
    }

    @Data
    public static class List {
        private java.util.List<MessageResponse> data;
    }
}
