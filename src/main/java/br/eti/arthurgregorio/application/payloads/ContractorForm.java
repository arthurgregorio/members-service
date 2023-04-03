package br.eti.arthurgregorio.application.payloads;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ContractorForm extends MemberForm {

    @NotNull(message = "For contractors, contract duration is required")
    @Positive(message = "For contractors, contract duration must be more than one")
    private final Integer contractDuration;

    public ContractorForm(String name, BigDecimal salary, String country, Integer contractDuration, List<String> tags) {
        super(name, salary, country, tags);
        this.contractDuration = contractDuration;
    }
}
