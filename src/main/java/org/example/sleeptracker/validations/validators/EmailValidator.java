package org.example.sleeptracker.validations.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.example.sleeptracker.models.ValidationRuleEnum;
import org.example.sleeptracker.validations.PatternValidator;
import org.example.sleeptracker.validations.ValidationRuleService;
import org.example.sleeptracker.validations.annotations.Email;

@RequiredArgsConstructor
public class EmailValidator implements ConstraintValidator<Email, String> {

    private final ValidationRuleService validationRuleService;

    @Override
    public boolean isValid(String valueToValid, ConstraintValidatorContext constraintValidatorContext) {
        return PatternValidator.isValid(valueToValid, validationRuleService.findByRuleName(ValidationRuleEnum.EMAIL));
    }
}

