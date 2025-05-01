package ru.maximuspokez;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.maximuspokez.ciphers.DEAL.DEAL;
import ru.maximuspokez.ciphers.DES.DES;
import ru.maximuspokez.constants.CipherMode;
import ru.maximuspokez.constants.DealKeySize;
import ru.maximuspokez.constants.PaddingMode;
import ru.maximuspokez.context.SymmetricCipherContext;
import ru.maximuspokez.galois.GaloisFieldService;
import ru.maximuspokez.interfaces.SymmetricCipher;
import ru.maximuspokez.utils.HexUtil;
import ru.maximuspokez.utils.PolynomialUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;


public class Main {
  private static final Logger log = LoggerFactory.getLogger(Main.class);

  public static void main(String[] args) throws IOException {

//    byte[] key = {(byte)0x00, (byte)0x00, (byte)0x00, (byte)0xC4, (byte)0xC8, (byte)0xC0, (byte)0xCD, (byte)0xC0};
//    byte[] iv = {(byte) 0x01, (byte) 0x23, (byte) 0x45, (byte) 0x67, (byte) 0x89, (byte) 0xAB, (byte) 0xCD, (byte) 0xEF};
//
//    InputStream path = Main.class.getClassLoader().getResourceAsStream("Tux.jpg");
//
//    byte[] message = path.readAllBytes();
//
//
//    byte[] KEY = { (byte)0xD4, (byte)0xDE, (byte)0x6A, (byte)0xA8, (byte)0x94, (byte)0x55, (byte)0xD3, (byte)0x89,
//            (byte)0x03, (byte)0xF6, (byte)0xD7, (byte)0x8C, (byte)0x45, (byte)0xC4, (byte)0xC5, (byte)0xD8 };
//
//    byte[] MESS = { (byte)0xC7, (byte)0x22, (byte)0x7F, (byte)0x94, (byte)0xBB, (byte)0xA7, (byte)0xCB, (byte)0xA6,
//            (byte)0xD6, (byte)0x4C, (byte)0x55, (byte)0xAC, (byte)0x06, (byte)0xB2, (byte)0x8D, (byte)0x6D, (byte)0xB2 };
//
//    SymmetricCipher des = new DES();
//    des.setSymmetricKey(key);
//    DEAL deal = new DEAL(DealKeySize.KEY_128, key);
//    deal.setSymmetricKey(KEY);
//
//
//    SymmetricCipherContext context = new SymmetricCipherContext(deal, CipherMode.RANDOM_DELTA, PaddingMode.ISO_10126, null, (Object) null, 10);
//
//    byte[] ciphertext = context.encrypt(message);
//    byte[] decrypted = context.decrypt(ciphertext);
//
////    System.out.println("Start message: " + HexUtil.bytesToHex(MESS));
//    System.out.println("MEss: " + HexUtil.bytesToHex(message));
//    System.out.println("\n\n\nDecrypted: " + HexUtil.bytesToHex(decrypted));
//    System.out.println("\n\n\nRESULT: " + (message.length == decrypted.length));
//
//    String outputDir = System.getProperty("user.home") + "/Downloads/okkk.jpg";
//    Files.write(Paths.get(outputDir), decrypted);

    System.out.println(GaloisFieldService.factor((byte) 0x15));


  }

}