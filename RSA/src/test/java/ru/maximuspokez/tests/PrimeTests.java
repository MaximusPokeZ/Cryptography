package ru.maximuspokez.tests;

import org.junit.jupiter.api.Test;
import ru.maximuspokez.utils.prime.FermatTest;
import ru.maximuspokez.utils.prime.MillerRabinTest;
import ru.maximuspokez.utils.prime.PrimeTest;
import ru.maximuspokez.utils.prime.SolovayStrassenTest;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PrimeTests {

  @Test
  void testSimplePrimes() {
    PrimeTest fermat = new FermatTest();
    PrimeTest solovay = new SolovayStrassenTest();
    PrimeTest miller = new MillerRabinTest();

    BigInteger[] primes = {
            BigInteger.valueOf(2),
            BigInteger.valueOf(3),
            BigInteger.valueOf(5),
            BigInteger.valueOf(17),
            BigInteger.valueOf(101),
            BigInteger.valueOf(10601),
    };

    for (BigInteger prime : primes) {
      assertTrue(fermat.isPrime(prime, 0.99));
      assertTrue(solovay.isPrime(prime, 0.99));
      assertTrue(miller.isPrime(prime, 0.99));
    }
  }

  @Test
  void testComposites() {
    FermatTest fermat = new FermatTest();
    SolovayStrassenTest solovay = new SolovayStrassenTest();
    MillerRabinTest miller = new MillerRabinTest();

    BigInteger[] composites = {
            BigInteger.valueOf(4),
            BigInteger.valueOf(15),
            BigInteger.valueOf(21),
            BigInteger.valueOf(100),
            new BigInteger("123456")
    };

    for (BigInteger composite : composites) {
      assertFalse(fermat.isPrime(composite, 0.99));
      assertFalse(solovay.isPrime(composite, 0.99));
      assertFalse(miller.isPrime(composite, 0.99));
    }
  }

  @Test
  void testCarmichaelNumbers() {
    FermatTest fermat = new FermatTest();
    SolovayStrassenTest solovay = new SolovayStrassenTest();
    MillerRabinTest miller = new MillerRabinTest();

    BigInteger[] composites = {
            BigInteger.valueOf(561),
            BigInteger.valueOf(1105),
            BigInteger.valueOf(1729),
            BigInteger.valueOf(2465),
            new BigInteger("8911")
    };

    for (BigInteger composite : composites) {
      assertFalse(fermat.isPrime(composite, 0.99));
      assertFalse(solovay.isPrime(composite, 0.99));
      assertFalse(miller.isPrime(composite, 0.99));
    }
  }

}
