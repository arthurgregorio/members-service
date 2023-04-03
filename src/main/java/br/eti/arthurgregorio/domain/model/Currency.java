package br.eti.arthurgregorio.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
public class Currency {

    public Currency() {
    }

    public Currency(String name, String symbol) {
        this.name = name;
        this.symbol = symbol;
    }

    @Getter
    @Setter
    @Column(name = "currency_name", length = 150, nullable = false)
    private String name;
    @Getter
    @Setter
    @Column(name = "symbol", length = 3, nullable = false)
    private String symbol;
}
