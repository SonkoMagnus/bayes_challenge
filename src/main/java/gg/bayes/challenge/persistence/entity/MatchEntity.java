package gg.bayes.challenge.persistence.entity;

import lombok.Data;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.HashMap;
import java.util.Map;

@Data
@Entity
public class MatchEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Integer id;
    @ElementCollection(targetClass= HeroEntity.class)
    private Map<String, HeroEntity> heroEntityMap = new HashMap<>();
}
