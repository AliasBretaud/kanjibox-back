package flo.no.kanji.business.constants;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.stream.Stream;

public enum Language {
    JA("ja"),
    EN("en"),
    FR("fr");

    private final String value;

    Language(String label) {
        this.value = label;
    }

    public static Language fromLabel(String label) {
        return Stream.of(Language.values())
                .filter(v -> v.getValue().equals(label))
                .findAny().orElseThrow();
    }

    @JsonValue
    public String getValue() {
        return this.value;
    }

    @JsonCreator
    public static Language forValue(String value) {
        return Arrays.stream(Language.values())
                .filter(tr -> tr.getValue().equals(value))
                .findFirst()
                .orElseThrow(); // depending on requirements: can be .orElse(null);
    }

    @Override
    public String toString() {
        return this.value;
    }
}
