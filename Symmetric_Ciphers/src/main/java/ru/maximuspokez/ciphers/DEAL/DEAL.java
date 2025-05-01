package ru.maximuspokez.ciphers.DEAL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.maximuspokez.ciphers.DES.DES;
import ru.maximuspokez.constants.DealKeySize;
import ru.maximuspokez.interfaces.KeyExpansion;
import ru.maximuspokez.interfaces.SymmetricCipher;

import java.util.Arrays;

public class DEAL implements SymmetricCipher {
  private static final Logger log = LoggerFactory.getLogger(DEAL.class);
  private final KeyExpansion keyExpansion;
  private final ThreadLocal<DES> threadLocalDES;
  private byte[][] roundKeys;
  private final int blockSize = 16;

  public DEAL(DealKeySize keySize, byte[] keyForDes) {
    this.threadLocalDES = ThreadLocal.withInitial(() -> {
      DES des = new DES();
      des.setSymmetricKey(keyForDes);
      return des;
    });
    this.keyExpansion = new DealKeyExpansionImpl(keySize, threadLocalDES.get());
  }

  @Override
  public void setSymmetricKey(byte[] symmetricKey) {
    log.info("Generating round keys for DEAL");
    this.roundKeys = keyExpansion.generateRoundKeys(symmetricKey);
  }

  @Override
  public byte[] encrypt(byte[] message) {
    if (roundKeys == null) {
      throw new IllegalStateException("Round keys are not set. Call setSymmetricKey first!");
    }
    if (message.length != blockSize) {
      throw new IllegalArgumentException("Message must be exactly 128 bits");
    }
    log.info("Starting DEAL encryption");

    byte[] L = Arrays.copyOfRange(message, 0, 8);
    byte[] R = Arrays.copyOfRange(message, 8, 16);
    for (byte[] roundKey : roundKeys) {
      threadLocalDES.get().setSymmetricKey(roundKey);
      byte[] pre = xor(R, threadLocalDES.get().encrypt(L));
      R = L;
      L = pre;
    }
    byte[] result = new byte[blockSize];
    System.arraycopy(L, 0, result, 0, 8);
    System.arraycopy(R, 0, result, 8, 8);
    return result;
  }

  @Override
  public byte[] decrypt(byte[] ciphertext) {
    if (roundKeys == null) {
      throw new IllegalStateException("Round keys are not set. Call setSymmetricKey first!");
    }
    if (ciphertext.length != blockSize) {
      throw new IllegalArgumentException("Ciphertext must be exactly 128 bits");
    }
    log.info("Starting DEAL decryption");

    byte[] L = Arrays.copyOfRange(ciphertext, 0, 8);
    byte[] R = Arrays.copyOfRange(ciphertext, 8, 16);
    for (int i = roundKeys.length - 1; i >= 0; i--) {
      threadLocalDES.get().setSymmetricKey(roundKeys[i]);
      byte[] pre = xor(L, threadLocalDES.get().encrypt(R));
      L = R;
      R = pre;
    }
    byte[] result = new byte[blockSize];
    System.arraycopy(L, 0, result, 0, 8);
    System.arraycopy(R, 0, result, 8, 8);
    return result;
  }

  @Override
  public int getBlockSize() {
    return blockSize;
  }

  private byte[] xor(byte[] a, byte[] b) {
    byte[] result = new byte[a.length];
    for (int i = 0; i < a.length; i++) {
      result[i] = (byte) (a[i] ^ b[i]);
    }
    return result;
  }
}
