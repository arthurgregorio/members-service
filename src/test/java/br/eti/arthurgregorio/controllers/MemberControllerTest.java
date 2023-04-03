package br.eti.arthurgregorio.controllers;

import br.eti.arthurgregorio.application.controllers.MemberController;
import br.eti.arthurgregorio.application.mappers.MemberMapperImpl;
import br.eti.arthurgregorio.domain.services.MemberService;
import br.eti.arthurgregorio.fixture.MemberFixture;
import br.eti.arthurgregorio.infrastructure.repositories.CountryRepository;
import br.eti.arthurgregorio.infrastructure.repositories.MemberRepository;
import br.eti.arthurgregorio.domain.model.Member;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.google.common.base.Charsets;
import org.testcontainers.shaded.com.google.common.io.Resources;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@Import(MemberMapperImpl.class)
@WebMvcTest(MemberController.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Value("classpath:/payloads/contractor.json")
    private Resource contractorPayload;
    @Value("classpath:/payloads/employee.json")
    private Resource employeePayload;
    @Value("classpath:/payloads/contractor-nok.json")
    private Resource contractorNokPayload;
    @Value("classpath:/payloads/employee-nok.json")
    private Resource employeeNokPayload;

    @MockBean
    private MemberRepository memberRepository;
    @MockBean
    private CountryRepository countryRepository;

    @MockBean
    private MemberService memberService;

    private static final String BASE_URL = "/api/members";

    @Test
    void post_createContractor_expectCreated() throws Exception {

        final var expectedContractor = MemberFixture.createContractor();

        when(memberService.create(any(Member.class))).thenReturn(expectedContractor);

        final var payload = resourceAsString(contractorPayload);

        final var jsonResponse = this.mockMvc.perform(
                        post(BASE_URL + "/contractors")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(payload))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThatJson(jsonResponse)
                .isObject()
                .containsKey("id").isNotNull()
                .containsEntry("name", "The member")
                .containsEntry("salary", 10)
                .containsEntry("type", "CONTRACTOR")
                .containsEntry("contractDuration", 1);

        assertThatJson(jsonResponse)
                .node("country")
                .isObject()
                .containsEntry("name", "Brasil")
                .containsEntry("currency", "BRL");

        assertThatJson(jsonResponse)
                .node("tags")
                .isArray()
                .containsExactlyInAnyOrder("Java", "Backend");
    }

    @Test
    void post_createEmployee_expectCreated() throws Exception {

        final var expectedEmployee = MemberFixture.createEmployee();

        when(memberService.create(any(Member.class))).thenReturn(expectedEmployee);

        final var payload = resourceAsString(employeePayload);

        final var jsonResponse = this.mockMvc.perform(
                        post(BASE_URL + "/employees")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(payload))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThatJson(jsonResponse)
                .isObject()
                .containsKey("id").isNotNull()
                .containsEntry("name", "The member")
                .containsEntry("salary", 10)
                .containsEntry("type", "EMPLOYEE")
                .containsEntry("role", "Software Engineer");

        assertThatJson(jsonResponse)
                .node("country")
                .isObject()
                .containsEntry("name", "Brasil")
                .containsEntry("currency", "BRL");

        assertThatJson(jsonResponse)
                .node("tags")
                .isArray()
                .containsExactlyInAnyOrder("Java", "Backend");
    }

    @ParameterizedTest
    @MethodSource("invalidMembers")
    void post_RequiredFieldsNotPresent_expectUnprocessableEntity(
            String urlPath, String payload, Map<String, String> requiredEntries) throws Exception {

        final var jsonResponse = this.mockMvc.perform(
                        post(BASE_URL + urlPath)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(payload))
                .andExpect(status().isUnprocessableEntity())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThatJson(jsonResponse)
                .isObject()
                .node("errors")
                .isObject()
                .hasSize(requiredEntries.size())
                .containsExactlyInAnyOrderEntriesOf(requiredEntries);
    }

    @Test
    void put_updateEmployee_expectOk() throws Exception {

        final var expectedEmployee = MemberFixture.createEmployee();
        final var externalId = expectedEmployee.getExternalId();
        final var country = expectedEmployee.getCountry();

        when(memberService.update(any(Member.class))).thenReturn(expectedEmployee);
        when(memberRepository.findByExternalId(externalId)).thenReturn(Optional.of(expectedEmployee));
        when(countryRepository.findByNameIgnoreCase(country.getName())).thenReturn(Optional.of(country));

        final var payload = resourceAsString(employeePayload);

        final var jsonResponse = this.mockMvc.perform(
                        put(BASE_URL + "/employees/" + externalId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(payload))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThatJson(jsonResponse)
                .isObject()
                .containsEntry("id", expectedEmployee.getExternalId().toString());
    }

    @Test
    void put_updateContractor_expectOk() throws Exception {

        final var expectedContractor = MemberFixture.createContractor();
        final var externalId = expectedContractor.getExternalId();
        final var country = expectedContractor.getCountry();

        when(memberService.update(any(Member.class))).thenReturn(expectedContractor);
        when(memberRepository.findByExternalId(externalId)).thenReturn(Optional.of(expectedContractor));
        when(countryRepository.findByNameIgnoreCase(country.getName())).thenReturn(Optional.of(country));

        final var payload = resourceAsString(contractorPayload);

        final var jsonResponse = this.mockMvc.perform(
                        put(BASE_URL + "/contractors/" + externalId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(payload))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThatJson(jsonResponse)
                .isObject()
                .containsEntry("id", expectedContractor.getExternalId().toString());
    }

    @Test
    void delete_member_expectOk() throws Exception {

        final var externalId = UUID.randomUUID();
        final var expectedMember = MemberFixture.createEmployee();

        expectedMember.setExternalId(externalId);

        when(memberRepository.findByExternalId(externalId)).thenReturn(Optional.of(expectedMember));
        doNothing().when(memberService).delete(any(Member.class));

        this.mockMvc.perform(delete(BASE_URL + "/" + externalId))
                .andExpect(status().isNoContent());

        final var memberCaptor = ArgumentCaptor.forClass(Member.class);

        verify(memberService, times(1)).delete(memberCaptor.capture());

        assertThat(memberCaptor.getValue())
                .hasFieldOrPropertyWithValue("externalId", externalId);
    }

    @Test
    void get_expectListOfMembers() throws Exception {

        final var members = List.of(MemberFixture.createEmployee(), MemberFixture.createContractor());
        final var pageResponse = new PageImpl<>(members);

        when(memberRepository.findAll(isA(Specification.class), isA(Pageable.class))).thenReturn(pageResponse);

        final var jsonResponse = this.mockMvc.perform(
                        get(BASE_URL)
                                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThatJson(jsonResponse)
                .isObject()
                .node("content")
                .isArray()
                .isNotEmpty()
                .hasSize(members.size());
    }

    private String resourceAsString(Resource resource) {
        try {
            return Resources.toString(resource.getURL(), Charsets.UTF_8);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to load resource", ex);
        }
    }

    private Stream<Arguments> invalidMembers() {

        final Map<String, String> contractorEntries = Map.of(
                "name", "Name can't be null or blank",
                "salary", "Salary must be a positive value greather than zero",
                "country", "Country can't be null or blank",
                "contractDuration", "For contractors, contract duration must be more than one"
        );

        final Map<String, String> employeeEntries = Map.of(
                "name", "Name can't be null or blank",
                "salary", "Salary must be a positive value greather than zero",
                "country", "Country can't be null or blank",
                "role", "Employee's must have a role"
        );

        return Stream.of(
                Arguments.of("/contractors", resourceAsString(contractorNokPayload), contractorEntries),
                Arguments.of("/employees", resourceAsString(employeeNokPayload), employeeEntries)
        );
    }
}
