package ru.maximuspokez.rsa;

import java.math.BigInteger;

public class RsaService {

  public enum PrimaryTestType {
    FERMAT,
    SOLOVAY_STRASSEN,
    MILLER_RABIN
  }

  private RsaKeyPair keyPair;
  private final PrimaryTestType type;
  private final double probability;
  private final int bitLength;

  public RsaService(PrimaryTestType type, double probability, int bitLength) {
    this.type = type;
    this.probability = probability;
    this.bitLength = bitLength;
    regenerateKeys();
  }

  public void regenerateKeys() {
    this.keyPair = new KeyPairGenerator(type, probability, bitLength).generateKeyPair();
  }

  public BigInteger encrypt(BigInteger message) {
    return message.modPow(keyPair.publicKey().e(), keyPair.publicKey().n());
  }

  public BigInteger decrypt(BigInteger ciphertext) {
    return ciphertext.modPow(keyPair.privateKey().d(), keyPair.privateKey().n());
  }

  public RsaKeyPair getKeyPair() {
    return keyPair;
  }
}
