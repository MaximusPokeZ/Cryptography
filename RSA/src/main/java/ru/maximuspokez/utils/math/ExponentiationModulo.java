package ru.maximuspokez.utils.math;

import java.math.BigInteger;

public class ExponentiationModulo {

  public static BigInteger powMod(BigInteger a, BigInteger exponent, BigInteger mod) {
    if (mod.equals(BigInteger.ONE)) {
      return BigInteger.ZERO;
    }
    BigInteger result = BigInteger.ONE;
    a = a.mod(mod);
    while (exponent.compareTo(BigInteger.ZERO) > 0) {
      if (exponent.testBit(0)) {
        result = result.multiply(a).mod(mod);
      }
      a = a.multiply(a).mod(mod);
      exponent = exponent.shiftRight(1);
    }

    return result;
  }
}
