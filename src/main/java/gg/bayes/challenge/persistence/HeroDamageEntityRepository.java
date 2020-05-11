package gg.bayes.challenge.persistence;

import gg.bayes.challenge.persistence.entity.HeroDamageEntity;
import org.springframework.data.repository.CrudRepository;

public interface HeroDamageEntityRepository extends CrudRepository<HeroDamageEntity, Integer> {
}
