package ru.maximuspokez.sbox.Rijndael;

import ru.maximuspokez.galois.GaloisFieldService;
import ru.maximuspokez.sbox.SubstitutionBox;

public class SBoxRijndael implements SubstitutionBox {

  private final byte[] table;

  public SBoxRijndael(int modulus) {
    this.table = new byte[256];
    generate(modulus);
  }

  private void generate(int modulus) {
    for (int x = 0; x < 256; x++) {
      byte inv;
      if (x == 0) {
        inv = 0;
      } else {
        inv = GaloisFieldService.inverse((byte) x, modulus);
      }
      table[x] = affineTransform(inv);
    }
  }

  private byte affineTransform(byte b) {
    int x = Byte.toUnsignedInt(b);
    int result = 0;

    for (int i = 0; i < 8; i++) {
      int bit = (
              ((x >> i) & 1) ^
              ((x >> ((i + 4) % 8)) & 1) ^
              ((x >> ((i + 5) % 8)) & 1) ^
              ((x >> ((i + 6) % 8)) & 1) ^
              ((x >> ((i + 7) % 8)) & 1) ^
              ((0x63 >> i) & 1)
      );
      result |= (bit << i);
    }
    return (byte) result;
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
