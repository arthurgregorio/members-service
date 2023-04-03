package br.eti.arthurgregorio.domain.services;

import br.eti.arthurgregorio.domain.exceptions.CountryNotFoundException;
import br.eti.arthurgregorio.domain.model.Country;
import br.eti.arthurgregorio.infrastructure.http.RestCountriesClient;
import br.eti.arthurgregorio.infrastructure.repositories.CountryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CountryService {

    private final CountryRepository countryRepository;

    private final RestCountriesClient restCountriesClient;

    @Transactional
    public Country fetchAndSave(String countryName) {

        final var countries = restCountriesClient.getByName(countryName);

        final var country = countries.stream()
                .findFirst()
                .orElseThrow(() -> new CountryNotFoundException("No country found with name [%s]".formatted(countryName)));

        country.setName(countryName);

        return countryRepository.save(country);
    }
}
