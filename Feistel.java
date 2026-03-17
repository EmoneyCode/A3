/*****************************************************
 * CS 326 - Spring 2026 - Assignment #2
 * 
 * Student's full name: Gretchen Rost
 * Student's full name: Ethan Trammell
 * Student's full name: Caleb Wisneski
 *****************************************************/

import java.util.Arrays;

class Feistel {
    int w; // the half-width of a block (in bits)
    int n; // the number of rounds
    FeistelFunction F; // the round function
    int[][] K; // the set of sub-keys

    /* do not modify this constructor */
    Feistel(int w, int n, FeistelFunction F, int[][] K) {
        this.w = w;
        this.n = n;
        this.F = F;
        this.K = K;
    }// constructor

    /*
     * given a 2w-bit vector of plaintext, return the 2w-bit encrypted
     * input data block.
     */
    int[] encrypt(int[] block) {
        int[] lb = Arrays.copyOfRange(block, 0, w);
        int[] rb = Arrays.copyOfRange(block, w, 2 * w);
        for (int round = 1; round <= n; round++) {
            int[] temp = rb;
            rb = Utils.XOR(lb, F.round(rb, K[round]));
            lb = temp;
        }
        int[] result = new int[2 * w];
        System.arraycopy(rb, 0, result, 0, w);
        System.arraycopy(lb, 0, result, w, w);
        return result;
    }// encrypt method

    /*
     * given a 2w-bit vector of encrypted ciphertext, return the 2w-bit
     * plaintext block.
     */
    int[] decrypt(int[] block) {
        int[][] reversedKeys = new int[n + 1][];
        for (int i = 1; i <= n; i++) {
            reversedKeys[i] = K[n - i + 1];
        }

        int[][] originalKeys = K;
        K = reversedKeys;
        int[] result = encrypt(block);
        K = originalKeys;

        return result;
    }// decrypt method

    /*
     * I will use this driver code to test your program. Do not modify it.
     */
    public static void main(String[] args) {
        if (args.length != 5) {
            System.out.println("This program should be invoked with the " +
                    "following arguments:");
            System.out.println("  java Feistel <e or d> <w> <#rounds> " +
                    "<allzeros or allones or and> <hex block>");
            System.exit(1);
        }

        boolean encrypt = args[0].equals("e");
        int numBits = Integer.parseInt(args[1]);
        int numRounds = Integer.parseInt(args[2]);
        String roundFn = args[3];
        int[] block = Utils.binStringToIntArray(
                Utils.hexToBinString(args[4], 4 * args[4].length()));
        int[][] subkeys = new int[1 + numRounds][numBits];
        Feistel cipher = null;
        ;
        if (roundFn.equals("allzeros")) {
            cipher = new Feistel(numBits, numRounds,
                    new FeistelAllZeros(), subkeys);
        } else if (roundFn.equals("allones")) {
            cipher = new Feistel(numBits, numRounds,
                    new FeistelAllOnes(), subkeys);
        } else if (roundFn.equals("and")) {
            for (int round = 1; round <= numRounds; round += 2) {
                for (int bit = 0; bit < numBits; bit++) {
                    subkeys[round][bit] = 0;
                    if (round < numRounds)
                        subkeys[round + 1][bit] = 1;
                }
            }
            cipher = new Feistel(numBits, numRounds, new FeistelAnd(), subkeys);
        }

        if (encrypt) {
            System.out.println(
                    Utils.binStringToHex(
                            Utils.intArrayToBinString(cipher.encrypt(block))));
        } else {
            System.out.println(
                    Utils.binStringToHex(
                            Utils.intArrayToBinString(cipher.decrypt(block))));
        }
    }// main method

}// Feistel class
