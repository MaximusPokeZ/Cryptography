package ru.maximuspokez;


import ru.maximuspokez.DES.DES;
import ru.maximuspokez.DES.DesRoundFunction;
import ru.maximuspokez.DES.KeyExpansionImpl;
import ru.maximuspokez.constants.CipherMode;
import ru.maximuspokez.constants.PaddingMode;
import ru.maximuspokez.context.SymmetricCipherContext;
import ru.maximuspokez.interfaces.EncryptionTransformation;
import ru.maximuspokez.interfaces.KeyExpansion;
import ru.maximuspokez.interfaces.SymmetricCipher;

import java.util.Arrays;

public class Main {
  public static void main(String[] args) {

    byte[] key = {(byte)0x00, (byte)0x00, (byte)0x00, (byte)0xC4, (byte)0xC8, (byte)0xC0, (byte)0xCD, (byte)0xC0};
    byte[] message = "Hello, DES!!!123".getBytes();

    KeyExpansion keyExpansion = new KeyExpansionImpl();
    EncryptionTransformation roundFunction = new DesRoundFunction();
    SymmetricCipher des = new DES(keyExpansion, roundFunction);
    des.setSymmetricKey(key);

    SymmetricCipherContext context = new SymmetricCipherContext(des, CipherMode.ECB, PaddingMode.ZEROS, null);

    byte[] ciphertext = context.encrypt(message);
    byte[] decrypted = context.decrypt(ciphertext);

    System.out.println("Plaintext:  " + Arrays.toString(message));
    System.out.println("Ciphertext: " + Arrays.toString(ciphertext));
    System.out.println("Decrypted:  " + Arrays.toString(decrypted));
  }

}