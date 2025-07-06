package ru.jordosi.travel_planner.service.impl;

import io.micrometer.common.util.StringUtils;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import ru.jordosi.travel_planner.dto.country.Country;
import ru.jordosi.travel_planner.service.NationalityService;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class NationalityServiceImpl implements NationalityService {
    private final RestTemplate restTemplate;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String API_URL = "https://restcountries.com/v3.1";
    private static final String COUNTRY_VALIDITY_KEY_PREFIX = "country:valid:";
    private static final Duration CACHE_TTL = Duration.ofDays(30);

    @Override
    public boolean validateNationality(String countryCode) {
        if (StringUtils.isEmpty(countryCode)) {
            return false;
        }

        String normalizedCode = countryCode.trim().toUpperCase();

        Boolean isValid = (Boolean) redisTemplate.opsForValue().get("country:valid:" + normalizedCode);
        if (isValid != null) {
            return isValid;
        }

        boolean validationResult = checkCountryExistence(normalizedCode);

        redisTemplate.opsForValue().set(
                "country:valid:" + normalizedCode,
                validationResult,
                Duration.ofDays(30)
        );

        return validationResult;
    }

    private boolean checkCountryExistence(String code) {
        try {
            ResponseEntity<Void> response = restTemplate.exchange(
                    "https://restcountries.com/v3.1/alpha/{code}",
                    HttpMethod.HEAD,
                    null,
                    Void.class,
                    code
            );
            return response.getStatusCode().is2xxSuccessful();
        } catch (HttpClientErrorException.NotFound e) {
            return false;
        }
    }

    @Override
    @Cacheable(value = "countries", key = "'all'", cacheManager = "redisCacheManager")
    public List<Country> getAllCountries() {
        try {
            CountryDto[] response = restTemplate.getForObject(
                    API_URL + "/all?fields=cca2,name,flags",
                    CountryDto[].class
            );

            return Arrays.stream(response)
                    .map(dto -> new Country(
                            dto.getCca2(),
                            dto.getName().getCommon(),
                            dto.getFlags().getPng()))
                    .filter(c -> c.getName() != null)
                    .sorted(Comparator.comparing(Country::getName))
                    .toList();
        } catch (Exception e) {
            log.error("Failed to fetch countries", e);
            return Collections.emptyList();
        }
    }

    @Override
    @Cacheable(value = "country", key = "#code", cacheManager = "redisCacheManager")
    public Optional<Country> getCountryByCode(String code) {
        try {
            ResponseEntity<CountryDto> response = restTemplate.getForEntity(API_URL+"/alpha/"+ code+ "?fields=cca2,name,flags", CountryDto.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                CountryDto cd = response.getBody();
                return Optional.of(new Country(
                        cd.getCca2(),
                        cd.getName().getCommon(),
                        cd.getFlags().getPng()
                ));
            }
            log.error("Failed to parse country by code: {}", code);
            return Optional.empty();
        }
        catch (Exception e) {
            log.error("Failed to fetch country by code: {}", code);
            return Optional.empty();
        }
    }

    @Override
    public List<String> getValidCountryCodes() {
        return getAllCountries().stream()
                .map(Country::getCode)
                .toList();
    }

    @PostConstruct
    public void init() {
        preloadCountryCodes();
    }

    private void preloadCountryCodes() {
        try {
            log.info("Preloading country codes...");
            String url = API_URL + "/all?fields=cca2";
            ResponseEntity<CountryCodeDto[]> response = restTemplate.getForEntity(url, CountryCodeDto[].class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                List<String> codes = Arrays.stream(response.getBody())
                        .map(c -> c.getCca2())
                        .filter(Objects::nonNull)
                        .toList();

                log.info("Received {} country codes from API", codes.size());

                int savedCount = 0;
                for (String code : codes) {
                    try {
                        redisTemplate.opsForValue().set(
                                "country:valid:"+code.toUpperCase(),
                                true,
                                Duration.ofDays(30)
                        );
                        savedCount++;
                    }
                    catch (Exception e) {
                        log.error("Failed to save code {} to Redis: {}", code, e.getMessage());
                    }
                }

                log.info("Successfully preloaded {} country codes from API", savedCount);
            }
            else {
                log.error("Failed to fetch countries. Status: {}, Body: {}",
                        response.getStatusCode(), response.getBody());
            }
        } catch (Exception e) {
            log.error("Critical error during country codes preloading", e);
        }
    }

    @Scheduled(fixedRate = 7, timeUnit = TimeUnit.DAYS)
    @CacheEvict(value = {"countries", "country"}, allEntries = true)
    public void refreshCache() {
        log.info("Refreshing countries cache");
        preloadCountryCodes();
    }

    @Getter
    private static class CountryCodeDto {
        private String cca2;
    }

    @Getter
    private static class CountryDto {
        private String cca2;
        private CountryName name;
        private CountryFlags flags;

        @Getter
        private static class CountryName {
            private String common;
        }

        @Getter
        private static class CountryFlags {
            private String png;
            private String svg;
            private String alt;
        }
    }
}
