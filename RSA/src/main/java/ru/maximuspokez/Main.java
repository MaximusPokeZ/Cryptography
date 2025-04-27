package ru.maximuspokez;

import ru.maximuspokez.rsa.RsaKeyPair;
import ru.maximuspokez.rsa.RsaService;

import java.math.BigInteger;

public class Main {
  public static void main(String[] args) {

    RsaService service = new RsaService(RsaService.PrimaryTestType.MILLER_RABIN, 0.9999, 512);
    RsaKeyPair keyPair = service.getKeyPair();

    System.out.println("Public key (e, n):");
    System.out.println("e = " + keyPair.publicKey().e());
    System.out.println("n = " + keyPair.publicKey().n());

    System.out.println("Private key (d):");
    System.out.println("d = " + keyPair.privateKey().d());

    BigInteger message = new BigInteger("123452312312313123123131236789");
    BigInteger ciphertext = service.encrypt(message);
    BigInteger decrypted = service.decrypt(ciphertext);

    System.out.println("Original message: " + message);
    System.out.println("Encrypted: " + ciphertext);
    System.out.println("Decrypted: " + decrypted);


  }
}