package flo.no.kanji.ai.openai.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Thread {

    private String id;

    private List<MessageInput> messages;
}
