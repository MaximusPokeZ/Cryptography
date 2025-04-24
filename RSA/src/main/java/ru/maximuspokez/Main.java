package ru.maximuspokez;

import ru.maximuspokez.utils.math.ExponentiationModulo;
import ru.maximuspokez.utils.math.ExtendedEuclid;
import ru.maximuspokez.utils.prime.FermatTest;
import ru.maximuspokez.utils.prime.PrimeTest;

import java.math.BigInteger;

public class Main {
  public static void main(String[] args) {
    System.out.println(ExtendedEuclid.extendedEuclid(BigInteger.valueOf(1071), BigInteger.valueOf(462)));

    System.out.println(ExponentiationModulo.powMod(BigInteger.valueOf(3), BigInteger.valueOf(13), BigInteger.valueOf(7)));


    PrimeTest primeTest = new FermatTest();
    System.out.println(primeTest.isPrime(BigInteger.valueOf(561), 0.9999));
  }
}