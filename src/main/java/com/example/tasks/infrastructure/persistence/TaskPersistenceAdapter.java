package com.example.tasks.infrastructure.persistence;

import com.example.tasks.application.TaskRepository;
import com.example.tasks.domain.Task;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

@Repository
class TaskPersistenceAdapter implements TaskRepository {

    private final TaskJpaRepository jpa;

    TaskPersistenceAdapter(TaskJpaRepository jpa) {
        this.jpa = jpa;
    }

    // shortcut: without @Version the last PUT wins, add optimistic locking if concurrent edits matter
    @Override
    public void save(Task task) {
        jpa.save(TaskEntity.from(task));
    }

    @Override
    public Optional<Task> findById(UUID id) {
        return jpa.findById(id).map(TaskEntity::toDomain);
    }

    @Override
    public List<Task> findPage(int page, int size) {
        return jpa.findAllByOrderByCreatedAtDescIdAsc(PageRequest.of(page, size)).stream()
                .map(TaskEntity::toDomain)
                .toList();
    }

    @Override
    public boolean deleteById(UUID id) {
        if (!jpa.existsById(id)) {
            return false;
        }
        jpa.deleteById(id);
        return true;
    }
}
