package gg.bayes.challenge.persistence.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
public class HeroDamageEntity {
    @Id
    private Integer id;
    private String target;
    private Integer damageInstances;
    private Integer totalDamage;

}
