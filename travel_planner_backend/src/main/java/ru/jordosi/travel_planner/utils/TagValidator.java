package ru.jordosi.travel_planner.utils;

import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class TagValidator {
    private static final Set<String> ALLOWED_TAGS = Set.of(
            "Европа", "пляжный отдых", "Африка", "гастротуры",
            "история", "культурный отдых", "Латинская Америка",
            "места из кино" //to be extended...
    );

    public void validate(Set<String> tags) {
        if (tags != null && !tags.isEmpty()) {
            for (String tag : tags) {
                if (!ALLOWED_TAGS.contains(tag)) {
                    throw new IllegalArgumentException("Unsupported tag: " + tag);
                }
            }
        }
    }
}
