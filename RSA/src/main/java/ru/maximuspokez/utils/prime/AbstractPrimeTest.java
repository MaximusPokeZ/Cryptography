package ru.maximuspokez.utils.prime;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

public abstract class AbstractPrimeTest implements PrimeTest {

  protected final Random random = new SecureRandom();

  protected abstract boolean primeTest(BigInteger n, BigInteger a);

  @Override
  public boolean isPrime(BigInteger n, double probability) {
    if (n.compareTo(BigInteger.TWO) < 0) return false;
    if (n.compareTo(BigInteger.valueOf(3)) <= 0) return true;

    int rounds = getRounds(probability);

    for (int i = 0; i < rounds; i++) {
      BigInteger a = getRandomBaseA(n);
      if (!primeTest(n, a)) return false;
    }
    
    return true;
  }

  protected BigInteger getRandomBaseA(BigInteger n) {
    BigInteger a;
    do {
      a = new BigInteger(n.bitLength(), random);
    } while (a.compareTo(BigInteger.TWO) < 0 || a.compareTo(n.subtract(BigInteger.TWO)) > 0);

    return a;
  }

  protected int getRounds(double probability) {
    if (probability < 0.5 || probability >= 1.0) {
      throw new IllegalArgumentException("Probability must be in range [0.5, 1)");
    }

    return (int) Math.ceil(Math.log(1.0 / (1.0 - probability)) / Math.log(2));
  }

}
