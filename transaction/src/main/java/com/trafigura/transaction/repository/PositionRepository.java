package com.trafigura.transaction.repository;

import com.trafigura.transaction.entity.Position;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author ：wpm
 */
public interface PositionRepository extends JpaRepository<Position, String> {
}
