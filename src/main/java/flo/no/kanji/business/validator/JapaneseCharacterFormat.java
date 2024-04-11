package flo.no.kanji.business.validator;

import flo.no.kanji.business.constants.CharacterType;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Annotation used for validating input Japanese format of given String object
 *
 * @author Florian
 */
@Documented
@Constraint(validatedBy = JapaneseCharacterFormatValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface JapaneseCharacterFormat {
    String message() default "Invalid japanese format";

    CharacterType[] format();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
