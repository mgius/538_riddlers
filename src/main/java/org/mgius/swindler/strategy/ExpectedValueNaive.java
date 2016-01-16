package org.mgius.swindler.strategy;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class ExpectedValueNaive implements Strategy {
  private static final int offerCount = 5;
  SortedSet<Integer> offersSeen = new TreeSet<Integer>();


  public Set<Integer> getRemainingPotentialOffers() {
    Set<Integer> remainingOffers = new HashSet<Integer>();
    int lowest = offersSeen.first();
    int highest = offersSeen.last();

    int spread = highest - lowest;

    // Add offers between lowest and highest
    for (int i = lowest + 1; i < highest; i++) {
      remainingOffers.add(i);
    }

    // Add potential offers higher and lower.
    for (int i = 1; i < offerCount - spread; i++) {
      remainingOffers.add(lowest - i);
      remainingOffers.add(highest + i);
    }

    return remainingOffers;
  }

  public Double getExpectedOutcomeRemainingOffers() {
    return getRemainingPotentialOffers().stream()
        .mapToDouble(i -> i.doubleValue())
        .average().getAsDouble();

  }

  @Override
  public boolean acceptOffer(Integer offer) {
    offersSeen.add(offer);
    if (offersSeen.size() >= offerCount) {
      return true;
    }

    if (offer < getExpectedOutcomeRemainingOffers()) {
      return true;
    }
    return false;
  }
}
