package br.eti.arthurgregorio.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

@Entity
@Table(name = "countries")
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Country extends PersistentEntity {

    public Country() {
    }

    public Country(String name) {
        this.name = name;
    }

    @Getter
    @Setter
    @JsonIgnore
    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Setter
    @Getter
    @Embedded
    private Currency currency;

    @JsonProperty("currencies")
    public void unmarshalCurrencies(Map<String, Object> currencies) {

        final var countryCurrency = new Currency();

        for (Map.Entry<String, Object> entry : currencies.entrySet()) {
            countryCurrency.setName(entry.getKey());
            final var currencyDetail = (Map<String, String>) entry.getValue();
            countryCurrency.setSymbol(currencyDetail.get("symbol"));
        }

        this.currency = countryCurrency;
    }
}
