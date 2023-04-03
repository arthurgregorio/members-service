package br.eti.arthurgregorio.application.mappers;

import br.eti.arthurgregorio.application.payloads.ContractorForm;
import br.eti.arthurgregorio.application.payloads.CountryView;
import br.eti.arthurgregorio.application.payloads.MemberView;
import br.eti.arthurgregorio.domain.model.Country;
import br.eti.arthurgregorio.domain.model.Member;
import br.eti.arthurgregorio.infrastructure.repositories.CountryRepository;
import br.eti.arthurgregorio.application.payloads.EmployeeForm;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@Mapper(config = MappingConfiguration.class)
public abstract class MemberMapper {

    private CountryRepository countryRepository;

    @Mapping(source = "externalId", target = "id")
    @Mapping(source = "country", target = "country", qualifiedByName = "countryMapper")
    public abstract MemberView map(Member member);

    @Mapping(target = "type", constant = "CONTRACTOR")
    @Mapping(source = "tags", target = "tags", qualifiedByName = "tagsMapper")
    @Mapping(source = "country", target = "country", qualifiedByName = "countryMapper")
    public abstract Member map(ContractorForm form);

    @Mapping(target = "type", constant = "EMPLOYEE")
    @Mapping(source = "tags", target = "tags", qualifiedByName = "tagsMapper")
    @Mapping(source = "country", target = "country", qualifiedByName = "countryMapper")
    public abstract Member map(EmployeeForm form);

    @Named("countryMapper")
    Country countryMapper(String countryName) {
        return countryRepository.findByNameIgnoreCase(countryName)
                .orElse(new Country(countryName));
    }

    @Named("tagsMapper")
    List<String> tagsMapper(List<String> tags) {
        return new ArrayList<>(tags);
    }

    @Named("countryMapper")
    CountryView countryMapper(Country country) {
        return new CountryView(country.getName(), country.getCurrency().getName());
    }

    @Autowired
    public void setCountryRepository(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }
}
