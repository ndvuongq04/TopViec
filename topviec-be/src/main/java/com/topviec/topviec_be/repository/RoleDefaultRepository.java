package com.topviec.topviec_be.repository;

import com.topviec.topviec_be.entity.RoleDefault;
import com.topviec.topviec_be.enums.companyMember.MemberRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleDefaultRepository extends JpaRepository<RoleDefault, Long> {
    Optional<RoleDefault> findByRoleName(MemberRole roleName);
    boolean existsByRoleName(MemberRole roleName);
}
