package ru.maximuspokez.utils.math;

import ru.maximuspokez.utils.prime.PrimeTest;

import java.math.BigInteger;

public class LegendreSymbol {

  private final PrimeTest primeTest;

  public LegendreSymbol(PrimeTest primeTest) {
    this.primeTest = primeTest;
  }

  public int calculate(BigInteger a, BigInteger p, double probability) {
    if (!p.testBit(0) || !primeTest.isPrime(p, probability)) {
      throw new IllegalArgumentException("p must be an odd prime");
    }

    a = a.mod(p);

    if (a.equals(BigInteger.ZERO)) {
      return 0;
    }

    if (a.equals(BigInteger.ONE)) {
      return 1;
    }

    // Критерий эйлера a^((p-1)/2) mod p
    BigInteger exponent = p.subtract(BigInteger.ONE).divide(BigInteger.TWO);
    BigInteger result = ExponentiationModulo.powMod(a, exponent, p);

    if (result.equals(BigInteger.ONE)) {
      return 1;
    } else if (result.equals(p.subtract(BigInteger.ONE))) {
      return -1;
    } else {
      return 0;
    }
  }
}
