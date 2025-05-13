package ru.maximuspokez.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.maximuspokez.constants.SymmetricCipher.CipherMode;
import ru.maximuspokez.constants.SymmetricCipher.PaddingMode;
import ru.maximuspokez.interfaces.SymmetricCipher;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.stream.IntStream;

public class SymmetricCipherContext {
  private static final Logger log = LoggerFactory.getLogger(SymmetricCipherContext.class);
  private final SymmetricCipher cipher;
  private final int blockSize;
  private final CipherMode cipherMode;
  private final PaddingMode paddingMode;
  private byte[] iv;
  private byte[] nonce;
  private final Object[] params;

  public SymmetricCipherContext(SymmetricCipher cipher, CipherMode cipherMode, PaddingMode paddingMode, byte[] iv, Object... params) {
    this.cipher = cipher;
    this.blockSize = cipher.getBlockSize();
    this.cipherMode = cipherMode;
    this.paddingMode = paddingMode;
    this.iv = (iv == null) ? null : iv.clone();
    this.params = (params == null) ? null : params.clone();
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

    if (iv != null && iv.length != blockSize) {
      throw new IllegalArgumentException("The length of IV must be equal to the block size: " + iv.length);
    }

    log.info("DATA LEN {}", data.length);
    byte[] IV = (iv != null) ? iv.clone() : new byte[blockSize];

    if (iv == null) {
      SecureRandom sr = new SecureRandom();
      sr.nextBytes(IV);
      iv = IV.clone();
    }

    byte[] result = new byte[data.length];
    int numBlocks = data.length / blockSize;
    switch (cipherMode) {
      case ECB:
        IntStream.range(0, numBlocks).parallel().forEach(i -> {
          int offset = i * blockSize;
          byte[] block = Arrays.copyOfRange(data, offset, offset + blockSize);
          byte[] preResult = isEncrypt ? cipher.encrypt(block) : cipher.decrypt(block);
          System.arraycopy(preResult, 0, result, offset, blockSize);
        });
        break;
      case CBC:
        if (isEncrypt) {
          for (int i = 0; i < data.length; i += blockSize) {
            byte[] block = Arrays.copyOfRange(data, i, i + blockSize);
            block = xor(IV, block);
            byte[] preResult = cipher.encrypt(block);
            System.arraycopy(preResult, 0, result, i, blockSize);
            IV = preResult;
          }
        } else {
          byte[][] decryptedBlocks = new byte[data.length / blockSize][blockSize];

          // параллельная дешифровка
          IntStream.range(0, data.length / blockSize).parallel().forEach(i -> {
            int offset = i * blockSize;
            byte[] block = Arrays.copyOfRange(data, offset, offset + blockSize);
            decryptedBlocks[i] = cipher.decrypt(block);
          });

          byte[][] previousCiphertexts = new byte[data.length / blockSize][blockSize];
          previousCiphertexts[0] = iv.clone();
          for (int i = 1; i < data.length / blockSize; i++) {
            previousCiphertexts[i] = Arrays.copyOfRange(data, (i - 1) * blockSize, i * blockSize);
          }

          IntStream.range(0, data.length / blockSize).parallel().forEach(i -> {
            byte[] plainBlock = xor(decryptedBlocks[i], previousCiphertexts[i]);
            System.arraycopy(plainBlock, 0, result, i * blockSize, blockSize);
          });
        }
        break;
      case PCBC:
        for (int i = 0; i < data.length; i += blockSize) {
          byte[] block = Arrays.copyOfRange(data, i, i + blockSize);
          if (isEncrypt) {
            byte[] blockToEncrypt = xor(IV, block);
            byte[] cipherBlock = cipher.encrypt(blockToEncrypt);
            System.arraycopy(cipherBlock, 0, result, i, blockSize);
            IV = xor(block, cipherBlock);
          } else {
            byte[] decrypted = cipher.decrypt(block);
            byte[] message = xor(IV, decrypted);
            System.arraycopy(message, 0, result, i, blockSize);
            IV = xor(message, block);
          }
        }
        break;
      case CFB:
        if (isEncrypt) {
          for (int i = 0; i < data.length; i += blockSize) {
            byte[] block = Arrays.copyOfRange(data, i, i + blockSize);
            byte[] preResult = cipher.encrypt(IV);
            preResult = xor(block, preResult);
            System.arraycopy(preResult, 0, result, i, blockSize);
            IV =  preResult;
          }
        } else {
            int nBlocks = data.length / blockSize;
            byte[][] ciphertextBlocks = new byte[nBlocks][];
            for (int i = 0; i < nBlocks; i++) { // копируем все Сi
              ciphertextBlocks[i] = Arrays.copyOfRange(data, i * blockSize, (i + 1) * blockSize);
            }

            byte[] finalIV = IV;
            IntStream.range(0, nBlocks).parallel().forEach(i -> {
              byte[] currentIV = (i == 0) ? finalIV : ciphertextBlocks[i - 1];
              byte[] keystream = cipher.encrypt(currentIV);
              byte[] decryptedBlock = xor(ciphertextBlocks[i], keystream);
              System.arraycopy(decryptedBlock, 0, result, i * blockSize, blockSize);
          });
        }
        break;
      case OFB:
        for(int i = 0; i < data.length; i += blockSize) {
          byte[] block = Arrays.copyOfRange(data, i, i + blockSize);
          byte[] keystream = cipher.encrypt(IV);
          byte[] preResult = xor(keystream, block);
          System.arraycopy(preResult, 0, result, i, blockSize);
          IV = keystream;
        }
        break;
      case CTR:
      case RANDOM_DELTA:
        int nonceLen = blockSize / 2;
        genNonce(nonceLen);

        int counterLength = blockSize - nonceLen;
        byte[] counter = new byte[counterLength];

        SecureRandom sr = null;
        boolean useRandomDelta = false;
        if (cipherMode == CipherMode.RANDOM_DELTA && params.length > 1) {
          long seed = ((Number) params[1]).longValue();
          try {
            sr = SecureRandom.getInstance("SHA1PRNG"); // тк не на всех платформах поддерживается
          } catch (Exception ex) {
            sr = new SecureRandom(); // работает неправильно, тк даже с одним seed будет генерировать разные числа в отличие от SHA1PRNG
          }
          sr.setSeed(seed);
          useRandomDelta = true;
        }

        byte[][] ivBlocks = new byte[numBlocks][blockSize];

        // Генерируем все IV
        for (int i = 0; i < numBlocks; i++) {
          byte[] ivBlock = new byte[blockSize]; // блок наполовину из nonce и наполовину из counter
          System.arraycopy(nonce, 0, ivBlock, 0, nonceLen);
          System.arraycopy(counter, 0, ivBlock, nonceLen, counterLength);
          ivBlocks[i] = ivBlock;

          if (useRandomDelta) {
            byte[] delta = new byte[counterLength];
            sr.nextBytes(delta);
            addCounter(counter, delta);
          } else {
            incrementCounter(counter);
          }
        }

        IntStream.range(0, numBlocks).parallel().forEach(i -> {
          int offset = i * blockSize;
          byte[] block = Arrays.copyOfRange(data, offset, offset + blockSize);
          byte[] keystream = cipher.encrypt(ivBlocks[i]);
          byte[] preResult = xor(keystream, block);
          System.arraycopy(preResult, 0, result, offset, blockSize);
        });
        break;
      default:
        break;
    }

    return result;
  }

  private void genNonce(int nonceLen) {
    if (nonce == null && params.length > 0 && params[0] != null) {
      nonce = ((byte[]) params[0]).clone();
    } else if (nonce == null) {
      nonce = new byte[nonceLen];
      SecureRandom sr = new SecureRandom();
      sr.nextBytes(nonce);
    }
  }

  private void incrementCounter(byte[] iv) {
    for (int i = iv.length - 1; i >= 0; i--) {
      iv[i]++;
      if (iv[i] != 0) {
        break;
      }
    }
  }

  private void addCounter(byte[] counter, byte[] delta) {
    int carry = 0;
    for (int i = counter.length - 1; i >= 0; i--) {
      int sum = (counter[i] & 0xFF) + (delta[i] & 0xFF) + carry;
      counter[i] = (byte) sum;
      carry = sum >> 8; // sum / 256
    }
  }

  private byte[] xor(byte[] a, byte[] b) {
    byte[] result = new byte[a.length];
    for (int i = 0; i < result.length; i++) {
      result[i] = (byte) (a[i] ^ b[i]);
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

    log.info("Success apply padding mode: {}", paddingMode);
    return padded;
  }

  private byte[] removePadding(byte[] data) {
    int len = 0;

    switch (paddingMode) {
      case ZEROS:
        for (int i = data.length - 1, j = blockSize; j >= 0 && data[i] == 0; i--, j--) {
          len++;
        }
        break;
      case PKCS7:
      case ANSI_X923:
      case ISO_10126:
        len = data[data.length - 1] & 0xFF;
        if (len == 0 || len > blockSize) {
          throw new IllegalArgumentException("Invalid padding length: " + len + ", block size: " + blockSize);
        }
        break;
      default:
        throw new IllegalArgumentException("unknown padding mode: " + paddingMode);
    }

    return Arrays.copyOfRange(data, 0, data.length - len);
  }

  public int getBlockSize() {
    return blockSize;
  }
}
