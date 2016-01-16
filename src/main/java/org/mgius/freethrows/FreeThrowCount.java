package org.mgius.freethrows;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class FreeThrowCount {
  private final int shotsTotal;
  private final int shotsSuccess;
  private final double shotOdds;

  public FreeThrowCount(int shotsTotal, int shotsSuccess) {
    this.shotsTotal = shotsTotal;
    this.shotsSuccess = shotsSuccess;
    this.shotOdds = (double) shotsSuccess / (double) shotsTotal;
  }

  public int getShotsTotal() {
    return shotsTotal;
  }

  public int getShotsSuccess() {
    return shotsSuccess;
  }

  public double getShotOdds() {
    return shotOdds;
  }

  @Override
  public boolean equals(Object obj) {
    return EqualsBuilder.reflectionEquals(this, obj);
  }

  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this,
        ToStringStyle.SHORT_PREFIX_STYLE);
  }

  public FreeThrowCount withNextShotMade(boolean shotMade) {
    if (shotMade) {
      return new FreeThrowCount(shotsTotal + 1, shotsSuccess + 1);
    } else {
      return new FreeThrowCount(shotsTotal + 1, shotsSuccess);
    }
  }
}
