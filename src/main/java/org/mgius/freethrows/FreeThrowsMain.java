package org.mgius.freethrows;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FreeThrowsMain {
  private Map<FreeThrowCount, Double> countProbability = new HashMap<>();

  public FreeThrowsMain() {
    initializeShots();
    calculateShotsFrom(3, 98);
    addFinalShot();
  }

  public static void main(String[] args) {
    final FreeThrowsMain freeThrowsMain = new FreeThrowsMain();

    for (int i = 3; i <= 100; i++) {
      System.out.println("Shot probability for shot " + i + " is " + freeThrowsMain.getShotProbabilityForShotNum(i));
    }
  }

  private List<Map.Entry<FreeThrowCount, Double>> getEntriesForShotNum(int shotNum) {
    final List<Map.Entry<FreeThrowCount, Double>> entries = countProbability
        .entrySet().stream()
        .filter(entry -> entry.getKey().getShotsTotal() == shotNum)
        .collect(Collectors.toList());

    return entries;
  }

  private void calculateShotsFrom(int firstShot, int lastShot) {
    for (int shotNum = firstShot; shotNum <= lastShot; shotNum++) {
      // Get all the shot probabilities for previous shot
      final List<Map.Entry<FreeThrowCount, Double>> entries = getEntriesForShotNum(shotNum - 1);

      // For each shot, figure out the contributory odds for each subsequent shot
      for (Map.Entry<FreeThrowCount, Double> entry : entries) {
        final FreeThrowCount count = entry.getKey();
        final Double countOdds = entry.getValue();

        double successProbability = count.getShotOdds();

        FreeThrowCount successCount = count.withNextShotMade(true);
        FreeThrowCount failureCount = count.withNextShotMade(false);

        double successOdds = countOdds * successProbability;
        double failureOdds = countOdds * (1.0d - successProbability);

        // Set odds if unset, otherwise add odds to existing odds
        countProbability.compute(successCount,
            (k, v) -> v == null ? successOdds : successOdds + v);

        countProbability.compute(failureCount,
            (k, v) -> v == null ? failureOdds : failureOdds + v);

      }
    }
  }

  private void initializeShots() {
    // Initialize known start state (2 shots thrown, 1 shot made)
    countProbability.put(new FreeThrowCount(2, 1), 1.0d);
  }

  private void addFinalShot() {
    // Add guaranteed good shot 99
    final List<Map.Entry<FreeThrowCount, Double>> entries = getEntriesForShotNum(98);

    for (Map.Entry<FreeThrowCount, Double> entry : entries) {
      final FreeThrowCount freeThrowCount = entry.getKey();
      countProbability.put(freeThrowCount.withNextShotMade(true), entry.getValue());
    }
  }

  public double getShotProbabilityForShotNum(int shotNum) {
    return getEntriesForShotNum(shotNum)
        .stream()
        .parallel()
        .mapToDouble(entry -> entry.getValue() * entry.getKey().getShotOdds())
        .sum();
  }
}
