package br.eti.arthurgregorio.application.payloads;

import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class EmployeeForm extends MemberForm {

    @NotBlank(message = "Employee's must have a role")
    private final String role;

    public EmployeeForm(String name, BigDecimal salary, String country, String role, List<String> tags) {
        super(name, salary, country, tags);
        this.role = role;
    }
}
