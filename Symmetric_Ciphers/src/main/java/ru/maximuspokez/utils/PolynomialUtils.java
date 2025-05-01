package ru.maximuspokez.utils;

import java.util.ArrayList;
import java.util.List;

public class PolynomialUtils {
  private PolynomialUtils() {}

  private static int degree(int p) {
    return (p == 0) ? -1 : 31 - Integer.numberOfLeadingZeros(p); // Работает быстрее чем циклом. Как одна CPU-инструкция
  }

  public static byte add(byte a, byte b) {
    return (byte) (a ^ b);
  }

  public static int add(int a, int b) {
    return a ^ b;
  }

  public static int multiply(int a, int b) {
    int result = 0;
    while (b != 0) {
      if ((b & 1) != 0) result ^= a;
      a <<= 1;
      b >>= 1;
    }
    return result;
  }

  public static int[] quotientAndRemainder (int a, int b) {
    if (b == 0) throw new ArithmeticException("Division by zero");

    int db = degree(b);

    int q = 0;
    int r = a;
    while (degree(r) >= db) {
      int shift = degree(r) - db;
      q ^= (1 << shift);
      r ^= (b << shift);
    }
    return new int[]{q, r};
  }

  public static int divide(int a, int b) {
    return quotientAndRemainder(a, b)[0];
  }

  public static int mod(int a, int b) {
    return quotientAndRemainder(a, b)[1];
  }

  public static int multiplyMod(int a, int b, int modulus) {
    return mod(multiply(a, b), modulus);
  }

  public static int[] extendedEuclid(int a, int b) {
    int prev_r = a, r = b;
    int prev_x = 1, x = 0;
    int prev_y = 0, y = 1;

    while (r != 0) {
      int quotient = divide(prev_r, r);

      int temp = r;
      r = prev_r ^ multiply(quotient, r);
      prev_r = temp;

      temp = x;
      x = prev_x ^ multiply(quotient, x);
      prev_x = temp;

      temp = y;
      y = prev_y ^ multiply(quotient, y);
      prev_y = temp;
    }

    return new int[]{prev_r, prev_x, prev_y};
  }

  public static boolean isIrreducible(int poly) {
    int deg = degree(poly);
    if (deg < 1) return false;

    if ((poly & 1) == 0) return false; // x = 0
    if (Integer.bitCount(poly) % 2 == 0) return false; // x = 1

    int maxDegreePoly = 1 << (deg / 2 + 1);
    for (int d = 2; d < maxDegreePoly; d++) {
      if (mod(poly, d) == 0) return false;
    }
    return true;
  }

  public static List<Integer> generateIrreduciblePolynomials(int degree) {
    List<Integer> result = new ArrayList<>();
    int start = 1 << degree;
    int end = 1 << (degree + 1);
    for (int i = start; i < end; i++) {
      if (isIrreducible(i)) {
        result.add(i);
      }
    }

    return result;
  }

  public static int inverse(int a, int modulus) {
    int[] res = extendedEuclid(a, modulus);
    int gcd = res[0];
    int inv = res[1];

    if (gcd != 1) throw new IllegalArgumentException("The inverse element does not exist: GCD ≠ 1");
    return inv;
  }

  public static List<Integer> factor(int poly) {
    List<Integer> result = new ArrayList<>();
    if (poly == 0) throw new IllegalArgumentException("Cannot factor zero");
    if (poly == 1) return result;

    int deg = degree(poly);

    for (int d = 1; d <= deg / 2; d++) {
      for (int p : generateIrreduciblePolynomials(d)) {
        while (mod(poly, p) == 0) {
          result.add(p);
          poly = divide(poly, p);
        }
      }
    }
    if (poly != 1) {
      result.add(poly);
    }

    return result;
  }


}
