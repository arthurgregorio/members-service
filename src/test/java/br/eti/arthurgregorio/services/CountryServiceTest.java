package br.eti.arthurgregorio.services;

import br.eti.arthurgregorio.BaseIntegrationTest;
import br.eti.arthurgregorio.domain.exceptions.CountryNotFoundException;
import br.eti.arthurgregorio.domain.services.CountryService;
import br.eti.arthurgregorio.fixture.CountryFixture;
import br.eti.arthurgregorio.infrastructure.http.RestCountriesClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class CountryServiceTest extends BaseIntegrationTest {

    @MockBean
    private RestCountriesClient restCountriesClient;

    @Autowired
    private CountryService countryService;

    @Test
    void fetchAndSave_shouldFetchTheCountry_saveAndReturIt() {

        final var country = CountryFixture.create();

        when(restCountriesClient.getByName(anyString())).thenReturn(List.of(country));

        final var found = countryService.fetchAndSave("Brasil");

        assertThat(found)
                .hasFieldOrPropertyWithValue("name", country.getName())
                .hasFieldOrPropertyWithValue("currency", country.getCurrency());
    }

    @Test
    void fetchAndSave_noCountryFound_shouldThrownError() {

        when(restCountriesClient.getByName(anyString())).thenReturn(List.of());

        assertThatThrownBy(() -> countryService.fetchAndSave("Brasil"))
                .isInstanceOf(CountryNotFoundException.class);
    }
}
