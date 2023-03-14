package study.datajpa.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class MemberTest {

	@PersistenceContext
	EntityManager em;

	@Test
	public void testEntity() {
		List<Team> teams = new ArrayList<>();
		for (int i = 0; i < 2; i++) {
			Team team = new Team("team" + (i+1));
			em.persist(team);
			teams.add(team);
		}

		for (int i = 0; i < 5; i++) {
			Member member = new Member("member"+(i+1), 10 * (i+1), teams.get(i%2));
			em.persist(member);
		}

		em.flush();
		em.clear();

		List<Member> members = em.createQuery("select m from Member m", Member.class)
			.getResultList();

		for (Member member : members) {
			System.out.println("member = " + member);
			System.out.println("=> member.team = " + member.getTeam());
		}
	}

}