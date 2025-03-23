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


    byte[] sBlocks = applySBlocks(xored);

    // 4. Перестановка: применяем P-блок для получения финального результата.
    byte[] outputBlock = PermuteBits.permute(sBlocks, DesConstants.P, false, false); // 48 --> 32

    return outputBlock;
  }

  private byte[] applySBlocks(byte[] xored) {
    
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
