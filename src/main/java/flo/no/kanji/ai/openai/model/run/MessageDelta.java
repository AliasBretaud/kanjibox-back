package flo.no.kanji.ai.openai.model.run;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class MessageDelta extends Message {
    private Delta delta;

    @Data
    public static class Delta {
        private List<Content> content;

        @Data
        public static class Content {
            private int index;
            private String type;
            private Text text;

            @Data
            public static class Text {
                private String value;
            }
        }
    }
}
