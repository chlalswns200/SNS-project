package com.example.finalproject_choiminjun.repository;

import com.example.finalproject_choiminjun.domain.Alarm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlarmRepository extends JpaRepository<Alarm,Long> {
    Page<Alarm> findAllByTargetId(Long targetId, Pageable pageable);

}
