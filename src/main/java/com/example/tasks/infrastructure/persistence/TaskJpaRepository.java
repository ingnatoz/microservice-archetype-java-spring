package com.example.tasks.infrastructure.persistence;

import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

interface TaskJpaRepository extends JpaRepository<TaskEntity, UUID> {

    List<TaskEntity> findAllByOrderByCreatedAtDescIdAsc(Pageable pageable);
}
