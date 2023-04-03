package br.eti.arthurgregorio.infrastructure.http;

import br.eti.arthurgregorio.domain.exceptions.CountryNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import reactor.core.publisher.Mono;

@Component
public class RestCountriesClientFactory {

    @Bean
    RestCountriesClient createClient(@Value("${application.rest-countries-url}") String baseUrl) {

        final WebClient webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultStatusHandler(status -> status == HttpStatus.NOT_FOUND,
                        response -> Mono.error(new CountryNotFoundException("No country found for the provided name")))
                .build();

        final var proxyFactory = HttpServiceProxyFactory
                .builder(WebClientAdapter.forClient(webClient))
                .build();

        return proxyFactory.createClient(RestCountriesClient.class);
    }
}
