package ru.jordosi.travel_planner.service;

import ru.jordosi.travel_planner.dto.country.Country;

import java.util.List;
import java.util.Optional;

public interface NationalityService {
    boolean validateNationality(String countryCode);
    List<Country> getAllCountries();
    Optional<Country> getCountryByCode(String code);
    List<String> getValidCountryCodes();
}
