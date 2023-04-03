package br.eti.arthurgregorio.mappers;

import br.eti.arthurgregorio.application.payloads.ContractorForm;
import br.eti.arthurgregorio.fixture.MemberFixture;
import br.eti.arthurgregorio.application.mappers.MemberMapper;
import br.eti.arthurgregorio.application.mappers.MemberMapperImpl;
import br.eti.arthurgregorio.application.payloads.EmployeeForm;
import br.eti.arthurgregorio.domain.model.Member;
import br.eti.arthurgregorio.fixture.CountryFixture;
import br.eti.arthurgregorio.infrastructure.repositories.CountryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberMapperTest {

    @Mock
    private CountryRepository countryRepository;

    @InjectMocks
    private final MemberMapper memberMapper = new MemberMapperImpl();

    @Test
    void map_contractorFormToMember() {

        final var country = CountryFixture.create();
        final var contractorForm = new ContractorForm("The contractor", BigDecimal.TEN, "Brasil",
                3, List.of("Java", "Backend"));

        when(countryRepository.findByNameIgnoreCase(anyString())).thenReturn(Optional.of(country));

        final var contractor = memberMapper.map(contractorForm);

        assertThat(contractor)
                .isNotNull()
                .hasFieldOrPropertyWithValue("name", contractorForm.getName())
                .hasFieldOrPropertyWithValue("salary", contractorForm.getSalary())
                .hasFieldOrPropertyWithValue("type", Member.Type.CONTRACTOR)
                .hasFieldOrPropertyWithValue("contractDuration", contractorForm.getContractDuration())
                .hasFieldOrPropertyWithValue("role", null);

        assertThat(contractor.getCountry())
                .isNotNull()
                .hasFieldOrPropertyWithValue("name", "Brasil")
                .hasFieldOrPropertyWithValue("currency", country.getCurrency());

        assertThat(contractor.getTags())
                .isNotEmpty()
                .containsExactlyInAnyOrderElementsOf(contractorForm.getTags());
    }

    @Test
    void map_employeeFormToMember() {

        final var country = CountryFixture.create();
        final var employeeForm = new EmployeeForm("The contractor", BigDecimal.TEN, "Brasil",
                "Software Engineer", List.of("Java", "Backend"));

        when(countryRepository.findByNameIgnoreCase(anyString())).thenReturn(Optional.of(country));

        final var employee = memberMapper.map(employeeForm);

        assertThat(employee)
                .isNotNull()
                .hasFieldOrPropertyWithValue("name", employeeForm.getName())
                .hasFieldOrPropertyWithValue("salary", employeeForm.getSalary())
                .hasFieldOrPropertyWithValue("type", Member.Type.EMPLOYEE)
                .hasFieldOrPropertyWithValue("role", employeeForm.getRole())
                .hasFieldOrPropertyWithValue("contractDuration", null);

        assertThat(employee.getCountry())
                .isNotNull()
                .hasFieldOrPropertyWithValue("name", "Brasil")
                .hasFieldOrPropertyWithValue("currency", country.getCurrency());

        assertThat(employee.getTags())
                .isNotEmpty()
                .containsExactlyInAnyOrderElementsOf(employeeForm.getTags());
    }

    @ParameterizedTest
    @MethodSource("members")
    void map_memberToMemberView(Member member) {

        final var memberView = memberMapper.map(member);

        assertThat(memberView)
                .isNotNull()
                .hasFieldOrPropertyWithValue("name", member.getName())
                .hasFieldOrPropertyWithValue("salary", member.getSalary())
                .hasFieldOrPropertyWithValue("type", member.getType());

        assertThat(memberView.tags())
                .isNotEmpty()
                .containsExactlyInAnyOrderElementsOf(member.getTags());

        assertThat(memberView.country())
                .isNotNull()
                .hasFieldOrPropertyWithValue("name", "Brasil")
                .hasFieldOrPropertyWithValue("currency", "BRL");

        if (member.getType() == Member.Type.CONTRACTOR) {
            assertThat(memberView).hasFieldOrPropertyWithValue("contractDuration", member.getContractDuration());
        } else {
            assertThat(memberView).hasFieldOrPropertyWithValue("role", member.getRole());
        }
    }

    private static Stream<Arguments> members() {
        return Stream.of(
                Arguments.of(MemberFixture.createContractor()),
                Arguments.of(MemberFixture.createEmployee())
        );
    }
}
