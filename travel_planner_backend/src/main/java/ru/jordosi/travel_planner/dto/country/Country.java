package ru.jordosi.travel_planner.dto.country;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Country {
    private String code;
    private String name;
    private String flagUrl;
}
