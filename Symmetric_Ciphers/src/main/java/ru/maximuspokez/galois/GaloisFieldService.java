package ru.maximuspokez.galois;

import ru.maximuspokez.exceptions.ReducedModuleException;
import ru.maximuspokez.utils.PolynomialUtils;

import java.util.ArrayList;
import java.util.List;

public class GaloisFieldService {

  private GaloisFieldService() {}

  private static void checkModulus(byte modulus) {
    int m = modulus & 0xFF;
    if (!PolynomialUtils.isIrreducible(m))
      throw new ReducedModuleException("The module must be irreducible!");
  }
  public static byte add(byte a, byte b, byte modulus) {
    checkModulus(modulus);
    return PolynomialUtils.add(a, b);
  }

  public static byte multiply(byte a, byte b, byte modulus) {
    checkModulus(modulus);
    int result = PolynomialUtils.multiplyMod(a & 0xFF, b & 0xFF, modulus & 0xFF);
    return (byte) result;
  }

  public static byte inverse(byte a, byte modulus) {
    checkModulus(modulus);
    if (a == 0) throw new IllegalArgumentException("Zero has no inverse element!");

    int inv = PolynomialUtils.inverse(a & 0xFF, modulus & 0xFF);
    return (byte) inv;
  }

  public static boolean isIrreducible(byte polynomial) {
    return PolynomialUtils.isIrreducible(polynomial & 0xFF);
  }

  public static List<Byte> getAllIrreduciblePolynomialsOfDegree8() {
    List<Integer> polys = PolynomialUtils.generateIrreduciblePolynomials(8);
    List<Byte> result = new ArrayList<>();
    for (Integer p : polys) {
      result.add(p.byteValue());
    }
    return result;
  }

  public static List<Byte> factor(byte polynomial) {
    int p = polynomial & 0xFF;
    List<Integer> factors = PolynomialUtils.factor(p);
    return factors.stream().map(Integer::byteValue).toList();
  }

}
