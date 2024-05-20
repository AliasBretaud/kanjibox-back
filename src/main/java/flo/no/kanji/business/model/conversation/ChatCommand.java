package flo.no.kanji.business.model.conversation;

import java.util.Objects;
import java.util.stream.Stream;

public enum ChatCommand {
    START("START"), NEXT("NEXT");

    private final String command;

    ChatCommand(final String command) {
        this.command = command;
    }

    public static ChatCommand fromValue(final String value) {
        return Stream.of(ChatCommand.values())
                .filter(c -> Objects.equals(c.command, value))
                .findFirst().orElse(null);
    }
}
