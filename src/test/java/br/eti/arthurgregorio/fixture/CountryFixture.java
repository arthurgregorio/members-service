package br.eti.arthurgregorio.fixture;

import br.eti.arthurgregorio.domain.model.Country;
import br.eti.arthurgregorio.domain.model.Currency;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CountryFixture {

    public static Country create() {

        final var country = new Country("Brasil");
        country.setCurrency(new Currency("BRL", "R$"));

        return country;
    }
}
