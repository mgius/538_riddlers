package org.mgius.swindler;

import org.mgius.swindler.strategy.ExpectedValueNaive;
import org.mgius.swindler.strategy.ExpectedValueSmarter;
import org.mgius.swindler.strategy.Strategy;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SwindlerMain {
  public static List<Integer> getPrices() {
    Integer[] prices = new Integer[]{0, 1, 2, 3, 4};
    return Arrays.asList(prices);
  }

  public static List<Integer> getShuffledPrices() {
    List<Integer> prices = getPrices();
    Collections.shuffle(prices);
    return prices;
  }

  public static Integer runStrategyOnPrices(Supplier<Strategy> getStrategy, List<Integer> prices) {
    Strategy toTest = getStrategy.get();
    for (Integer price : prices) {
      if (toTest.acceptOffer(price)) {
        return price;
      }
    }
    return prices.get(prices.size() - 1);
  }

  public static double findOverpaymentAverage(Supplier<Strategy> getStrategy, List<List<Integer>> testCases) throws Exception {
    return testCases.stream()
        .mapToDouble(testCase -> runStrategyOnPrices(getStrategy, testCase))
        .average().orElseThrow(() -> new Exception());
  }

  public static void main(String [] args) throws Exception {
    final List<List<Integer>> testCases = IntStream
//        .range(1, 1000)
        .range(1, 100000)
        .mapToObj(i -> getShuffledPrices())
        .collect(Collectors.toList());

//    final double takeFirstAverage = findOverpaymentAverage(TakeFirst::new, testCases);
//    final double takeLastAverage = findOverpaymentAverage(TakeLast::new, testCases);
    final double naiveAverage = findOverpaymentAverage(ExpectedValueNaive::new, testCases);
    final double smarterAverage = findOverpaymentAverage(ExpectedValueSmarter::new, testCases);

//    System.out.println("TakeFirst: " + takeFirstAverage);
//    System.out.println("TakeLast: " + takeLastAverage);
    System.out.println("naive: " + naiveAverage);
    System.out.println("Smarter: " + smarterAverage);

  }


}
