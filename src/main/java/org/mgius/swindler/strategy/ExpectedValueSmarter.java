package org.mgius.swindler.strategy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ExpectedValueSmarter implements Strategy {
  private final Integer interval;

  Logger logger = Logger.getLogger("ExpectedValueSmarter");

  private static final int offerCount = 5;
  SortedSet<Integer> offersSeen = new TreeSet<Integer>();

  public ExpectedValueSmarter(Integer interval) {
    this.interval = interval;
  }

  public ExpectedValueSmarter() {
    this(1);
  }

  // Get the lowest value not seen which is higher than at least one seen value
  private Integer lowestNotSeen() {
    if (offersSeen.isEmpty()) {
      return Integer.MAX_VALUE;
    }

    final List<Integer> sortedOffers = offersSeen.stream().sorted().collect(Collectors.toList());

    int candidate = offersSeen
        .stream().sorted().skip(1)
        .reduce(offersSeen.first(), (a, b) -> a + interval == b ? b : a);

    candidate++;
//    logger.info("Last not seen between spread: " + candidate + ". With offersSeen: " + offersSeen.stream().map(i -> i.toString()).collect(Collectors.joining(", ")).toString());
    return candidate;
  }

  public Collection<Integer> getRemainingPotentialAcceptedOffers() {
    List<Integer> remainingOffers = new ArrayList<Integer>();
    int lowest = offersSeen.first();
    int highest = offersSeen.last();
    int lowestNotSeen = lowestNotSeen();

    for (int i = lowest; i < highest; i += interval) {
      if (offersSeen.contains(i)) {
        continue;
      }
      // for each offer between the spread, we would never accept anything greater than
      // lowest not seen between the spread
      remainingOffers.add(lowestNotSeen);
    }

    int spread = highest - lowest;

    // Add potential offers lower, and lowestNotSeen
    for (int i = 1; i < offerCount - spread; i++) {
      remainingOffers.add(lowest - i);
      remainingOffers.add(lowestNotSeen);
    }

    return remainingOffers;
  }

  public Double getExpectedOutcomeRemainingOffers() {
    return getRemainingPotentialAcceptedOffers().stream()
        .mapToDouble(i -> i.doubleValue())
        .average().getAsDouble();

  }

  @Override
  public boolean acceptOffer(Integer offer) {
    // If this offer is higher than the highest offer we know must exist then reject immediately
    if (offer > lowestNotSeen()) {
      offersSeen.add(offer);
      return false;
    }

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
