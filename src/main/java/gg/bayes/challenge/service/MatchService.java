package gg.bayes.challenge.service;

import gg.bayes.challenge.rest.model.HeroItems;
import gg.bayes.challenge.rest.model.HeroKills;

import java.util.List;

public interface MatchService {
    Long ingestMatch(String payload);
    List<HeroItems> getItems(Long matchId, String heroName);
    List<HeroKills> getHeroKills(Long matchId);
}
