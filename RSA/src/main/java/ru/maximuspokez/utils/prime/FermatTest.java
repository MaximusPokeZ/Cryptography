package ru.maximuspokez.utils.prime;

import ru.maximuspokez.utils.math.ExponentiationModulo;

import java.math.BigInteger;

public class FermatTest extends AbstractPrimeTest {

  @Override
  protected boolean primeTest(BigInteger n, BigInteger a) {
    return ExponentiationModulo.powMod(a, n.subtract(BigInteger.ONE), n).equals(BigInteger.ONE);
  }
}
