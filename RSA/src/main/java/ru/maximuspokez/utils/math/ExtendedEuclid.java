package ru.maximuspokez.utils.math;

import java.math.BigInteger;

public class ExtendedEuclid {

  public static class Result {
    public final BigInteger gcd;
    public final BigInteger x;
    public final BigInteger y;

    public Result(BigInteger gcd, BigInteger x, BigInteger y) {
      this.gcd = gcd;
      this.x = x;
      this.y = y;
    }

    @Override
    public String toString() {
      return String.format("gcd = %s, x = %s, y = %s", gcd, x, y);
    }
  }

  public static Result extendedEuclid(BigInteger a, BigInteger b) {
    if (b.equals(BigInteger.ZERO)) {
      return new Result(a, BigInteger.ONE, BigInteger.ZERO);
    }

    Result res = extendedEuclid(b, a.mod(b));
    BigInteger x = res.y;
    BigInteger y = res.x.subtract(a.divide(b).multiply(res.y));

    return new Result(res.gcd, x, y);
  }
}
