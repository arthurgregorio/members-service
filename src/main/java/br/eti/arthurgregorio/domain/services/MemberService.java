package br.eti.arthurgregorio.domain.services;

import br.eti.arthurgregorio.domain.model.Country;
import br.eti.arthurgregorio.domain.model.Member;
import br.eti.arthurgregorio.infrastructure.repositories.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    private final CountryService countryService;

    @Transactional
    public Member create(Member member) {
        final var savedCountry = saveMemberCountry(member.getCountry());
        member.setCountry(savedCountry);
        return memberRepository.save(member);
    }

    @Transactional
    public Member update(Member member) {
        final var savedCountry = saveMemberCountry(member.getCountry());
        member.setCountry(savedCountry);
        return memberRepository.save(member);
    }

    @Transactional
    public void delete(Member member) {
        memberRepository.delete(member);
    }

    private Country saveMemberCountry(Country country) {
        if (!country.isSaved()) {
            return countryService.fetchAndSave(country.getName());
        } else {
            return country;
        }
    }
}
