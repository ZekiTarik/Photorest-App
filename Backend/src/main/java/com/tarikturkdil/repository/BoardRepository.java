package com.tarikturkdil.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tarikturkdil.entity.Board;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long>{

	List<Board> findByUserId(Long userId);
}
