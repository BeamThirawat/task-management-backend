package com.example.taskmanagement.repository;

import com.example.taskmanagement.entity.Tasks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TasksRepository extends JpaRepository<Tasks, Long> {

    List<Tasks> findByUserIdOrderByIdDesc(Long user_id);
    Optional<Tasks> findById(Long id);
    List<Tasks> findByStatus(Tasks.TaskStatus status);
    List<Tasks> findByStatusAndUserIdOrderByIdDesc(Tasks.TaskStatus status, Long userId);

    void  deleteByFolderId(Long id);
}
