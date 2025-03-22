package ru.maximuspokez;

import ru.maximuspokez.crypto.PermuteBits;

import java.util.Arrays;

public class Main {
  public static void main(String[] args) {


    PermuteBits permuteBits = new PermuteBits();

    byte[] b = {(byte)0x11};
    int[] pBlock = {1, 3, 5, 7, 2, 4, 6, 8};
    byte[] r = permuteBits.permute(b, pBlock, true, true);

    System.out.println(Arrays.toString(r));

  }
}