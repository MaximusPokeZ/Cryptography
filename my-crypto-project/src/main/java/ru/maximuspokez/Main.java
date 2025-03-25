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

    byte[] key = {(byte)0x13, (byte)0x34, (byte)0x57, (byte)0x79, (byte)0x9B, (byte)0xBC, (byte)0xDF, (byte)0xF1};
    byte[] message = {(byte)0x01, (byte)0x23, (byte)0x45, (byte)0x67, (byte)0x89, (byte)0xAB, (byte)0xCD, (byte)0xEF};

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