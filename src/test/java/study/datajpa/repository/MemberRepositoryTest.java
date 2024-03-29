package study.datajpa.repository;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.data.domain.Sort.*;
import static org.springframework.data.domain.Sort.Direction.*;

import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

	@PersistenceContext
	EntityManager em;

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

	@Test
	public void paging() {
		// given
		for (int i = 0; i < 10; i++) {
			Member member = new Member("member" + (i + 1), 10);
			memberRepository.save(member);
		}

		PageRequest pageRequest = PageRequest.of(0, 3, by(DESC, "username"));

		// when
		Page<Member> page = memberRepository.findByAge(10, pageRequest);

		Page<MemberDto> toMap = page.map(
			member -> new MemberDto(member.getId(), member.getUsername(), member.getTeam().getName()));

		// then
		List<Member> content = page.getContent();
		long totalElements = page.getTotalElements();

		assertThat(content.size()).isEqualTo(3);
		assertThat(totalElements).isEqualTo(10);
		assertThat(page.getNumber()).isEqualTo(0);
		assertThat(page.getTotalPages()).isEqualTo(4);
		assertThat(page.isFirst()).isEqualTo(true);
		assertThat(page.hasNext()).isEqualTo(true);
	}

	@Test
	public void bulkUpTest() {
		// given
		for (int i = 0; i < 10; i++) {
			memberRepository.save(new Member("member" + (i+1), 10 * (i+1)));
		}

		// when
		int resultCount = memberRepository.bulkAgePlus(50);
		// em.flush();
		// em.clear();

		List<Member> result = memberRepository.findByUsername("member5");
		System.out.println("member5 = " + result.get(0));

		// then
		assertThat(resultCount).isEqualTo(6);
	}

	@Test
	public void findMemberLazy() {
		// given
		Team teamA = new Team("teamA");
		Team teamB = new Team("teamB");
		teamRepository.save(teamA);
		teamRepository.save(teamB);

		Member member1 = new Member("member1", 10, teamA);
		Member member2 = new Member("member2", 10, teamB);
		memberRepository.save(member1);
		memberRepository.save(member2);

		em.flush();
		em.clear();

		// when
		List<Member> members = memberRepository.findAll();

		for (Member member : members) {
			System.out.println("member = " + member.getUsername());
			System.out.println("member.teamClass = " + member.getTeam().getClass());
			System.out.println("member.team = " + member.getTeam().getName());
		}
	}

	@Test
	public void findMemberFetchJoin() {
		// given
		Team teamA = new Team("teamA");
		Team teamB = new Team("teamB");
		teamRepository.save(teamA);
		teamRepository.save(teamB);

		Member member1 = new Member("member1", 10, teamA);
		Member member2 = new Member("member2", 10, teamB);
		memberRepository.save(member1);
		memberRepository.save(member2);

		em.flush();
		em.clear();

		// when
		List<Member> members = memberRepository.findMemberFetchJoin();

		for (Member member : members) {
			System.out.println("member = " + member.getUsername());
			System.out.println("member.teamClass = " + member.getTeam().getClass());
			System.out.println("member.team = " + member.getTeam().getName());
		}
	}

	@Test
	public void findMemberEntityGraph() {
		// given
		Team teamA = new Team("teamA");
		Team teamB = new Team("teamB");
		teamRepository.save(teamA);
		teamRepository.save(teamB);

		Member member1 = new Member("member1", 10, teamA);
		Member member2 = new Member("member2", 10, teamB);
		memberRepository.save(member1);
		memberRepository.save(member2);

		em.flush();
		em.clear();

		// when
		List<Member> members = memberRepository.findAll();

		for (Member member : members) {
			System.out.println("member = " + member.getUsername());
			System.out.println("member.teamClass = " + member.getTeam().getClass());
			System.out.println("member.team = " + member.getTeam().getName());
		}
	}

	@Test
	public void queryHint() {
		// given
		Member member1 = new Member("member1", 10);
		memberRepository.save(member1);
		em.flush();
		em.clear();

		// when
		Member foundMember = memberRepository.findReadOnlyById(member1.getId());
		foundMember.setAge(12);

		em.flush();
	}

	@Test
	public void lock() {
		// given
		Member member1 = new Member("member1", 10);
		memberRepository.save(member1);
		em.flush();
		em.clear();

		// when
		List<Member> result = memberRepository.findLockByUsername(member1.getUsername());
	}

	@Test
	public void callCustom() {
		List<Member> result = memberRepository.findMemberCustom();
	}

	@Test
	public void queryByExample() {
		// given
		Team team = new Team("teamA");
		em.persist(team);

		Member m1 = new Member("m1", 0, team);
		Member m2 = new Member("m2", 0, team);
		em.persist(m1);
		em.persist(m2);

		em.flush();
		em.clear();

		// when
		// Probe 생성
		Member member = new Member("m1");
		member.setTeam(team);

		ExampleMatcher matcher = ExampleMatcher.matching()
			.withIgnorePaths("age");

		Example<Member> example = Example.of(member, matcher);

		List<Member> result = memberRepository.findAll(example);

		assertThat(result.get(0).getUsername()).isEqualTo("m1");
	}

	@Test
	public void projections() {
		// given
		Team team = new Team("teamA");
		em.persist(team);

		Member m1 = new Member("m1", 0, team);
		Member m2 = new Member("m2", 0, team);
		em.persist(m1);
		em.persist(m2);

		em.flush();
		em.clear();

		// when
		memberRepository.findProjectionsByUsername("m1")
			.forEach(member ->
				System.out.println("usernameOnly => " + member.getUsername() + " " + member.getTeam().getName()));
	}

	@Test
	public void NativeQuery() {
		// given
		Team team = new Team("teamA");
		em.persist(team);

		Member m1 = new Member("m1", 0, team);
		Member m2 = new Member("m2", 0, team);
		em.persist(m1);
		em.persist(m2);

		em.flush();
		em.clear();

		// when
		Member result = memberRepository.findByNativeQuery("m1");
		System.out.println("result = " + result);
	}

	@Test
	public void NativeProjection() {
		// given
		Team team = new Team("teamA");
		em.persist(team);

		Member m1 = new Member("m1", 0, team);
		Member m2 = new Member("m2", 0, team);
		em.persist(m1);
		em.persist(m2);

		em.flush();
		em.clear();

		// when
		Page<MemberProjection> result = memberRepository.findByNativeProjection(PageRequest.of(1, 10));
		result.getContent()
			.forEach(m -> {
				System.out.println("memberProjection = " + m.getId());
				System.out.println("memberProjection = " + m.getUsername());
				System.out.println("memberProjection = " + m.getTeamName());
				System.out.println();
			});
	}
}