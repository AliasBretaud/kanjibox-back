package flo.no.kanji.ai.openai.model.run;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ThreadMessage extends Message {
    private String assistantId;
    private String threadId;
    private String runId;
    private String status;
    private String role;
    private List<Content> content;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Content {
        private String type;
        private Text text;

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Text {
            private String value;
        }
    }
}
