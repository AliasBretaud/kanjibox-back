package flo.no.kanji.ai.openai.model.run;

import java.util.List;

public class MessageType {

    public static final String THREAD_MESSAGE_DELTA = "thread.message.delta";

    public static final String THREAD_MESSAGE = "thread.message.completed";

    public static final String DONE = "done";

    public static List<String> getMessagesTypes = List.of(THREAD_MESSAGE_DELTA, THREAD_MESSAGE, DONE);

    private MessageType() {
    }
}
