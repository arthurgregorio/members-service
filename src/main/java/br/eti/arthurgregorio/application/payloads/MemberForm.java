package br.eti.arthurgregorio.application.payloads;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

@Getter
@ToString
@EqualsAndHashCode
public class MemberForm {

    @NotBlank(message = "Name can't be null or blank")
    private final String name;
    @NotNull(message = "Salary must not be null")
    @Positive(message = "Salary must be a positive value greather than zero")
    private final BigDecimal salary;
    @NotBlank(message = "Country can't be null or blank")
    private final String country;
    private final List<String> tags;

    public MemberForm(String name, BigDecimal salary, String country, List<String> tags) {
        this.name = name;
        this.salary = salary;
        this.country = country;
        this.tags = tags;
    }
}
