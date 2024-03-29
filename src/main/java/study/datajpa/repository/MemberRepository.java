package study.datajpa.repository;

import static javax.persistence.LockModeType.*;

import java.util.Collection;
import java.util.List;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

	List<Member> findByUsername(String username);
	List<Member> findByUsernameAndAgeGreaterThan(String username, int age);
	List<Member> findTop3ByOrderByAgeDesc();

	@Query("select m from Member m where m.username = :username and m.age = :age")
	List<Member> findUser(@Param("username") String username, @Param("age") int age);

	@Query("select m.username from Member m")
	List<String> findUsernameList();

	@Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
	List<MemberDto> findMemberDto();

	@Query("select m from Member m where m.username in :names")
	List<Member> findByNames(@Param("names") Collection<String> names);

	Page<Member> findByAge(int age, Pageable pageable);

	@Query(value = "select m from Member m left join m.team t",
		countQuery = "select count(m) from Member m")
	Page<Member> findByAge_join(int age, Pageable pageable);

	@Modifying(clearAutomatically = true)
	@Query("update Member m set m.age = m.age + 1 where m.age >= :age")
	int bulkAgePlus(@Param("age") int age);

	@Query("select m from Member m left join fetch m.team")
	List<Member> findMemberFetchJoin();

	@Override
	@EntityGraph(attributePaths = {"team"})
	List<Member> findAll();

	@QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
	Member findReadOnlyById(Long id);

	@Lock(PESSIMISTIC_WRITE)
	List<Member> findLockByUsername(String username);

	List<NestedClosedProjections> findProjectionsByUsername(@Param("username") String username);

	@Query(value = "select * from Member where username = ?", nativeQuery = true)
	Member findByNativeQuery(String username);

	@Query(value = "select m.member_id as id, m.username, t.name as teamName "
		+ "from member m left join team t",
		countQuery = "select count(*) from member",
		nativeQuery = true)
	 Page<MemberProjection> findByNativeProjection(Pageable pageable);
}
