package br.eti.arthurgregorio.services;

import br.eti.arthurgregorio.fixture.MemberFixture;
import br.eti.arthurgregorio.BaseIntegrationTest;
import br.eti.arthurgregorio.domain.model.Country;
import br.eti.arthurgregorio.domain.model.Currency;
import br.eti.arthurgregorio.domain.model.Member;
import br.eti.arthurgregorio.domain.services.MemberService;
import br.eti.arthurgregorio.infrastructure.repositories.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class MemberServiceTest extends BaseIntegrationTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    public void clearDatabase() {
        memberRepository.deleteAll();
    }

    @ParameterizedTest
    @MethodSource("members")
    void create_shouldCreateMember_returnCreated(Member toCreate, Consumer<Pair<Member, Member>> specificAssertions) {

        final var created = memberService.create(toCreate);

        assertThat(created)
                .isNotNull()
                .hasFieldOrProperty("id").isNotNull()
                .hasFieldOrProperty("externalId").isNotNull()
                .hasFieldOrProperty("lastUpdate").isNotNull()
                .hasFieldOrPropertyWithValue("type", toCreate.getType())
                .hasFieldOrPropertyWithValue("name", toCreate.getName())
                .hasFieldOrPropertyWithValue("salary", toCreate.getSalary());

        specificAssertions.accept(Pair.of(created, toCreate));

        assertThat(created.getTags())
                .isNotEmpty()
                .containsExactlyInAnyOrderElementsOf(toCreate.getTags());

        final var country = created.getCountry();

        assertThat(country)
                .hasFieldOrPropertyWithValue("name", country.getName())
                .hasFieldOrPropertyWithValue("currency", country.getCurrency());
    }

    @ParameterizedTest
    @MethodSource("members")
    void update_shouldUpdateMember_searchAndCheckUpdated(Member toCreate, Consumer<Pair<Member, Member>> specificAssertions) {

        final var created = memberService.create(toCreate);
        final var toUpdate = createUpdatedMember(created.getType());

        created.updateValues(toUpdate);

        final var updated = memberService.update(created);

        assertThat(updated)
                .hasFieldOrProperty("lastUpdate").isNotEqualTo(created.getLastUpdate())
                .hasFieldOrPropertyWithValue("id", created.getId())
                .hasFieldOrPropertyWithValue("externalId", created.getExternalId())
                .hasFieldOrPropertyWithValue("type", toUpdate.getType())
                .hasFieldOrPropertyWithValue("name", toUpdate.getName())
                .hasFieldOrPropertyWithValue("salary", toUpdate.getSalary());

        specificAssertions.accept(Pair.of(updated, toUpdate));

        assertThat(updated.getTags())
                .isNotEmpty()
                .containsExactlyInAnyOrderElementsOf(toUpdate.getTags());

        final var country = updated.getCountry();

        assertThat(country)
                .hasFieldOrPropertyWithValue("name", country.getName())
                .hasFieldOrPropertyWithValue("currency", country.getCurrency());
    }

    @ParameterizedTest
    @MethodSource("members")
    void delete_shouldDeleteMember_searchAndFindNothing(Member toCreate, Consumer<Pair<Member, Member>> specificAssertions) {

        final var created = memberService.create(toCreate);
        assertThat(created).isNotNull();

        memberService.delete(created);

        final var found = memberRepository.findByExternalId(created.getExternalId());
        assertThat(found).isNotPresent();
    }

    private Member createUpdatedMember(Member.Type type) {

        final var updated = new Member();

        updated.setName("Updated");
        updated.setType(type);
        updated.setTags(List.of("Kotlin", "Android"));
        updated.setSalary(BigDecimal.ONE);

        final var country = new Country("Argentina");
        country.setCurrency(new Currency("ARS", "$"));

        updated.setCountry(country);

        if (type == Member.Type.CONTRACTOR) {
            updated.setContractDuration(2);
        } else {
            updated.setRole("Tech Lead");
        }

        return updated;
    }

    private static Stream<Arguments> members() {

        final Consumer<Pair<Member, Member>> contractorAssertions = pair ->
                assertThat(pair.getFirst())
                        .hasFieldOrPropertyWithValue("contractDuration", pair.getSecond().getContractDuration())
                        .hasFieldOrPropertyWithValue("role", null);

        final Consumer<Pair<Member, Member>> employeeAssertions = pair ->
                assertThat(pair.getFirst())
                        .hasFieldOrPropertyWithValue("role", pair.getSecond().getRole())
                        .hasFieldOrPropertyWithValue("contractDuration", null);

        return Stream.of(
                Arguments.of(MemberFixture.createContractor(), contractorAssertions),
                Arguments.of(MemberFixture.createEmployee(), employeeAssertions)
        );
    }
}
