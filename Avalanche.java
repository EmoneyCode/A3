
/*****************************************************
   CS 326 - Spring 2026 - Assignment #3

   Student's full name: _____
   Student's full name: _____
   Student's full name: _____

 *****************************************************/

import java.util.*;

class Avalanche extends Feistel {
    /* do NOT modify this constructor */
    Avalanche(int[][] K) {
        super(32, 16, new DESround(), K);
    }// constructor

    /*
     * Takes in two plaintext blocks and encrypts them with DES
     * while producing (in the console window) the detailed output
     * described in the handout for this assignment.
     */
    void testEffect(int[] block, int[] block2) {
        block = Utils.applyPermut(DES.IP, block);
        block2 = Utils.applyPermut(DES.IP, block2);

        int[] lb1 = Arrays.copyOfRange(block, 0, w);
        int[] rb1 = Arrays.copyOfRange(block, w, 2 * w);
        int[] lb2 = Arrays.copyOfRange(block2, 0, w);
        int[] rb2 = Arrays.copyOfRange(block2, w, 2 * w);
        for (int round = 1; round <= n; round++) {
            int[] temp1 = rb1;
            int[] temp2 = rb2;
            rb1 = Utils.XOR(lb1, F.round(rb1, K[round]));
            rb2 = Utils.XOR(lb2, F.round(rb2, K[round]));
            lb1 = temp1;
            lb2 = temp2;

            System.out.printf("Round %02d ", round);
            System.out.print(Utils.intArrayToBinString(lb1) + " ");
            System.out.println(Utils.intArrayToBinString(rb1));

            System.out.print("         ");
            System.out.print(Utils.intArrayToBinString(lb2) + " ");
            System.out.println(Utils.intArrayToBinString(rb2));

            System.out.print("         ");
            int diff = 0;
            for (int i = 0; i < lb1.length; i++) {
                if (lb1[i] != lb2[i]) {
                    System.out.print("*");
                    diff += 1;
                } else {
                    System.out.print(" ");
                }
            }

            System.out.print(" ");

            for (int i = 0; i < rb1.length; i++) {
                if (rb1[i] != rb2[i]) {
                    System.out.print("*");
                    diff += 1;
                } else {
                    System.out.print(" ");
                }
            }
            System.out.println(" " + diff);

            System.out.print("         ");
            printHexDigits(lb1);
            System.out.print(" ");
            printHexDigits(rb1);
            System.out.println("");

            System.out.print("         ");
            printHexDigits(lb2);
            System.out.print(" ");
            printHexDigits(rb2);
            System.out.println("");
        }
        int[] result1 = new int[2 * w];
        System.arraycopy(rb1, 0, result1, 0, w);
        System.arraycopy(lb1, 0, result1, w, w);
        Utils.applyPermut(DES.IP, result1);

        int[] result2 = new int[2 * w];
        System.arraycopy(rb2, 0, result2, 0, w);
        System.arraycopy(lb2, 0, result2, w, w);
        Utils.applyPermut(DES.IP, result2);

        lb1 = Arrays.copyOfRange(result1, 0, w);
        rb1 = Arrays.copyOfRange(result1, w, 2 * w);
        lb2 = Arrays.copyOfRange(result2, 0, w);
        rb2 = Arrays.copyOfRange(result2, w, 2 * w);

        System.out.print("IPinv    ");

        System.out.print(Utils.intArrayToBinString(lb1) + " ");
        System.out.println(Utils.intArrayToBinString(rb1));

        System.out.print("         ");
        System.out.print(Utils.intArrayToBinString(lb2) + " ");
        System.out.println(Utils.intArrayToBinString(rb2));

        System.out.print("         ");
        int diff = 0;
        for (int i = 0; i < lb1.length; i++) {
            if (lb1[i] != lb2[i]) {
                System.out.print("*");
                diff += 1;
            } else {
                System.out.print(" ");
            }
        }

        System.out.print(" ");

        for (int i = 0; i < rb1.length; i++) {
            if (rb1[i] != rb2[i]) {
                System.out.print("*");
                diff += 1;
            } else {
                System.out.print(" ");
            }
        }
        System.out.println(" " + diff);

        System.out.print("         ");
        printHexDigits(lb1);
        System.out.print(" ");
        printHexDigits(rb1);
        System.out.println("");

        System.out.print("         ");
        printHexDigits(lb2);
        System.out.print(" ");
        printHexDigits(rb2);
        System.out.println("");

    }// testEffect method

    static void printHexDigits(int[] bits) {
        String hex = Utils.getHex(bits);
        for (int i = 0; i < hex.length(); i++)
            System.out.print("   " + hex.charAt(i));
    }

    /* This method will be used for testing purposes. Do NOT modify. */
    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Usage:  java Avalanche <key> <P1> <P2>");
            System.exit(1);
        }

        String key = args[0];
        String plaintext1 = args[1];
        String plaintext2 = args[2];
        Avalanche av = new Avalanche(DES.getSubKeys(key));
        av.testEffect(Utils.getBitVectorFromHex(plaintext1),
                Utils.getBitVectorFromHex(plaintext2));
    }// main method

}// class Avalanche
