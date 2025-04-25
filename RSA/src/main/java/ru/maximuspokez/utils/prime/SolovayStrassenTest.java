package ru.maximuspokez.utils.prime;

import ru.maximuspokez.utils.math.ExponentiationModulo;
import ru.maximuspokez.utils.math.JacobiSymbol;

import java.math.BigInteger;

public class SolovayStrassenTest extends AbstractPrimeTest {

  @Override
  protected boolean primeTest(BigInteger n, BigInteger a) {
    BigInteger exp = n.subtract(BigInteger.ONE).divide(BigInteger.valueOf(2));
    BigInteger aExp = ExponentiationModulo.powMod(a, exp, n);

    int jacobi = JacobiSymbol.calculate(a, n);

    if (jacobi == 0) {
      return false;
    }

    BigInteger jacobiMod = jacobi == 1 ? BigInteger.ONE : n.subtract(BigInteger.ONE);

    return jacobiMod.equals(aExp);
  }

}
