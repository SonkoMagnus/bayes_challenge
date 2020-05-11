package gg.bayes.challenge.persistence;

import gg.bayes.challenge.persistence.entity.HeroEntity;
import org.springframework.data.repository.CrudRepository;

public interface HeroEntityRepository extends CrudRepository<HeroEntity, Integer> {
}
