package br.eti.arthurgregorio.infrastructure.http;

import br.eti.arthurgregorio.domain.model.Country;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.List;

@HttpExchange("/v3.1")
public interface RestCountriesClient {

    @GetExchange("/name/{countryName}")
    List<Country> getByName(@PathVariable("countryName") String countryName);
}
