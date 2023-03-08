package study.datajpa.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import study.datajpa.entity.Member;

@SpringBootTest
@Transactional
class MemberJpaRepositoryTest {

	@Autowired
	MemberJpaRepository memberJpaRepository;

	@Test
	public void testMember() {
		Member member = new Member("memberA");
		Member savedMember = memberJpaRepository.save(member);

		Member foundMember = memberJpaRepository.find(savedMember.getId());

		assertThat(foundMember.getId()).isEqualTo(member.getId());
		assertThat(foundMember.getUsername()).isEqualTo(member.getUsername());
		assertThat(foundMember).isEqualTo(member);
	}

	@Test
	public void basicCRUD() {
		Member member1 = new Member("member1");
		Member member2 = new Member("member2");
		memberJpaRepository.save(member1);
		memberJpaRepository.save(member2);

		// 단건 조회 검증
		Member foundMember1 = memberJpaRepository.findById(member1.getId())
			.orElseThrow(EntityNotFoundException::new);
		Member foundMember2 = memberJpaRepository.findById(member2.getId())
				.orElseThrow(EntityNotFoundException::new);
		assertThat(foundMember1).isEqualTo(member1);
		assertThat(foundMember2).isEqualTo(member2);

		// 리스트 조회 검증
		List<Member> all = memberJpaRepository.findAll();
		assertThat(all.size()).isEqualTo(2);

		// 카운트 검증
		long count = memberJpaRepository.count();
		assertThat(count).isEqualTo(2);

		// 삭제 검증
		memberJpaRepository.delete(member1);
		memberJpaRepository.delete(member2);

		count = memberJpaRepository.count();
		assertThat(count).isEqualTo(0);
	}

	@Test
	public void findByUsernameAndAgeGraterThen() {
		Member member1 = new Member("AAA", 10);
		Member member2 = new Member("AAA", 20);
		memberJpaRepository.save(member1);
		memberJpaRepository.save(member2);

		List<Member> result = memberJpaRepository.findByUsernameAndAgeGraterThen("AAA", 15);

		for (Member member : result) {
			assertThat(member.getUsername()).isEqualTo("AAA");
			assertThat(member.getAge() > 15).isEqualTo(true);
		}
		assertThat(result.size()).isEqualTo(1);
	}

}