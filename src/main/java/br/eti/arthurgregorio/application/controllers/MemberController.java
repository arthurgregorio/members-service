package br.eti.arthurgregorio.application.controllers;

import br.eti.arthurgregorio.application.payloads.ContractorForm;
import br.eti.arthurgregorio.application.mappers.MemberMapper;
import br.eti.arthurgregorio.application.payloads.EmployeeForm;
import br.eti.arthurgregorio.application.payloads.MemberFilter;
import br.eti.arthurgregorio.application.payloads.MemberView;
import br.eti.arthurgregorio.domain.exceptions.MemberNotFoundException;
import br.eti.arthurgregorio.domain.services.MemberService;
import br.eti.arthurgregorio.infrastructure.repositories.MemberRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {

    private final MemberMapper memberMapper;

    private final MemberRepository memberRepository;

    private final MemberService memberService;

    @GetMapping
    public ResponseEntity<Page<MemberView>> get(MemberFilter filter, Pageable pageable) {
        final var response = memberRepository.findAll(filter.toSpecification(), pageable)
                .map(memberMapper::map);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{externalId}")
    public ResponseEntity<MemberView> getById(@PathVariable UUID externalId) {
        return this.memberRepository.findByExternalId(externalId)
                .map(memberMapper::map)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new MemberNotFoundException(externalId));
    }

    @PostMapping("/employees")
    public ResponseEntity<MemberView> createEmployee(@RequestBody @Valid EmployeeForm form) {

        final var member = memberMapper.map(form);
        final var saved = memberService.create(member);

        final var location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(saved.getExternalId())
                .toUri();

        return ResponseEntity.created(location).body(memberMapper.map(saved));
    }

    @PostMapping("/contractors")
    public ResponseEntity<MemberView> createContractor(@RequestBody @Valid ContractorForm form) {

        final var member = memberMapper.map(form);
        final var saved = memberService.create(member);

        final var location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(saved.getExternalId())
                .toUri();

        return ResponseEntity.created(location).body(memberMapper.map(saved));
    }

    @PutMapping("/employees/{externalId}")
    public ResponseEntity<MemberView> update(@PathVariable UUID externalId, @RequestBody @Valid EmployeeForm form) {

        final var member = memberRepository.findByExternalId(externalId)
                .orElseThrow(() -> new MemberNotFoundException(externalId));

        member.updateValues(memberMapper.map(form));
        final var updated = memberService.update(member);

        return ResponseEntity.ok(memberMapper.map(updated));
    }

    @PutMapping("/contractors/{externalId}")
    public ResponseEntity<MemberView> update(@PathVariable UUID externalId, @RequestBody @Valid ContractorForm form) {

        final var member = memberRepository.findByExternalId(externalId)
                .orElseThrow(() -> new MemberNotFoundException(externalId));

        member.updateValues(memberMapper.map(form));
        final var updated = memberService.update(member);

        return ResponseEntity.ok(memberMapper.map(updated));
    }

    @DeleteMapping("/{externalId}")
    public ResponseEntity<Void> delete(@PathVariable UUID externalId) {

        final var member = memberRepository.findByExternalId(externalId)
                .orElseThrow(() -> new MemberNotFoundException(externalId));

        memberService.delete(member);

        return ResponseEntity.noContent().build();
    }
}
