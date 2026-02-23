# Tabletop Tournamentor

A JavaFX application to organize (tabletop) game tournaments. It runs rounds, keeps track of scores, shuffles players and games.

# Shuffling
This program has various shuffling algorithms implemented, they are at most based on the players and their scores. 
Swiss or Round Robin aren't supported.
<br/>
Shuffling aglorithms:
- *Pure Shuffle*: Players shuffled.
- *Pair Shuffle*: Players shuffled, favoring games in pairs.
- *Basic Rank Shuffle*: Players sorted by score.
- *Weighted Rank Shuffle*: Players with similar scores are more likely to be shuffled together.
- *Group Shuffle*: Players shuffled, favoring bigger groups for games.

Shuffling are automatically selected  (but can be changed) based on round number, rotating in 3:
- Round 1: Pure Shuffle
- Round 2: Pair Shuffle
- Round 3: Weighted Rank Shuffle
- Round 4 is Round 1 again, etc.

Build it:
```shell
mvn clena javafx:jlink
```

Run it:
```shell
./target/app/bin/app
```
