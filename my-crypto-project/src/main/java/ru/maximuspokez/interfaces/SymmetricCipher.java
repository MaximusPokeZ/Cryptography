package ru.maximuspokez.interfaces;

public interface SymmetricCipher {

  void setSymmetricKey(byte[] symmetricKey);

  byte[] encrypt(byte[] message);

  byte[] decrypt(byte[] ciphertext);
}
