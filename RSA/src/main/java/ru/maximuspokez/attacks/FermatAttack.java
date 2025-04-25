package ru.maximuspokez.attacks;

import ru.maximuspokez.utils.math.ExtendedEuclid;

import java.math.BigInteger;

public class FermatAttack {

  public record FermatAttackResult(BigInteger d, BigInteger phi) {
  }

  private record Factorization(BigInteger p, BigInteger q) {
  }

  public FermatAttackResult attack(BigInteger n, BigInteger e, int maxIters) {
    Factorization factorization = factorize(n, maxIters);
    if (factorization == null) {
      return null;
    }

    BigInteger p = factorization.p;
    BigInteger q = factorization.q;

    BigInteger phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
    BigInteger d = ExtendedEuclid.extendedEuclid(e, phi).x;
    if (d.compareTo(BigInteger.ZERO) < 0) {
      d = d.add(phi);
    }

    return new FermatAttackResult(d, phi);
  }

  private Factorization factorize(BigInteger n, int maxIters) {
    BigInteger a = sqrtCeil(n);
    BigInteger b2;
    BigInteger b;

    for (int i = 0; i < maxIters; i++) {
      b2 = a.pow(2).subtract(n);

      b = sqrtExact(b2);
      if (b != null) {
        BigInteger p = a.add(b);
        BigInteger q = a.subtract(b);
        return new Factorization(p, q);
      }

      a = a.add(BigInteger.ONE);
    }

    return null;
  }

  private BigInteger sqrtExact(BigInteger b2) {
    BigInteger sqrt = b2.sqrt();
    return sqrt.pow(2).equals(b2) ? sqrt : null;
  }

  private BigInteger sqrtCeil(BigInteger n) {
    BigInteger sqrt = n.sqrt();
    if (sqrt.pow(2).equals(n)) {
      return sqrt;
    }
    return sqrt.add(BigInteger.ONE);
  }
}
