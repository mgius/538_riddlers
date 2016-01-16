package org.mgius.swindler.strategy;

public class TakeFirst implements Strategy {


  @Override
  public boolean acceptOffer(Integer offer) {
    return true;
  }
}
