package ru.maximuspokez.sbox.Rijndael;

import ru.maximuspokez.sbox.SubstitutionBox;

public class InverseSBoxRijndael implements SubstitutionBox {

  private final byte[] table;

  public InverseSBoxRijndael(SBoxRijndael sBox) {
    this.table = new byte[256];
    generateFrom(sBox);
  }

  private void generateFrom(SBoxRijndael sBox) {
    for (int i = 0; i < 256; i++) {
      byte value = (byte) i;
      byte mapped = sBox.get(value);

      int index = Byte.toUnsignedInt(mapped);
      table[index] = value;
    }
  }
  @Override
  public byte get(byte value) {
    return table[Byte.toUnsignedInt(value)];
  }

  @Override
  public byte[] getTable() {
    return table.clone();
  }
}
