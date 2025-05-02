package ru.maximuspokez.ciphers.Rijndael;

import ru.maximuspokez.config.RijndaelConfiguration;
import ru.maximuspokez.galois.GaloisFieldService;
import ru.maximuspokez.interfaces.KeyExpansion;
import ru.maximuspokez.interfaces.SymmetricCipher;
import ru.maximuspokez.sbox.InverseSBox;
import ru.maximuspokez.sbox.Sbox;

import java.util.Arrays;

public class Rijndael implements SymmetricCipher {
  private static final int WORD_SIZE = 4;

  private final KeyExpansion keyExpansion;
  private byte[][] roundKeys = null;
  private RijndaelConfiguration configuration;

  public Rijndael(RijndaelConfiguration configuration) {
    this.configuration = configuration;
    this.keyExpansion = new RijndaelKeyExpansionImpl(configuration);
  }

  public Rijndael() {
    this(new RijndaelConfiguration(4, 4));
  }

  @Override
  public void setSymmetricKey(byte[] symmetricKey) {
    int keySizeInBytes = configuration.getNk() * WORD_SIZE;
    if (symmetricKey.length != keySizeInBytes) {
      throw new IllegalArgumentException("Invalid key size: expected " + keySizeInBytes + " but got " + symmetricKey.length);
    }
    this.roundKeys = keyExpansion.generateRoundKeys(symmetricKey);
  }

  @Override
  public byte[] encrypt(byte[] message) {
    if (roundKeys == null) {
      throw new IllegalStateException("Round keys are not set. Call setSymmetricKey first!");
    }

    int blockSize = getBlockSize();
    if (message.length != blockSize) {
      throw new IllegalArgumentException("Block length must be " + blockSize + "bytes but got " + message.length);
    }

    byte[] state = Arrays.copyOf(message, message.length);
    addRoundKey(state, roundKeys[0]);

    int Nr = configuration.getNr();
    for (int round = 1; round < Nr; round++) {
      subBytes(state);
      shiftRows(state);
      mixColumns(state);
      addRoundKey(state, roundKeys[round]);
    }

    subBytes(state);
    shiftRows(state);
    addRoundKey(state, roundKeys[Nr]);

    return state;
  }

  @Override
  public byte[] decrypt(byte[] ciphertext) {
    if (roundKeys == null) {
      throw new IllegalStateException("Round keys are not set. Call setSymmetricKey first!");
    }

    int blockSize = getBlockSize();
    if (ciphertext.length != blockSize) {
      throw new IllegalArgumentException("Block length must be " + blockSize + "bytes but got " + ciphertext.length);
    }

    byte[] state = Arrays.copyOf(ciphertext, ciphertext.length);
    addRoundKey(state, roundKeys[configuration.getNr()]);

    int Nr = configuration.getNr();
    for (int round = Nr - 1; round > 0; round--) {
      invShiftRows(state);
      invSubBytes(state);
      addRoundKey(state, roundKeys[round]);
      invMixColumns(state);
    }

    invShiftRows(state);
    invSubBytes(state);
    addRoundKey(state, roundKeys[0]);

    return state;
  }

  private void addRoundKey(byte[] state, byte[] roundKey) {
    for (int i = 0; i < state.length; i++) {
      state[i] ^= roundKey[i];
    }
  }

  private void subBytes(byte[] state) {
    Sbox sBox = configuration.getSBox();
    for (int i = 0; i < state.length; i++) {
      state[i] = sBox.get(state[i]);
    }
  }

  private void shiftRows(byte[] state) {
    byte[] temp = Arrays.copyOf(state, state.length);
    int Nb = configuration.getNb();

    for (int row = 1; row < 4; row++) { // Первая строка не сдвигается
      for (int col = 0; col < Nb; col++) {
        int newPos = (col + row) % Nb;
        state[row + 4 * newPos] = temp[row + 4 * col];
      }
    }
  }

  private void mixColumns(byte[] state) {
    byte[] temp = new byte[state.length];

    int modulus = configuration.getModulus();
    for (int col = 0; col < configuration.getNb(); col++) {
      int offset = col * 4;


      temp[offset]     = (byte) (GaloisFieldService.multiply((byte) 0x02, state[offset], modulus) ^
                                  GaloisFieldService.multiply((byte) 0x03, state[offset + 1], modulus) ^
                                    state[offset + 2] ^
                                      state[offset + 3]);

      temp[offset + 1] = (byte) (state[offset] ^
                                  GaloisFieldService.multiply((byte) 0x02, state[offset + 1], modulus) ^
                                    GaloisFieldService.multiply((byte) 0x03, state[offset + 2], modulus) ^
                                      state[offset + 3]);

      temp[offset + 2] = (byte) (state[offset] ^
                                  state[offset + 1] ^
                                    GaloisFieldService.multiply((byte) 0x02, state[offset + 2], modulus) ^
                                      GaloisFieldService.multiply((byte) 0x03, state[offset + 3], modulus));

      temp[offset + 3] = (byte) (GaloisFieldService.multiply((byte) 0x03, state[offset], modulus) ^
                                  state[offset + 1] ^
                                    state[offset + 2] ^
                                      GaloisFieldService.multiply((byte) 0x02, state[offset + 3], modulus));
    }

    System.arraycopy(temp, 0, state, 0, state.length);
  }

  private void invSubBytes(byte[] state) {
    InverseSBox invSBox = configuration.getInvSBox();
    for (int i = 0; i < state.length; i++) {
      state[i] = invSBox.get(state[i]);
    }
  }

  private void invShiftRows(byte[] state) {
    byte[] temp = Arrays.copyOf(state, state.length);
    int Nb = configuration.getNb();

    for (int row = 1; row < 4; row++) { // Обратный сдвиг
      for (int col = 0; col < Nb; col++) {
        int newPos = (col - row + Nb) % Nb;
        state[row + 4 * newPos] = temp[row + 4 * col];
      }
    }
  }

  private void invMixColumns(byte[] state) {
    byte[] temp = new byte[state.length];
    int modulus = configuration.getModulus();
    for (int col = 0; col < configuration.getNb(); col++) {
      int offset = col * 4;

      temp[offset] = (byte) (GaloisFieldService.multiply((byte) 0x0e, state[offset], modulus) ^
                              GaloisFieldService.multiply((byte) 0x0b, state[offset + 1], modulus) ^
                                GaloisFieldService.multiply((byte) 0x0d, state[offset + 2], modulus) ^
                                  GaloisFieldService.multiply((byte) 0x09, state[offset + 3], modulus));

      temp[offset + 1] = (byte) (GaloisFieldService.multiply((byte) 0x09, state[offset], modulus) ^
                                  GaloisFieldService.multiply((byte) 0x0e, state[offset + 1], modulus) ^
                                    GaloisFieldService.multiply((byte) 0x0b, state[offset + 2], modulus) ^
                                      GaloisFieldService.multiply((byte) 0x0d, state[offset + 3], modulus));

      temp[offset + 2] = (byte) (GaloisFieldService.multiply((byte) 0x0d, state[offset], modulus) ^
                                  GaloisFieldService.multiply((byte) 0x09, state[offset + 1], modulus) ^
                                    GaloisFieldService.multiply((byte) 0x0e, state[offset + 2], modulus) ^
                                      GaloisFieldService.multiply((byte) 0x0b, state[offset + 3], modulus));

      temp[offset + 3] = (byte) (GaloisFieldService.multiply((byte) 0x0b, state[offset], modulus) ^
                                  GaloisFieldService.multiply((byte) 0x0d, state[offset + 1], modulus) ^
                                    GaloisFieldService.multiply((byte) 0x09, state[offset + 2], modulus) ^
                                      GaloisFieldService.multiply((byte) 0x0e, state[offset + 3], modulus));
    }

    System.arraycopy(temp, 0, state, 0, state.length);
  }

  @Override
  public int getBlockSize() {
    return configuration.getNb() * WORD_SIZE;
  }
}
