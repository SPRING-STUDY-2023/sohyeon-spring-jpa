package study.datajpa.controller;

import javax.annotation.PostConstruct;
import javax.persistence.EntityNotFoundException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import study.datajpa.entity.Member;
import study.datajpa.repository.MemberRepository;

@RestController
@RequiredArgsConstructor
public class MemberController {

	private final MemberRepository memberRepository;

	@GetMapping("/members/{id}")
	public String findMember(@PathVariable("id") Long id) {
		Member member = memberRepository.findById(id)
			.orElseThrow(EntityNotFoundException::new);
		return member.getUsername( );
	}

	@GetMapping("/members2/{id}")
	public String findMember2(@PathVariable("id") Member member) {
		return member.getUsername( );
	}

	@PostConstruct
	public void init() {
		memberRepository.save(new Member("memberA"));
	}
}
