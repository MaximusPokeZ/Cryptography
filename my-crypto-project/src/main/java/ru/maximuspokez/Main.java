package ru.maximuspokez;


import ru.maximuspokez.DES.DES;
import ru.maximuspokez.DES.DesRoundFunction;
import ru.maximuspokez.DES.KeyExpansionImpl;
import ru.maximuspokez.interfaces.EncryptionTransformation;
import ru.maximuspokez.interfaces.KeyExpansion;
import ru.maximuspokez.interfaces.SymmetricCipher;

import java.util.Arrays;

public class Main {
  public static void main(String[] args) {

    byte[] key = {(byte)0x00, (byte)0x00, (byte)0x00, (byte)0xC4, (byte)0xC8, (byte)0xC0, (byte)0xCD, (byte)0xC0};
    byte[] message = {(byte)0x00, (byte)0x00, (byte)0xC4, (byte)0xC2, (byte)0xCE, (byte)0xD0, (byte)0xDF, (byte)0xCA};

    KeyExpansion keyExpansion = new KeyExpansionImpl();
    EncryptionTransformation roundFunction = new DesRoundFunction();
    SymmetricCipher des = new DES(keyExpansion, roundFunction);

    des.setSymmetricKey(key);

    byte[] ciphertext = des.encrypt(message);

    byte[] decrypted = des.decrypt(ciphertext);

    System.out.println("Plaintext:  " + Arrays.toString(message));
    System.out.println("Ciphertext: " + Arrays.toString(ciphertext));
    System.out.println("Decrypted:  " + Arrays.toString(decrypted));
  }

}