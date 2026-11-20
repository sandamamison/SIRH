package SI.RH.repository;

import SI.RH.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MutationRepository extends JpaRepository<Mutation, Long> {
    List<Mutation> findByEmployeId(Long employeId);
}
