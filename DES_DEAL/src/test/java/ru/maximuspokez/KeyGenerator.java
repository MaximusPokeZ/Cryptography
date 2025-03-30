package ru.maximuspokez;

import java.security.SecureRandom;

public class KeyGenerator {

  private static final SecureRandom secureRandom = new SecureRandom();

  public static byte[] generateKey(int keySize) {
    byte[] key = new byte[keySize];
    secureRandom.nextBytes(key);
    return key;
  }
}
