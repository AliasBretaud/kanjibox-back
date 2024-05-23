package flo.no.kanji.ai.openai.model.run;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class ThreadMessage extends Message {
    private String assistantId;
    private String threadId;
    private String runId;
    private String status;
    private String role;
    private List<Content> content;

    @Data
    public static class Content {
        private String type;
        private Text text;

        @Data
        public static class Text {
            private String value;
        }
    }
}
