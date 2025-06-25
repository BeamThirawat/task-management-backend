package com.example.taskmanagement.repository;

import com.example.taskmanagement.entity.Folders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FoldersRepository extends JpaRepository<Folders, Long> {

    Optional<Folders> findById(Long id);
    List<Folders> findByUserIdOrderByIdDesc(Long user_id);
}
