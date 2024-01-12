package flo.no.kanji.business.model.quiz;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Question {

    private Long id;

    private String text;

    private Integer kanjiPosition;

    private List<String> propositions;

    private Answer answer;
}
