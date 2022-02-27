package flo.no.kanji.business.validator;

import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import flo.no.kanji.business.constants.CharacterType;
import flo.no.kanji.util.CharacterUtils;

@Component
public class JapaneseCharacterFormatValidator
	implements ConstraintValidator<JapaneseCharacterFormat, Object>{
	
	private CharacterType format;
	
	public void initialize(JapaneseCharacterFormat constraintAnnotation) {
        this.format = constraintAnnotation.format();
    }

	@SuppressWarnings("unchecked")
	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context) {
		if (value == null) {
			return true;
		}
		var input = (List<String>) (value instanceof List ? value : List.of(value));
		return !ObjectUtils.isEmpty(input) && input.stream().allMatch(this::isValid);
	}
	
	private boolean isValid(String value) {
		return StringUtils.isNotEmpty(value)
				&& CharacterUtils.getCharacterType(value) == this.format;
	}

}
