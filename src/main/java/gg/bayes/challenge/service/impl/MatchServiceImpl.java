package gg.bayes.challenge.service.impl;

import gg.bayes.challenge.persistence.HeroDamageEntityRepository;
import gg.bayes.challenge.persistence.HeroEntityRepository;
import gg.bayes.challenge.persistence.HeroItemsEntityRepository;
import gg.bayes.challenge.persistence.MatchEntityRepository;
import gg.bayes.challenge.persistence.entity.HeroEntity;
import gg.bayes.challenge.persistence.entity.HeroItemsEntity;
import gg.bayes.challenge.persistence.entity.MatchEntity;
import gg.bayes.challenge.rest.model.HeroItems;
import gg.bayes.challenge.rest.model.HeroKills;
import gg.bayes.challenge.service.MatchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MatchServiceImpl implements MatchService {


    @Autowired
    MatchEntityRepository matchRepository;

    @Autowired
    public HeroDamageEntityRepository heroDamageRepository;

    @Autowired
    public HeroEntityRepository heroEntityRepository;

    @Autowired
    public HeroItemsEntityRepository heroItemsRepository;

    @Autowired
    public MatchServiceImpl() {
    }

    public static final String npcPrefix = "npc_dota_hero_";
    public static final String itemPrefix = "item_";
    public static final String killMessage = " is killed by ";


    @Override
    public Long ingestMatch(String payload) {

        MatchEntity m = new MatchEntity();
        m.setHeroEntityMap(new HashMap<>());


        final String[] payloadLines = payload.split("\\[");
        for (String payloadLine : payloadLines) {
            if (payloadLine.indexOf(']') != -1) {
                final String timestampString = payloadLine.substring(0, payloadLine.indexOf(']'));
                final Long timestamp = convertTimeStamp(timestampString);

                final String eventLine = payloadLine.substring(payloadLine.indexOf(']'), payloadLine.length());

                if (eventLine.contains(" buys item ")) {
                    processHeroItemLine(eventLine, timestamp, m);
                } else if (eventLine.contains(" is killed by ")) {
                    final String killer = eventLine.substring(eventLine.indexOf(killMessage) + killMessage.length());
                    if (killer.startsWith(npcPrefix)) {
                        String killerNpc = killer.substring(npcPrefix.length());
                        final HeroEntity heroEntity = m.getHeroEntityMap().getOrDefault(killerNpc, new HeroEntity());
                        heroEntity.setKills(heroEntity.getKills()+1);
                        heroEntityRepository.save(heroEntity);
                        m.getHeroEntityMap().put(killerNpc, heroEntity);
                    }
                } else if (eventLine.contains(" casts ability ")) {
                    // processSpellCastLine
                } else if (eventLine.contains(" hits ")) {
                    // processDamageLine
                }
            }

        }
        matchRepository.save(m);
        return Long.valueOf(m.getId());
    }

    @Override
    public List<HeroItems> getItems(Long matchId, String heroName) {
        final MatchEntity matchEntity = matchRepository.findById(matchId.intValue()).get();
        if (matchEntity == null) {
            return new ArrayList<>();
        } else {
            if (matchEntity.getHeroEntityMap().containsKey(heroName)) {
                final List<HeroItems> heroItemsList = matchEntity.getHeroEntityMap().get(heroName).getHeroItemsList().stream().map(a -> {
                    HeroItems hi = new HeroItems();
                    hi.setTimestamp(a.getTimestamp());
                    hi.setItem(a.getItem());
                    return hi;
                }).collect(Collectors.toList());
                return heroItemsList;
            } else {
                return new ArrayList<>();
            }
        }
    }

    @Override
    public List<HeroKills> getHeroKills(Long matchId) {
        final MatchEntity matchEntity = matchRepository.findById(matchId.intValue()).get();
        List<HeroKills> kills = new ArrayList<>();
        if (matchEntity == null) {
            return new ArrayList<>();
        } else {
            for (String s : matchEntity.getHeroEntityMap().keySet()) {
                HeroKills hk = new HeroKills();
                hk.setKills(matchEntity.getHeroEntityMap().get(s).getKills());
                hk.setHero(s);
                kills.add(hk);
            }
            return kills;
        }
    }

    private void processHeroItemLine(String eventLine, Long timestamp, MatchEntity m) {
        final String[] eventWords = eventLine.split(" ");
        String heroName = null;
        String itemName = null;
        for (String eventWord : eventWords) {
            if (eventWord.startsWith(npcPrefix)) {
                heroName = eventWord.substring(npcPrefix.length());
            }
            if (eventWord.startsWith(itemPrefix)) {
                itemName = eventWord.substring(itemPrefix.length());
            }
        }
        HeroItemsEntity hi = new HeroItemsEntity();
        hi.setItem(itemName);
        hi.setTimestamp(timestamp);
        heroItemsRepository.save(hi);
        HeroEntity heroEntity = m.getHeroEntityMap().getOrDefault(heroName, new HeroEntity());
        if (heroEntity.getHeroItemsList() == null) {
            heroEntity.setHeroItemsList(new ArrayList<>());
        }
        heroEntity.getHeroItemsList().add(hi);
        heroEntityRepository.save(heroEntity);
        m.getHeroEntityMap().put(heroName, heroEntity);
    }

    private Long convertTimeStamp(String timestamp) {
        int hours = Integer.parseInt(timestamp.substring(0, 2));
        int minutes = Integer.parseInt(timestamp.substring(3, 5));
        int seconds = Integer.parseInt(timestamp.substring(6, 8));
        int milliseconds = Integer.parseInt(timestamp.substring(9, 12));
        return Long.valueOf((hours * 60 * 60 * 1000) + (minutes * 60 * 1000) + (seconds * 1000) + milliseconds);
    }

}
