package com.rojas.dev.XCampo.repository;

import com.rojas.dev.XCampo.entity.Category;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Transactional
    @Query("SELECT c FROM Category c WHERE LOWER(TRIM(c.name)) LIKE LOWER(CONCAT('%', TRIM(:name), '%'))")
    List<Category> findByNameCategory(@Param("name") String name);


}
