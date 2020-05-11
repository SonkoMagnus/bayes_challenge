bayes-dota
==========

This is the [task](TASK.md).

#### Introduction

I compiled some notes about my solution. I couldn't implement all the methods because I ran out of time, but you will see in the code my approach to the problem. A considerable amount of time was spent narrowing down a bug in Hibernate - I will detail that as well. 

I allocated 2 hours to work on this task. In the spirit of transparency, here are the tasks I did related to this, but did not count towards that limit. My reasoning is these are things that I would be familiar with if I was working all the time on this, and also many of them are one-off taks.

- Setting up my development environment (testing H2 Db, setting up git repo, configuring lombok plugin for intellij), and making sure everything works as intended
- Researching DOTA2 a bit (I'm familiar with the basic premise, but had to look up some things
- Analyzing the existing codebase, and dependencies
- Writing this document

All in all, these didn't really take that long, but had to be done.

#### Notes

Test data file name in the sample curl (in the [task](TASK.md).) was wrong - not sure if that was intentional. It was `combatlog_1.txt` instead of `combatlog_1.log.txt `

#### Implicit assumptions about the task

- Hero names are unique
- The combat log is well formatted, a line contains one unit of actionable information, and contains no typos, formatting errors, and "[" and "]" characters
are only present enclosing the timestamp.
- Similarly, the input is coming from a trusted source. If this is a public endpoint, then anyone can submit a combat log, and that 
opens up opportunities for cheating.
- "buys", "hits", "casts" are "reserved words" in the log and can't be hero, item, or spell names
- I assumed the returned timestamps are measured in milliseconds, judging by the example.

#### Additional observations

- "The response models of the REST interface should not be changed." - I wasn't sure if that meant that I can't modify the response classes all, or just the objects themselves, so I went with the first option, and created additional entities.
- When I found no data for the specified match/hero/etc. ids, I just return empty objects. In production, I would consider returning error messages or warnings, depending on the actual usage.
- I would design file submissions as multipart/form-data instead of submitting strings, I might want to have more info, or additionally separate file upload.
- Maybe because of my background, I would implement something like this as something that runs on an application server.
- I would've liked to do more input validation, with descriptive error messages of what exactly went wrong, and adjust the responses based on that. I decided that even though that's important, it does not seem to be in the spirit of the task. 
- In a similar vein, I would've liked to know the use case and target audience for this particular software - is it customer-facing? Do I need some kind of internationalization? Where will it run? How mission-critical is it (From my part this mostly means higher test coverage) What are the ops considerations, if any?
- In production, I would examine the future use of the data. It is my assumption that match data should be effectively immutable - either for "accounting" for tournament results, or even helping restore the game state. Bearing that in mind, I'd design the data storage layer with this in mind. Obviously out of scope here.
- I just put the constants i used into the MatchServiceImpl class, but in a production system, there are better places for them. (Either a constants class, or if localization is an issue, a property bundle)

#### The bug 

I discovered that if I use Lists as a members of an Entity class, the Hibernate CrudRepository/JPArepository classes do not handle well if I try  to insert a duplicate into that List. I intentionally left examples in the code where that could occur, for example, in the `HeroEntity` class, if the List is initalized this way:

```
 private List<HeroDamageEntity> heroDamageList = new ArrayList<>();
```

Later if I try to insert a duplicate entity (meaning it had the same item name as a previous one), then Hibernate generates an existing Id for it. Here is a (https://stackoverflow.com/questions/58026416/jpa-hibernate-ordered-list-throws-constraintviolationexception)(link) detailing the root of the problem. In the end I used a workaround by not initalizing the list, but using it this way in the `processHeroItemLine` method:

```
(...)
heroItemsRepository.save(hi);
HeroEntity heroEntity = m.getHeroEntityMap().getOrDefault(heroName, new HeroEntity());
        if (heroEntity.getHeroItemsList() == null) {
            heroEntity.setHeroItemsList(new ArrayList<>());
        }
        heroEntity.getHeroItemsList().add(hi);
        heroEntityRepository.save(heroEntity);
(...)
```



I don't know if you encountered this before, but I think you should be aware.

