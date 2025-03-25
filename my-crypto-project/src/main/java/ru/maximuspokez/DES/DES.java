package ru.maximuspokez.DES;

import ru.maximuspokez.feistel.Feistel;
import ru.maximuspokez.interfaces.EncryptionTransformation;
import ru.maximuspokez.interfaces.KeyExpansion;
import ru.maximuspokez.interfaces.SymmetricCipher;

public class DES implements SymmetricCipher {
  private final Feistel feistel;
  private final KeyExpansion keyExpansion;
  private byte[][] roundKeys;

  public DES(KeyExpansion keyExpansion, EncryptionTransformation roundFunction) {
    this.keyExpansion = keyExpansion;
    this.feistel = new Feistel(roundFunction, 16);
    this.roundKeys = null;
  }

  @Override
  public void setSymmetricKey(byte[] symmetricKey) {
    this.roundKeys = keyExpansion.generateRoundKeys(symmetricKey);
  }

  @Override
  public byte[] encrypt(byte[] message) {
    if (roundKeys == null) {
      throw new IllegalStateException("Round keys are not set. Call setSymmetricKey first!!!!");
    }
    return feistel.encrypt(message, roundKeys);
  }

  @Override
  public byte[] decrypt(byte[] ciphertext) {
    if (roundKeys == null) {
      throw new IllegalStateException("Round keys are not set. Call setSymmetricKey first!!!!");
    }
    return feistel.decrypt(ciphertext, roundKeys);
  }
}
