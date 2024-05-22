package flo.no.kanji.ai.openai.model.run;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Message {

    private String id;

    private String object;
}