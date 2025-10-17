package org.example.sleeptracker.validations;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternValidator {
    public static boolean isValid(String valueToValid, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(valueToValid);
        return matcher.matches();
    }
}
