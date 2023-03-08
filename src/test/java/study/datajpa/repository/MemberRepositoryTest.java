package study.datajpa.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

@SpringBootTest
@Transactional
class MemberRepositoryTest {

	@Autowired
	MemberRepository memberRepository;
	@Autowired
	TeamRepository teamRepository;

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

	@Test
	public void basicCRUD() {
		Member member1 = new Member("member1");
		Member member2 = new Member("member2");
		memberRepository.save(member1);
		memberRepository.save(member2);

		// 단건 조회 검증
		Member foundMember1 = memberRepository.findById(member1.getId())
			.orElseThrow(EntityNotFoundException::new);
		Member foundMember2 = memberRepository.findById(member2.getId())
			.orElseThrow(EntityNotFoundException::new);
		assertThat(foundMember1).isEqualTo(member1);
		assertThat(foundMember2).isEqualTo(member2);

		// 리스트 조회 검증
		List<Member> all = memberRepository.findAll();
		assertThat(all.size()).isEqualTo(2);

		// 카운트 검증
		long count = memberRepository.count();
		assertThat(count).isEqualTo(2);

		// 삭제 검증
		memberRepository.delete(member1);
		memberRepository.delete(member2);

		count = memberRepository.count();
		assertThat(count).isEqualTo(0);
	}

	@Test
	public void findByUsernameAndAgeGraterThen() {
		Member member1 = new Member("AAA", 10);
		Member member2 = new Member("AAA", 20);
		memberRepository.save(member1);
		memberRepository.save(member2);

		List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

		for (Member member : result) {
			assertThat(member.getUsername()).isEqualTo("AAA");
			assertThat(member.getAge() > 15).isEqualTo(true);
		}
		assertThat(result.size()).isEqualTo(1);
	}

	@Test
	public void findTop3ByOrderByAgeDesc() {
		memberRepository.findTop3ByOrderByAgeDesc();
	}

	@Test
	public void testQuery() {
		Member member1 = new Member("AAA", 10);
		Member member2 = new Member("AAA", 20);
		memberRepository.save(member1);
		memberRepository.save(member2);

		List<Member> result = memberRepository.findUser("AAA", 10);

		for (Member member : result) {
			assertThat(member.getUsername()).isEqualTo("AAA");
			assertThat(member.getAge()).isEqualTo(10);
		}
		assertThat(result.size()).isEqualTo(1);
	}

	@Test
	public void findUsernameList() {
		Member member1 = new Member("AAA", 10);
		Member member2 = new Member("BBB", 20);
		memberRepository.save(member1);
		memberRepository.save(member2);

		List<String> usernameList = memberRepository.findUsernameList();
		for (String s : usernameList) {
			System.out.println("s = " + s);
		}
	}

	@Test
	public void findMemberDto() {
		Team team = new Team("teamA");
		teamRepository.save(team);

		Member member = new Member("AAA", 10);
		member.setTeam(team);
		memberRepository.save(member);

		List<MemberDto> memberDtoList = memberRepository.findMemberDto();
		for (MemberDto memberDto : memberDtoList) {
			System.out.println("dto = " + memberDto);
		}
	}

	@Test
	public void findByNames() {
		Member member1 = new Member("AAA", 10);
		Member member2 = new Member("BBB", 20);
		memberRepository.save(member1);
		memberRepository.save(member2);

		List<Member> members = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));
		for (Member member : members) {
			System.out.println("dto = " + member);
		}
	}
}