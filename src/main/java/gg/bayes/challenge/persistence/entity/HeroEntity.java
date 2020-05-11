package gg.bayes.challenge.persistence.entity;

import lombok.Data;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class HeroEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Integer id;
    @ElementCollection(targetClass= HeroDamageEntity.class)
    private List<HeroDamageEntity> heroDamageList = new ArrayList<>();
    @ElementCollection(targetClass= HeroItemsEntity.class)
    private List<HeroItemsEntity> heroItemsList;
    private Integer kills = 0;
    @ElementCollection(targetClass= HeroSpellsEntity.class)
    private List<HeroSpellsEntity> heroSpellsList = new ArrayList<>();
}
