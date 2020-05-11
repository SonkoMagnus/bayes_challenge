package gg.bayes.challenge.persistence;

import gg.bayes.challenge.persistence.entity.HeroItemsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HeroItemsEntityRepository extends JpaRepository<HeroItemsEntity, Integer> {
}
