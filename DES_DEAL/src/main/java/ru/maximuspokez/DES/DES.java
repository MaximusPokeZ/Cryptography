package ru.maximuspokez.DES;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.maximuspokez.feistel.Feistel;
import ru.maximuspokez.interfaces.EncryptionTransformation;
import ru.maximuspokez.interfaces.KeyExpansion;
import ru.maximuspokez.interfaces.SymmetricCipher;

public class DES implements SymmetricCipher {
  private static final Logger log = LoggerFactory.getLogger(DES.class);
  private final Feistel feistel;
  private final KeyExpansion keyExpansion;
  private byte[][] roundKeys;

  public DES(KeyExpansion keyExpansion, EncryptionTransformation roundFunction) {
    this.keyExpansion = keyExpansion;
    this.feistel = new Feistel(roundFunction, 16);
    this.roundKeys = null;

    log.info("DES initialized");
  }

  public DES() {
    this.keyExpansion = new KeyExpansionImpl();
    EncryptionTransformation roundFunction = new DesRoundFunction();
    this.feistel = new Feistel(roundFunction, 16);
    this.roundKeys = null;

    log.info("DES initialized");
  }

  public DES(DES other) {
    this.keyExpansion = other.keyExpansion;
    this.feistel = other.feistel;
    this.roundKeys = other.roundKeys;
  }

  @Override
  public void setSymmetricKey(byte[] symmetricKey) {
    log.info("Starting DES generation roundKeys");
    this.roundKeys = keyExpansion.generateRoundKeys(symmetricKey);
  }

  @Override
  public byte[] encrypt(byte[] message) {
    if (roundKeys == null) {
      throw new IllegalStateException("Round keys are not set. Call setSymmetricKey first!!!!");
    }

    log.info("Starting DES encryption");
    return feistel.encrypt(message, roundKeys);
  }

  @Override
  public byte[] decrypt(byte[] ciphertext) {
    if (roundKeys == null) {
      throw new IllegalStateException("Round keys are not set. Call setSymmetricKey first!!!!");
    }

    log.info("Starting DES decryption");
    return feistel.decrypt(ciphertext, roundKeys);
  }

  @Override
  public int getBlockSize() {
    return 8;
  }
}
