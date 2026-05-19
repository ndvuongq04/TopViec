package com.topviec.topviec_be.repository;

import com.topviec.topviec_be.entity.CvTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CvTemplateRepository extends JpaRepository<CvTemplate, Long> {

    @Query("""
            SELECT ct FROM CvTemplate ct
            WHERE ct.deletedAt IS NULL
            AND (:keyword IS NULL OR LOWER(ct.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(ct.slug) LIKE LOWER(CONCAT('%', :keyword, '%')))
            ORDER BY ct.isDefault DESC, ct.createdAt DESC
            """)
    Page<CvTemplate> searchAll(@Param("keyword") String keyword, Pageable pageable);

    @Query("""
            SELECT ct FROM CvTemplate ct
            WHERE ct.deletedAt IS NULL
            AND ct.isActive = true
            ORDER BY ct.isDefault DESC, ct.createdAt DESC
            """)
    List<CvTemplate> findAllActive();

    @Query("""
            SELECT ct FROM CvTemplate ct
            WHERE ct.id = :id
            AND ct.deletedAt IS NULL
            """)
    Optional<CvTemplate> findActiveOrInactiveById(@Param("id") Long id);

    @Query("""
            SELECT ct FROM CvTemplate ct
            WHERE ct.id = :id
            AND ct.isActive = true
            AND ct.deletedAt IS NULL
            """)
    Optional<CvTemplate> findActiveById(@Param("id") Long id);

    @Query("""
            SELECT COUNT(ct) > 0 FROM CvTemplate ct
            WHERE LOWER(ct.slug) = LOWER(:slug)
            AND ct.deletedAt IS NULL
            """)
    boolean existsBySlug(@Param("slug") String slug);

    @Query("""
            SELECT COUNT(ct) > 0 FROM CvTemplate ct
            WHERE LOWER(ct.slug) = LOWER(:slug)
            AND ct.id <> :id
            AND ct.deletedAt IS NULL
            """)
    boolean existsBySlugAndIdNot(@Param("slug") String slug, @Param("id") Long id);

    @Query("""
            SELECT ct FROM CvTemplate ct
            WHERE ct.isDefault = true
            AND ct.deletedAt IS NULL
            """)
    Optional<CvTemplate> findDefaultTemplate();
}
