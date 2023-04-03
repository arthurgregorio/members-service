package br.eti.arthurgregorio.infrastructure.repositories;

import br.eti.arthurgregorio.domain.model.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long>, JpaSpecificationExecutor<Member> {

    @EntityGraph(attributePaths = {"tags", "country"})
    Optional<Member> findByExternalId(UUID uuid);

    @Override
    @EntityGraph(attributePaths = {"tags", "country"})
    Page<Member> findAll(Specification<Member> specification, Pageable pageable);
}
