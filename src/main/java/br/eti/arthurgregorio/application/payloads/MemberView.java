package br.eti.arthurgregorio.application.payloads;

import br.eti.arthurgregorio.domain.model.Member;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record MemberView(
        UUID id,
        String name,
        BigDecimal salary,
        Member.Type type,
        Integer contractDuration,
        String role,
        CountryView country,
        List<String> tags
) {
}
