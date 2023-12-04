package flo.no.kanji.business.validator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import flo.no.kanji.business.constants.CharacterType;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * Annotation used for validating input Japanese format of given String object
 * @author Florian
 */
@Documented
@Constraint(validatedBy = JapaneseCharacterFormatValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface JapaneseCharacterFormat {
	String message() default "Invalid japanese format";
	CharacterType[] format();
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
