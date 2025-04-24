package ru.maximuspokez.utils.math;

import java.math.BigInteger;

public class Euclid {
  public static BigInteger euclid(BigInteger a, BigInteger b) {
    while (!b.equals(BigInteger.ZERO)) {
      BigInteger temp = b;
      b = a.mod(b);
      a = temp;
    }
    return a;
  }
}
