package ru.maximuspokez.crypto;

public class PermuteBits {

  public static byte[] permute(byte[] input, int[] pBlock, boolean LSBorMSB, boolean startFromZeroIndx) {
    int totalBits = input.length * 8;

    int outputSizeBytes = (int) Math.ceil(pBlock.length / 8.0);
    byte[] output = new byte[outputSizeBytes];

    int pBlockLength = pBlock.length;

    for (int i = 0; i < pBlockLength; ++i) {
      int currBitPos = pBlock[i] - (startFromZeroIndx ? 1 : 0);

      if (currBitPos < 0 || currBitPos >= totalBits) {
        throw new IllegalArgumentException("Illegal bit position: " + currBitPos + " is out of range [" + (startFromZeroIndx ? 0 : 1) + ", " + (totalBits - (startFromZeroIndx ? 1 : 0)) + "]");
      }

      int currByteIndx = currBitPos / 8;
      int currBitIndx = (!LSBorMSB ? currBitPos % 8 : 7 - (currBitPos % 8));

      boolean bitValue = ((input[currByteIndx] >> currBitIndx) & 1) == 1;

      int dstByteIndx = i / 8;
      int dstBitIndx = (!LSBorMSB ? i % 8 : 7 - (i % 8));

      output[dstByteIndx] |= (byte) ((bitValue ? 1 : 0) << dstBitIndx);

    }

    return output;
  }
}
