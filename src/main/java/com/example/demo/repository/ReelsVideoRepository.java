package com.example.demo.repository;

import com.example.demo.model.ReelsVideo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReelsVideoRepository extends JpaRepository<ReelsVideo, Long> {
}
