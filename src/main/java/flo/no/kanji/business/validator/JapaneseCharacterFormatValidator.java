package flo.no.kanji.business.validator;

import flo.no.kanji.business.constants.CharacterType;
import flo.no.kanji.util.CharacterUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.Arrays;
import java.util.List;

/**
 * Japanese validating format class validation
 *
 * @author Florian
 */
@Component
public class JapaneseCharacterFormatValidator implements ConstraintValidator<JapaneseCharacterFormat, Object> {

    /** Japanese format/alphabet used for validating **/
    private CharacterType[] formats;

    /**
     * @{inheritDoc}
     */
    public void initialize(JapaneseCharacterFormat constraintAnnotation) {
        this.formats = constraintAnnotation.format();
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        var input = (List<String>) (value instanceof List ? value : List.of(value));
        return ObjectUtils.isEmpty(input) || input.stream().allMatch(this::isValid);
    }

    /**
     * Validates the input string compared to the given format
     *
     * @param value Input String
     * @return True if the input String matches the given format/alphabet, false otherwise
     */
    private boolean isValid(String value) {
        return StringUtils.isNotEmpty(value)
                && Arrays.stream(this.formats).anyMatch(f -> f == CharacterUtils.getCharacterType(value));
    }

}
