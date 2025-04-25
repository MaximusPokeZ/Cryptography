package ru.maximuspokez.rsa;

import ru.maximuspokez.utils.math.Euclid;
import ru.maximuspokez.utils.math.ExtendedEuclid;
import ru.maximuspokez.utils.prime.FermatTest;
import ru.maximuspokez.utils.prime.MillerRabinTest;
import ru.maximuspokez.utils.prime.PrimeTest;
import ru.maximuspokez.utils.prime.SolovayStrassenTest;

import java.math.BigInteger;
import java.security.SecureRandom;

public class KeyPairGenerator {
  private final PrimeTest primeTest;
  private final double probability;
  private final int bitLength;

  private final SecureRandom random = new SecureRandom();


  public KeyPairGenerator(RsaService.PrimaryTestType type, double probability, int bitLength) {
    this.probability = probability;
    this.bitLength = bitLength;
    this.primeTest = switch (type) {
      case FERMAT -> new FermatTest();
      case SOLOVAY_STRASSEN -> new SolovayStrassenTest();
      case MILLER_RABIN -> new MillerRabinTest();
    };
  }

  public RsaKeyPair generateKeyPair() {
    BigInteger p, q, n, e, phi, d;

    p = getRandomPrime();
    do {
      q = getRandomPrime();
      n = p.multiply(q);
    } while (!preventFermat(p, q, n));

    phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));

    e = BigInteger.valueOf(65_537);
    if (Euclid.euclid(e, phi).equals(BigInteger.ONE)) {
      do {
        e = new BigInteger(bitLength / 2, random);
      } while (!Euclid.euclid(e, phi).equals(BigInteger.ONE));
    }

    d = ExtendedEuclid.extendedEuclid(e, phi).x;
    if (d.compareTo(BigInteger.ZERO) < 0) {
      d = d.add(phi);
    }

    // Wiener d > n^{1/4}
    BigInteger nFourth = n.sqrt().sqrt();
    if (d.compareTo(nFourth) <= 0) {
      return generateKeyPair();
    }

    PublicKey publicKey = new PublicKey(n, e);
    PrivateKey privateKey = new PrivateKey(n, d);

    return new RsaKeyPair(privateKey, publicKey);
  }

  private BigInteger getRandomPrime() {
    BigInteger p;
    do {
      p = new BigInteger(bitLength, random)
              .setBit(bitLength - 1)
              .setBit(0);
    } while (!primeTest.isPrime(p, probability));

    return p;
  }

  private boolean preventFermat(BigInteger p, BigInteger q, BigInteger n) {
    BigInteger diff = p.subtract(q).abs();

    int shift = n.bitLength() / 2 - 100;
    BigInteger exp = BigInteger.ONE.shiftLeft(shift);

    return diff.compareTo(exp) > 0;
  }
}
