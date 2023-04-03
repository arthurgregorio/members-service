package br.eti.arthurgregorio.fixture;

import br.eti.arthurgregorio.domain.model.Country;
import br.eti.arthurgregorio.domain.model.Currency;
import br.eti.arthurgregorio.domain.model.Member;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MemberFixture {

    public static Member createContractor() {

        final var member = create(Member.Type.CONTRACTOR);
        member.setContractDuration(1);

        return member;
    }

    public static Member createEmployee() {

        final var member = create(Member.Type.EMPLOYEE);
        member.setRole("Software Engineer");

        return member;
    }

    public static Member create(Member.Type type) {

        final var member = new Member();

        member.setName("The member");
        member.setType(type);
        member.setTags(List.of("Java", "Backend"));
        member.setSalary(BigDecimal.TEN);

        final var country = new Country("Brasil");
        country.setCurrency(new Currency("BRL", "R$"));

        member.setCountry(country);

        member.setId(1L);
        member.setExternalId(UUID.randomUUID());

        return member;
    }
}
