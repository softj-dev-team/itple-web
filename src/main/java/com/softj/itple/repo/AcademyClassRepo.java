package com.softj.itple.repo;

import com.softj.itple.domain.SearchVO;
import com.softj.itple.domain.Types;
import com.softj.itple.entity.AcademyClass;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Type;
import java.util.List;

@Repository
public interface AcademyClassRepo extends JpaRepository<AcademyClass, Long>, QuerydslPredicateExecutor<AcademyClass> {

    List<AcademyClass> findByAcademyType(Types.AcademyType academyType);
    @Query(value = "SELECT a.* from tb_class a WHERE CASE WHEN CAST(:className AS TEXT) IS NULL THEN (1=1) ELSE (a.class_name LIKE '%'||CAST(:className AS TEXT)||'%') END AND a.is_deleted=false AND a.academy_type = :academyType ORDER BY a.class_name collate \"ko_KR.utf8\" asc, a.id asc", nativeQuery = true)
    List<AcademyClass> getAcademyClassList(@Param("academyType") String academyType, @Param("className") String className, Pageable pageable);

    @Query(value = "SELECT count(a.id) from tb_class a WHERE CASE WHEN CAST(:className AS TEXT) IS NULL THEN (1=1) ELSE (a.class_name LIKE '%'||CAST(:className AS TEXT)||'%') END AND a.is_deleted=false AND a.academy_type = :academyType", nativeQuery = true)
    int getAcademyClassListTotal(@Param("academyType") String academyType, @Param("className") String className);

    @Query(value = "SELECT a.* from tb_class a WHERE a.is_deleted = :isDeleted ORDER BY a.class_name collate \"ko_KR.utf8\" asc, a.id asc", nativeQuery = true)
    List<AcademyClass> getAllAcademyClassList(@Param("isDeleted") Boolean isDeleted);
}
