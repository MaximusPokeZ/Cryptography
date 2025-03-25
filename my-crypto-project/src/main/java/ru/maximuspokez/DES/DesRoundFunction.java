package ru.maximuspokez.DES;

import ru.maximuspokez.constants.DesConstants;
import ru.maximuspokez.crypto.PermuteBits;
import ru.maximuspokez.interfaces.EncryptionTransformation;

public class DesRoundFunction implements EncryptionTransformation {
  @Override
  public byte[] transform(byte[] inputBlock, byte[] roundKey) {
    byte[] afterExpansion = PermuteBits.permute(inputBlock, DesConstants.E, false, false); // 32 --> 48

    if(afterExpansion.length != roundKey.length) {
      throw new IllegalArgumentException("The size of the extended block and the round key must equal (48 bits)");
    }

    byte[] xored = new byte[afterExpansion.length];
    for (int i = 0; i < afterExpansion.length; i++) {
      xored[i] = (byte) (afterExpansion[i] ^ roundKey[i]);
    }

    byte[] sBlocks = applySBlocks(xored); // 48 --> 32

    return PermuteBits.permute(sBlocks, DesConstants.P, false, false);
  }

  private byte[] applySBlocks(byte[] xored) {
    if (xored.length != 6) {
      throw new IllegalArgumentException("The size of the extended block and the round key must be 6 bits");
    }

    // формируем 8 групп по 6 бит
    long in48bit = 0;
    for (int i = 0; i < 6; i++) {
      in48bit = (in48bit << 8) | (xored[i] & 0xFF);
    }

    long out32bit = 0;
    for (int i = 0; i < 8; i++) {
      int shits = (7 - i) * 6;
      long bits6 = (in48bit >> shits) & 0x3F;

      long row = ((bits6 & 0x20) >> 4) | (bits6 & 0x01);
      long column = ((bits6 >> 1) & 0x0F);

      out32bit = (out32bit << 4) | (DesConstants.S[i][(int) row][(int) column] & 0x0F);
    }

    byte[] result = new byte[4]; // 32
    result[0] = (byte)((out32bit >> 24) & 0xFF);
    result[1] = (byte)((out32bit >> 16) & 0xFF);
    result[2] = (byte)((out32bit >> 8) & 0xFF);
    result[3] = (byte)(out32bit & 0xFF);

    return result;
  }

  @Override
  public int getInputBlockSize() {
    return 4;
  }

  @Override
  public int getRoundKeySize() {
    return 6;
  }

  @Override
  public int getOutputBlockSize() {
    return 4;
  }

  @Override
  public String getTransformationName() {
    return "DES Round Function";
  }
}
