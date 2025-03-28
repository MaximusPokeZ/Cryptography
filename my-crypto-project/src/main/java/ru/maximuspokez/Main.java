package ru.maximuspokez;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.maximuspokez.DES.DES;
import ru.maximuspokez.DES.DesRoundFunction;
import ru.maximuspokez.DES.KeyExpansionImpl;
import ru.maximuspokez.constants.CipherMode;
import ru.maximuspokez.constants.PaddingMode;
import ru.maximuspokez.context.SymmetricCipherContext;
import ru.maximuspokez.interfaces.EncryptionTransformation;
import ru.maximuspokez.interfaces.KeyExpansion;
import ru.maximuspokez.interfaces.SymmetricCipher;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;


public class Main {
  private static final Logger log = LoggerFactory.getLogger(Main.class);

  public static void main(String[] args) throws IOException {

    byte[] key = {(byte)0x00, (byte)0x00, (byte)0x00, (byte)0xC4, (byte)0xC8, (byte)0xC0, (byte)0xCD, (byte)0xC0};
    byte[] iv = {(byte) 0x01, (byte) 0x23, (byte) 0x45, (byte) 0x67, (byte) 0x89, (byte) 0xAB, (byte) 0xCD, (byte) 0xEF};

    InputStream path = Main.class.getClassLoader().getResourceAsStream("Tux.jpg");

    byte[] message = path.readAllBytes();

    KeyExpansion keyExpansion = new KeyExpansionImpl();
    EncryptionTransformation roundFunction = new DesRoundFunction();
    SymmetricCipher des = new DES(keyExpansion, roundFunction);
    des.setSymmetricKey(key);

    SymmetricCipherContext context = new SymmetricCipherContext(des, CipherMode.RANDOM_DELTA, PaddingMode.ANSI_X923, null, (Object) null, 5);

    byte[] ciphertext = context.encrypt(message);
    byte[] decrypted = context.decrypt(ciphertext);

    String outputDir = System.getProperty("user.home") + "/Downloads/okkk.jpg";
    Files.write(Paths.get(outputDir), decrypted);
  }

}