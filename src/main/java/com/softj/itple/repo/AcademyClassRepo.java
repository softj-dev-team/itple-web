package com.softj.itple.repo;

import com.softj.itple.domain.Types;
import com.softj.itple.entity.AcademyClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Type;
import java.util.List;

@Repository
public interface AcademyClassRepo extends JpaRepository<AcademyClass, Long>, QuerydslPredicateExecutor<AcademyClass> {

    List<AcademyClass> findByAcademyType(Types.AcademyType academyType);

    @Query(value = "SELECT a.* from tb_class a WHERE a.is_deleted = :isDeleted AND CASE WHEN CAST(:academyType AS TEXT) IS NOT NULL THEN a.academy_type = COALESCE(CAST(:academyType AS TEXT), '') ELSE 1=1 END ORDER BY a.class_name collate \"ko_KR.utf8\" asc, a.id asc", nativeQuery = true)
    List<AcademyClass> getAllAcademyClassList(@Param("isDeleted") Boolean isDeleted, @Param("academyType") String academyType);
}
