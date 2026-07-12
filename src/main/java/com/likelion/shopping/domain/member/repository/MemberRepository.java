package com.likelion.shopping.domain.member.repository;

import com.likelion.shopping.domain.member.entity.Member;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select m from Member m where m.id =:id")
    Optional<Member> findByIdWithLock(@Param("id") Long id);

    // 회원가입 시 이메일 중복 확인용
    boolean existsByEmail(String email);

    // 로그인 시 이메일로 사용자 검증 및 토큰 발급용
    Optional<Member> findByEmail(String email);
}
