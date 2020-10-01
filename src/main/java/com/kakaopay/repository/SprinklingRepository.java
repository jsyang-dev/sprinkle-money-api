package com.kakaopay.repository;

import com.kakaopay.domain.Sprinkling;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SprinklingRepository extends JpaRepository<Sprinkling, Long> {

  @EntityGraph(attributePaths = "receivings")
  Optional<Sprinkling> findByToken(String token);
}
