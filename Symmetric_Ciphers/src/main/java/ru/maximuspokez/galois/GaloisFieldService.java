package ru.maximuspokez.galois;

import ru.maximuspokez.exceptions.ReducedModuleException;
import ru.maximuspokez.utils.PolynomialUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GaloisFieldService {

  private static final Random RANDOM = new Random();

  private GaloisFieldService() {}

  private static void checkModulus(int modulus) {
    if (!PolynomialUtils.isIrreducible(modulus))
      throw new ReducedModuleException("The module must be irreducible!");
  }

  public static byte add(byte a, byte b, int modulus) {
    checkModulus(modulus);
    return PolynomialUtils.add(a, b);
  }

  public static byte multiply(byte a, byte b, int modulus) {
    checkModulus(modulus);
    int result = PolynomialUtils.multiplyMod(a & 0xFF, b & 0xFF, modulus);
    return (byte) result;
  }

  public static byte inverse(byte a, int modulus) {
    checkModulus(modulus);
    if (a == 0) throw new IllegalArgumentException("Zero has no inverse element!");

    int inv = PolynomialUtils.inverse(a & 0xFF, modulus);
    return (byte) inv;
  }

  public static boolean isIrreducible(int polynomial) {
    return PolynomialUtils.isIrreducible(polynomial);
  }

  public static List<Integer> getAllIrreduciblePolynomialsOfDegree8() {
    return PolynomialUtils.generateIrreduciblePolynomials(8);
  }

  public static int getRandomIrreduciblePolynomial() {
    List<Integer> polys = getAllIrreduciblePolynomialsOfDegree8();
    return polys.get(RANDOM.nextInt(polys.size()));
  }

  public static List<Byte> factor(byte polynomial) {
    int p = polynomial & 0xFF;
    List<Integer> factors = PolynomialUtils.factor(p);
    return factors.stream().map(Integer::byteValue).toList();
  }

}
