package flo.no.kanji.business.model.quiz;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Quiz {

    private Long id;

    private String name;

    private String theme;

    private Integer nbQuestions;

    private Integer correctAnswersCount;

    private Integer incorrectAnswersCount;
}
