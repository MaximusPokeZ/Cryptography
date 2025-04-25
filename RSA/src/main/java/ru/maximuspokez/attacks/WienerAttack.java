package ru.maximuspokez.attacks;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class WienerAttack {
  public record  WienerAttackResult(BigInteger d, BigInteger phi, List<Fraction> fractions) {

  }

  public record Fraction(BigInteger n, BigInteger d) {
    @Override
    public String toString() {
      return n.toString() + "/" + d.toString();
    }
  }


  public WienerAttackResult attack(BigInteger e, BigInteger n) {
    List<Fraction> fractions = CalcContinuedFractions(e, n);

    for (Fraction fraction : fractions) {
      BigInteger ki = fraction.n;
      BigInteger di = fraction.d;

      if (ki.equals(BigInteger.ZERO)) {
        continue;
      }

      BigInteger phi = e.multiply(di).subtract(BigInteger.ONE).divide(ki);
      BigInteger b = n.subtract(phi).add(BigInteger.ONE);
      BigInteger D = b.multiply(b).subtract(n.multiply(BigInteger.valueOf(4)));

      if (D.signum() >= 0 && isPerfectSquare(D)) {
        return new WienerAttackResult(di, phi, fractions);
      }
    }

    return null;
  }

  private List<Fraction> CalcContinuedFractions(BigInteger e, BigInteger n) {
    List<BigInteger> quotients = new ArrayList<>();

    BigInteger a = e;
    BigInteger b = n;
    while (!b.equals(BigInteger.ZERO)) {
      quotients.add(a.divide(b));
      BigInteger temp = a.mod(b);
      a = b;
      b = temp;
    }

    List<Fraction> fractions = new ArrayList<>();

    BigInteger p0 = BigInteger.ONE;
    BigInteger q0 = BigInteger.ZERO;              // p_i = a_i * p_i-1 + p_i-2
    BigInteger p1 = quotients.get(0);             // q_i = a_i * q_i-1 + q_i-2
    BigInteger q1 = BigInteger.ONE;

    fractions.add(new Fraction(p1, q1));

    for (int i = 1; i < quotients.size(); i++) {
      BigInteger ai = quotients.get(i);

      BigInteger p2 = ai.multiply(p1).add(p0);
      BigInteger q2 = ai.multiply(q1).add(q0);

      fractions.add(new Fraction(p2, q2));

      p0 = p1;
      q0 = q1;
      p1 = p2;
      q1 = q2;
    }

    return fractions;
  }

  private boolean isPerfectSquare(BigInteger n) {
    BigInteger sqrt = n.sqrt();
    return sqrt.pow(2).equals(n);
  }

}
