package flo.no.kanji.ai;

import flo.no.kanji.business.model.conversation.ChatMessage;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Message {

    private String id;

    private boolean isDone;

    private boolean isStream;

    private String streamValue;

    private ChatMessage messageValue;
}
