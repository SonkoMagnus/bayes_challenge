package gg.bayes.challenge.persistence;

import gg.bayes.challenge.persistence.entity.MatchEntity;
import org.springframework.data.repository.CrudRepository;

public interface MatchEntityRepository extends CrudRepository<MatchEntity, Integer> {
}
