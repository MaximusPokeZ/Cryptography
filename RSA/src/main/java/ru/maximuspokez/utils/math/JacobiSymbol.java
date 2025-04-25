package ru.maximuspokez.utils.math;

import java.math.BigInteger;

// https://planetmath.org/calculatingthejacobisymbol
// https://neerc.ifmo.ru/wiki/index.php?title=Алгоритм_вычисления_символа_Якоби

public class JacobiSymbol {

  public static int calculate(BigInteger a, BigInteger n) {
    if (n.testBit(1) && n.signum() <= 0) {
      throw new IllegalArgumentException("n must be positive and odd");
    }

    if (a.equals(BigInteger.ZERO)) {
      return 0;
    }

    if (a.equals(BigInteger.ONE)) {
      return 1;
    }

    a = a.mod(n);

    if (a.equals(BigInteger.ZERO)) {
      return 0;
    }

    int res = 1;

    while (!a.equals(BigInteger.ZERO)) {
      // 2
      while (!a.testBit(0)) {
        a = a.shiftRight(1);
        BigInteger nMod8 = n.mod(BigInteger.valueOf(8));
        if (nMod8.equals(BigInteger.valueOf(3)) || nMod8.equals(BigInteger.valueOf(5))) {
          res = -res;
        }
      }

      BigInteger temp = a;
      a = n;
      n = temp;

      if (a.mod(BigInteger.valueOf(4)).equals(BigInteger.valueOf(3)) && n.mod(BigInteger.valueOf(4)).equals(BigInteger.valueOf(3))) {
        res = -res;
      }

      a = a.mod(n);
    }

    return n.equals(BigInteger.ONE) ? res : 0;
  }
}
