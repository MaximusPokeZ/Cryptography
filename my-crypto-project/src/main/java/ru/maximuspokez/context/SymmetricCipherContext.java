package ru.maximuspokez.context;

import ru.maximuspokez.constants.CipherMode;
import ru.maximuspokez.constants.PaddingMode;
import ru.maximuspokez.interfaces.SymmetricCipher;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SymmetricCipherContext {
  private final SymmetricCipher cipher;
  private final int blockSize;
  private final CipherMode cipherMode;
  private final PaddingMode paddingMode;
  private final byte[] iv;
  private final Object[] params;
  private final ExecutorService executor;

  public SymmetricCipherContext(SymmetricCipher cipher, CipherMode cipherMode, PaddingMode paddingMode, byte[] iv, Object... params) {
    this.cipher = cipher;
    this.blockSize = cipher.getBlockSize();
    this.cipherMode = cipherMode;
    this.paddingMode = paddingMode;
    this.iv = (iv == null) ? null : iv.clone();
    this.params = params;
    this.executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() / 2 + 1);
  }

  public byte[] encrypt(byte[] message) {
    byte[] padded = applyPadding(message);
    return processBlocks(padded, true);
  }

  public byte[] decrypt(byte[] ciphertext) {
    byte[] decrypted = processBlocks(ciphertext, false);
    return removePadding(decrypted);
  }

  private byte[] processBlocks(byte[] data, boolean isEncrypt) {
    if (data.length % blockSize != 0) {
      throw new IllegalArgumentException("data length must be a multiple of block size: " + data.length);
    }

    byte[] result = new byte[data.length];
    switch (cipherMode) {
      case ECB:
        for (int i = 0; i < data.length; i+=blockSize) {
          byte[] block = Arrays.copyOfRange(data, i, i + blockSize);
          byte[] preResult = isEncrypt ? cipher.encrypt(block) : cipher.decrypt(block);
          System.arraycopy(preResult, 0, result, i, blockSize);
        }
        break;
      default:
        break;
    }

    return result;
  }

  private byte[] applyPadding(byte[] data) {
    int padSize = blockSize - data.length % blockSize; // сколько надо добавить до кратности
    if (padSize == 0) {
      padSize = blockSize; // для PKCS#7 чтобы правильно убрать padding
    }

    byte[] padded = new byte[data.length + padSize];
    System.arraycopy(data, 0, padded, 0, data.length);

    switch (paddingMode) {
      case ZEROS:
        break;
      case PKCS7:
        for (int i = data.length; i < padded.length; i++) {
          padded[i] = (byte) padSize;
        }
        break;
      case ISO_10126:
        SecureRandom sr = new SecureRandom();
        for (int i = data.length; i < padded.length - 1; i++) {
          padded[i] = (byte) (sr.nextInt(256));
        }
        padded[padded.length - 1] = (byte) (padSize);
        break;
      case ANSI_X923:
        for (int i = data.length; i < padded.length - 1; i++) {
          padded[i] = (byte) (0x00);
        }
        padded[padded.length - 1] = (byte) (padSize);
        break;
      default:
        throw new IllegalArgumentException("unknown padding mode: " + paddingMode);
    }

    return padded;
  }

  private byte[] removePadding(byte[] data) {
    int len = 0;

    switch (paddingMode) {
      case ZEROS:
        for (int i = data.length - 1; i >= 0 && data[i] == 0; i--) {
          len++;
        }
        break;
      case PKCS7:
      case ANSI_X923:
      case ISO_10126:
        len = data[data.length - 1] & 0xFF;
        if (len == 0 || len > blockSize) {
          throw new IllegalArgumentException("Invalid padding length: " + len);
        }
        break;
      default:
        throw new IllegalArgumentException("unknown padding mode: " + paddingMode);
    }

    return Arrays.copyOfRange(data, 0, data.length - len);
  }
}
