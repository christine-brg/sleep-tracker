package org.example.sleeptracker.validations;

import org.example.sleeptracker.models.ValidationRuleEnum;

public interface ValidationRuleService {
    String findByRuleName(ValidationRuleEnum ruleName);
}