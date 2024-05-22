package flo.no.kanji.ai.openai.model.run;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MessageDelta extends Message {
    private Delta delta;

    @Data
    public static class Delta {
        private List<Content> content;

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Content {
            private int index;
            private String type;
            private Text text;

            @Data
            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class Text {
                private String value;
            }
        }
    }
}
