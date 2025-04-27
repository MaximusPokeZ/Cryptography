package ru.maximuspokez.utils.prime;

import ru.maximuspokez.utils.math.ExponentiationModulo;

import java.math.BigInteger;

public class MillerRabinTest extends AbstractPrimeTest {

  @Override
  protected boolean primeTest(BigInteger n, BigInteger a) {
    BigInteger nMinusOne = n.subtract(BigInteger.ONE);
    BigInteger d = nMinusOne;
    int s = 0;

    // n - 1 = 2^s * d
    while (d.mod(BigInteger.valueOf(2)).equals(BigInteger.ZERO)) {
      d = d.divide(BigInteger.valueOf(2));
      s++;
    }

    BigInteger newA = ExponentiationModulo.powMod(a, d, n);

    if (newA.equals(BigInteger.ONE) || newA.equals(nMinusOne)) {
      return true;
    }

    for (int r = 1; r < s; r++) {
      newA = newA.multiply(newA).mod(n);
      if (newA.equals(nMinusOne)) {
        return true;
      }
    }

    return false;
  }

}
