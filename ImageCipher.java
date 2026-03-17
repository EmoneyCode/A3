
/*****************************************************
   CS 326 - Spring 2026 - Assignment #3

   Student's full name: _____
   Student's full name: _____
   Student's full name: _____

*****************************************************/

import java.util.*;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import java.io.*;
import java.math.*;

class ImageCipher {

    // storage for the next plaintext block read from/written to the image file
    static int[] block = new int[64];

    // storage for the next ciphertext block read from/written to the image file
    static int[] cipherBlock = new int[64];

    // file extension for all image files in this assignment (you MUST use
    // this constant everywhere instead of the string itself)
    static String EXT = ".pgm";

    /*
     * Given a scanner object, read the next 8 integers from it and store
     * the 8 corresponding 8-bit patterns into the 64-bit instance variable
     * called 'block'
     */
    static void readBlock(Scanner s) throws Exception {
        for (int i = 0; i < 8; i++) {
            int pixelVal = s.nextInt(); // read one pixel value (0–255)

            // store the 8 bits of this byte into block[i*8 .. i*8+7]
            // MSB first
            for (int bit = 7; bit >= 0; bit--) {
                block[i * 8 + (7 - bit)] = (pixelVal >> bit) & 1;
            }
        }
    }// readBlock method

    /*
     * Given a PrintWriter object, write to it the 8 integers stored as the 8
     * corresponding 8-bit patterns in the 64-bit instance variable
     * called 'block'
     */
    static void writeBlock(PrintWriter w) throws Exception {
        for (int i = 0; i < 8; i++) {
            // reconstruct the byte from 8 bits (MSB first)
            int pixelVal = 0;
            for (int bit = 7; bit >= 0; bit--) {
                pixelVal |= (block[i * 8 + (7 - bit)] << bit);
            }

            // treat as unsigned: Java bytes are signed, but pixel values
            // must be 0–255, so mask to ensure no negatives
            pixelVal = pixelVal & 0xFF;

            w.print(pixelVal + " ");
        }
    }// writeBlock method

    /*
     * Given a Scanner object and a PrintWriter object, copy to the latter
     * the first four lines of the former.
     */
    static void processHeader(Scanner s, PrintWriter w) throws Exception {
        // useDelimiter("\n") so we can read full lines
        s.useDelimiter("\n");
        for (int i = 0; i < 4; i++) {
            String line = s.next();
            w.println(line);
        }
        // reset delimiter to default whitespace for pixel-by-pixel reading
        s.useDelimiter("\\s+");
    }// processHeader method

    /*
     * ---------------------------------------------------------------
     * Helper: convert a hex string (16 hex chars = 8 bytes) to a byte[].
     */
    static byte[] hexToBytes(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }

    /*
     * ---------------------------------------------------------------
     * Helper: convert a 64-element int[] of bits (MSB-first per byte)
     * into an 8-byte array.
     */
    static byte[] bitsToBytes(int[] bits) {
        byte[] result = new byte[8];
        for (int i = 0; i < 8; i++) {
            int val = 0;
            for (int bit = 7; bit >= 0; bit--) {
                val |= (bits[i * 8 + (7 - bit)] << bit);
            }
            result[i] = (byte) val;
        }
        return result;
    }

    /*
     * ---------------------------------------------------------------
     * Helper: fill a 64-element int[] of bits (MSB-first per byte)
     * from an 8-byte array. Fills either 'block' or 'cipherBlock'.
     */
    static void bytesToBits(byte[] bytes, int[] dest) {
        for (int i = 0; i < 8; i++) {
            int val = bytes[i] & 0xFF; // treat as unsigned
            for (int bit = 7; bit >= 0; bit--) {
                dest[i * 8 + (7 - bit)] = (val >> bit) & 1;
            }
        }
    }

    /*
     * ---------------------------------------------------------------
     * Helper: XOR two 64-bit (bit-array) blocks, result stored in dest.
     */
    static void xorBlocks(int[] a, int[] b, int[] dest) {
        for (int i = 0; i < 64; i++) {
            dest[i] = a[i] ^ b[i];
        }
    }

    /*
     * ---------------------------------------------------------------
     * Helper: build a DES Cipher in ECB/NoPadding mode from a hex key.
     */
    static Cipher buildCipher(String keyHex, int mode) throws Exception {
        byte[] keyBytes = hexToBytes(keyHex);
        DESKeySpec dks = new DESKeySpec(keyBytes);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("DES");
        SecretKey desKey = skf.generateSecret(dks);
        // Use ECB/NoPadding so we control the chaining ourselves
        Cipher cipher = Cipher.getInstance("DES/ECB/NoPadding");
        cipher.init(mode, desKey);
        return cipher;
    }

    /*
     * given a file name (with no extension) for a PGM image and a DES
     * key (in hex format), encrypt the image using DES in ECB mode and store
     * the result in a file whose name is obtained by adding to the input
     * file name the string "EncryptedECB" + EXT.
     */
    static void encryptECB(String filename, String key) {
        try {
            Scanner s = new Scanner(new File(filename + EXT));
            PrintWriter w = new PrintWriter(new FileWriter(filename + "EncryptedECB" + EXT));

            processHeader(s, w);

            Cipher cipher = buildCipher(key, Cipher.ENCRYPT_MODE);

            while (s.hasNextInt()) {
                readBlock(s); // fills 'block'
                byte[] plainBytes = bitsToBytes(block);
                byte[] cipherBytes = cipher.doFinal(plainBytes);
                bytesToBits(cipherBytes, block); // reuse 'block' for output
                writeBlock(w);
            }

            s.close();
            w.close();
        } catch (Exception e) {
            System.out.println("encryptECB error: " + e.getMessage());
            e.printStackTrace();
        }
    }// encryptECB method

    /*
     * given a file name (with no extension) for a PGM image and a DES
     * key (in hex format), decrypt the image using DES in ECB mode and store
     * the result in a file whose name is obtained by adding to the input
     * file name the string "DecryptedECB" + EXT.
     */
    static void decryptECB(String filename, String key) {
        try {
            Scanner s = new Scanner(new File(filename + EXT));
            PrintWriter w = new PrintWriter(new FileWriter(filename + "DecryptedECB" + EXT));

            processHeader(s, w);

            Cipher cipher = buildCipher(key, Cipher.DECRYPT_MODE);

            while (s.hasNextInt()) {
                readBlock(s);
                byte[] cipherBytes = bitsToBytes(block);
                byte[] plainBytes = cipher.doFinal(cipherBytes);
                bytesToBits(plainBytes, block);
                writeBlock(w);
            }

            s.close();
            w.close();
        } catch (Exception e) {
            System.out.println("decryptECB error: " + e.getMessage());
            e.printStackTrace();
        }
    }// decryptECB method

    /*
     * given a file name (with no extension) for a PGM image, a DES
     * key and an initialization vector (both in hex format), encrypt
     * the image using DES in CBC mode and store the result in a file
     * whose name is obtained by adding to the input file name the
     * string "EncryptedCBC" + EXT.
     */
    static void encryptCBC(String filename, String key, String IV) {
        try {
            Scanner s = new Scanner(new File(filename + EXT));
            PrintWriter w = new PrintWriter(new FileWriter(filename + "EncryptedCBC" + EXT));

            processHeader(s, w);

            Cipher cipher = buildCipher(key, Cipher.ENCRYPT_MODE);

            // previousBlock starts as the IV (as a bit array)
            int[] previousBlock = new int[64];
            bytesToBits(hexToBytes(IV), previousBlock);

            int[] xoredBlock = new int[64];

            while (s.hasNextInt()) {
                readBlock(s); // fills 'block' with plaintext

                // CBC encrypt: ciphertext[i] = DES_encrypt(plaintext[i] XOR ciphertext[i-1])
                xorBlocks(block, previousBlock, xoredBlock);

                byte[] xoredBytes = bitsToBytes(xoredBlock);
                byte[] cipherBytes = cipher.doFinal(xoredBytes);

                bytesToBits(cipherBytes, block); // 'block' now holds ciphertext
                writeBlock(w);

                // the ciphertext block becomes the "previous" for next iteration
                System.arraycopy(block, 0, previousBlock, 0, 64);
            }

            s.close();
            w.close();
        } catch (Exception e) {
            System.out.println("encryptCBC error: " + e.getMessage());
            e.printStackTrace();
        }
    }// encryptCBC method

    /*
     * given a file name (with no extension) for a PGM image, a DES
     * key and an initialization vector (both in hex format), decrypt
     * the image using DES in CBC mode and store the result in a file
     * whose name is obtained by adding to the input file name the
     * string "DecryptedCBC" + EXT.
     */
    static void decryptCBC(String filename, String key, String IV) {
        try {
            Scanner s = new Scanner(new File(filename + EXT));
            PrintWriter w = new PrintWriter(new FileWriter(filename + "DecryptedCBC" + EXT));

            processHeader(s, w);

            Cipher cipher = buildCipher(key, Cipher.DECRYPT_MODE);

            // previousBlock starts as the IV (as a bit array)
            int[] previousBlock = new int[64];
            bytesToBits(hexToBytes(IV), previousBlock);

            int[] decryptedBlock = new int[64];

            while (s.hasNextInt()) {
                readBlock(s); // fills 'block' with ciphertext

                // save ciphertext BEFORE overwriting block
                int[] savedCipher = Arrays.copyOf(block, 64);

                byte[] cipherBytes = bitsToBytes(block);
                byte[] decryptedBytes = cipher.doFinal(cipherBytes);

                // CBC decrypt: plaintext[i] = DES_decrypt(ciphertext[i]) XOR ciphertext[i-1]
                bytesToBits(decryptedBytes, decryptedBlock);
                xorBlocks(decryptedBlock, previousBlock, block); // result into 'block'

                writeBlock(w);

                // the saved ciphertext becomes the "previous" for next iteration
                System.arraycopy(savedCipher, 0, previousBlock, 0, 64);
            }

            s.close();
            w.close();
        } catch (Exception e) {
            System.out.println("decryptCBC error: " + e.getMessage());
            e.printStackTrace();
        }
    }// decryptCBC method

    /* This is the driver code used for testing purposes. Do NOT modify it. */
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("The first argument must be -e or -d, while ");
            System.out.println("the second argument must be -ECB or -CBC");
            System.exit(1);
        }
        if (args[1].equals("-ECB")) {
            if (args.length == 4) {
                String filename = args[2];
                String key = args[3];
                if (args[0].equals("-e")) {
                    encryptECB(filename, key);
                } else if (args[0].equals("-d")) {
                    decryptECB(filename, key);
                } else {
                    System.out.println("The first argument must be -e or -d");
                    System.exit(1);
                }
            } else {
                System.out.println("Usage: java ImageCipher [-e or -d] -ECB " +
                        "<image file name without .pgm> <key>");
                System.exit(1);
            }
        } else if (args[1].equals("-CBC")) {
            if (args.length == 5) {
                String filename = args[2];
                String key = args[3];
                String IV = args[4];
                if (args[0].equals("-e")) {
                    encryptCBC(filename, key, IV);
                } else if (args[0].equals("-d")) {
                    decryptCBC(filename, key, IV);
                } else {
                    System.out.println("The first argument must be -e or -d");
                    System.exit(1);
                }
            } else {
                System.out.println("Usage: java ImageCipher [-e or -d] -ECB " +
                        "<image file name without .pgm> <key> <IV>");
                System.exit(1);
            }
        } else {
            System.out.println("The second argument must be -ECB or -CBC");
            System.exit(1);
        }
    }// main method
}// ImageCipher class
