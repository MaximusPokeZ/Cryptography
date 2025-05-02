package ru.maximuspokez.sbox;

public class InverseSBox implements SubstitutionBox{

  private final byte[] table;

  public InverseSBox(Sbox sBox) {
    this.table = new byte[256];
    generateFrom(sBox);
  }

  private void generateFrom(Sbox sBox) {
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
