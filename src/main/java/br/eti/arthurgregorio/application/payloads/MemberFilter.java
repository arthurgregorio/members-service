package br.eti.arthurgregorio.application.payloads;

import br.eti.arthurgregorio.domain.model.Member;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.util.StringUtils.hasText;

public record MemberFilter(
        String name,
        Member.Type type,
        List<String> tags
) {
    public Specification<Member> toSpecification() {
        return (root, query, builder) -> {

            final List<Predicate> predicates = new ArrayList<>();

            if (hasText(name)) {
                predicates.add(builder.like(builder.lower(root.get("name")), name.toLowerCase() + "%"));
            }

            if (type != null) {
                predicates.add(builder.equal(root.get("type"), type));
            }

            if (tags != null && !tags.isEmpty()) {
                predicates.add(root.get("tags").in(tags));
            }

            return builder.and(predicates.toArray(new Predicate[]{}));
        };
    }
}
