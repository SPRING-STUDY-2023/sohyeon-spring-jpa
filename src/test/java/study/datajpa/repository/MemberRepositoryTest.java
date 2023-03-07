package study.datajpa.repository;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import study.datajpa.entity.Member;

@SpringBootTest
@Transactional
class MemberRepositoryTest {

	@Autowired
	MemberRepository memberRepository;

	@Test
	public void testMember() {
		Member member = new Member("memberA");
		Member savedMember = memberRepository.save(member);

		Member foundMember = memberRepository.findById(savedMember.getId())
			.orElseThrow(EntityNotFoundException::new);

		assertThat(foundMember.getId()).isEqualTo(member.getId());
		assertThat(foundMember.getUsername()).isEqualTo(member.getUsername());
		assertThat(foundMember).isEqualTo(member);
	}

}