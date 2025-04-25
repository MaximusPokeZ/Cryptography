package ru.maximuspokez.rsa;

import java.math.BigInteger;

public record PrivateKey(BigInteger n, BigInteger d) {

}
