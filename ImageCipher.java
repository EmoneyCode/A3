
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
    static void readBlock(Scanner s) throws Exception
    {
        for (int i = 0; i < 8; i++)
        {
            int pixelVal = s.nextInt();          // one pixel: 0-255
 
            // unpack 8 bits, MSB first
            for (int b = 7; b >= 0; b--)
            {
                block[i * 8 + (7 - b)] = (pixelVal >> b) & 1;
            }
        }
    }// readBlock method
 
    /* Given a PrintWriter object, write to it the 8 integers stored as the 8
       corresponding 8-bit patterns in the 64-bit instance variable
       called 'block'.
 
       We reassemble each 8-bit group back into an integer and print it
       as a decimal followed by a space (the PGM format is whitespace-
       separated, so a trailing space on each block is fine).
     */
    static void writeBlock(PrintWriter w) throws Exception
    {
        for (int i = 0; i < 8; i++)
        {
            int pixelVal = 0;
 
            // pack 8 bits back into an integer (MSB first)
            for (int b = 7; b >= 0; b--)
            {
                pixelVal |= (block[i * 8 + (7 - b)] << b);
            }
 
            // mask to unsigned byte: handles any sign issues after DES
            pixelVal = pixelVal & 0xFF;
 
            w.print(pixelVal + " ");
        }
    }// writeBlock method
 
    /* Given a Scanner object and a PrintWriter object, copy to the latter
       the first four lines of the former.
 
       We temporarily switch the Scanner to line-delimited mode so that
       next() grabs whole lines, then reset to the default whitespace
       delimiter for pixel-by-pixel reading afterwards.
     */
    static void processHeader(Scanner s, PrintWriter w) throws Exception
    {
        s.useDelimiter("\n");
        for (int i = 0; i < 4; i++)
        {
            String line = s.next();
            w.println(line);
        }
        // back to whitespace splitting for integer pixel values
        s.useDelimiter("\\s+");
    }// processHeader method
 
    // ---------------------------------------------------------------
    // Private helpers
    // ---------------------------------------------------------------
 
    /* Build a DES object from a 16-hex-digit key string. */
    private static DES buildDES(String keyHex)
    {
        return new DES(DES.getSubKeys(keyHex));
    }
 
    /* Convert a 16-hex-digit IV string into a 64-element int[] bit vector.
       Delegates to the course-provided Utils.getBitVectorFromHex(). */
    private static int[] hexToBits(String hex)
    {
        return Utils.getBitVectorFromHex(hex);
    }
 
    /* XOR two 64-bit arrays, returning a fresh array.
       Delegates to the course-provided Utils.XOR(). */
    private static int[] xor64(int[] a, int[] b)
    {
        return Utils.XOR(a, b);
    }
 
    /* Copy src into dst (both length 64). */
    private static void copy64(int[] src, int[] dst)
    {
        System.arraycopy(src, 0, dst, 0, 64);
    }
 
    /* Deep-copy a 64-element array into a new array. */
    private static int[] clone64(int[] src)
    {
        return Arrays.copyOf(src, 64);
    }
 
    // ---------------------------------------------------------------
    // ECB
    // ---------------------------------------------------------------
 
    /* given a file name (with no extension) for a PGM image and a DES
       key (in hex format), encrypt the image using DES in ECB mode and store
       the result in a file whose name is obtained by adding to the input
       file name the string "EncryptedECB" + EXT.
     */
    static void encryptECB(String filename, String key)
    {
        try
        {
            Scanner s = new Scanner(new File(filename + EXT));
            PrintWriter w = new PrintWriter(new FileWriter(filename + "EncryptedECB" + EXT));
 
            processHeader(s, w);
 
            DES des = buildDES(key);
 
            while (s.hasNextInt())
            {
                readBlock(s);                        // fills 'block' with 64 plaintext bits
                int[] cipher = des.encryptDES(block);
                copy64(cipher, block);               // move result into 'block' for writeBlock
                writeBlock(w);
            }
 
            s.close();
            w.close();
        }
        catch (Exception e)
        {
            System.out.println("encryptECB error: " + e.getMessage());
            e.printStackTrace();
        }
    }// encryptECB method
 
    /* given a file name (with no extension) for a PGM image and a DES
       key (in hex format), decrypt the image using DES in ECB mode and store
       the result in a file whose name is obtained by adding to the input
       file name the string "DecryptedECB" + EXT.
     */
    static void decryptECB(String filename, String key)
    {
        try
        {
            Scanner s = new Scanner(new File(filename + EXT));
            PrintWriter w = new PrintWriter(new FileWriter(filename + "DecryptedECB" + EXT));
 
            processHeader(s, w);
 
            DES des = buildDES(key);
 
            while (s.hasNextInt())
            {
                readBlock(s);                        // fills 'block' with 64 ciphertext bits
                int[] plain = des.decryptDES(block);
                copy64(plain, block);
                writeBlock(w);
            }
 
            s.close();
            w.close();
        }
        catch (Exception e)
        {
            System.out.println("decryptECB error: " + e.getMessage());
            e.printStackTrace();
        }
    }// decryptECB method
 
    // ---------------------------------------------------------------
    // CBC
    // ---------------------------------------------------------------
 
    /* given a file name (with no extension) for a PGM image, a DES
       key and an initialization vector (both in hex format), encrypt
       the image using DES in CBC mode and store the result in a file
       whose name is obtained by adding to the input file name the
       string "EncryptedCBC" + EXT.
 
       CBC encryption:
           C[0] = DES_E( P[0] XOR IV )
           C[i] = DES_E( P[i] XOR C[i-1] )   for i >= 1
     */
    static void encryptCBC(String filename, String key, String IV)
    {
        try
        {
            Scanner s = new Scanner(new File(filename + EXT));
            PrintWriter w = new PrintWriter(new FileWriter(filename + "EncryptedCBC" + EXT));
 
            processHeader(s, w);
 
            DES des = buildDES(key);
 
            // previousBlock is initialised to the IV bit-vector
            int[] previousBlock = hexToBits(IV);    // 64 bits
 
            while (s.hasNextInt())
            {
                readBlock(s);                        // 'block' = P[i]
 
                int[] xored  = xor64(block, previousBlock);    // P[i] XOR C[i-1]
                int[] cipher = des.encryptDES(xored);           // C[i]
 
                copy64(cipher, block);               // put C[i] into 'block' for writing
                writeBlock(w);
 
                // C[i] becomes the previous block for the next iteration
                previousBlock = clone64(cipher);
            }
 
            s.close();
            w.close();
        }
        catch (Exception e)
        {
            System.out.println("encryptCBC error: " + e.getMessage());
            e.printStackTrace();
        }
    }// encryptCBC method
 
    /* given a file name (with no extension) for a PGM image, a DES
       key and an initialization vector (both in hex format), decrypt
       the image using DES in CBC mode and store the result in a file
       whose name is obtained by adding to the input file name the
       string "DecryptedCBC" + EXT.
 
       CBC decryption:
           P[0] = DES_D( C[0] ) XOR IV
           P[i] = DES_D( C[i] ) XOR C[i-1]   for i >= 1
     */
    static void decryptCBC(String filename, String key, String IV)
    {
        try
        {
            Scanner s = new Scanner(new File(filename + EXT));
            PrintWriter w = new PrintWriter(new FileWriter(filename + "DecryptedCBC" + EXT));
 
            processHeader(s, w);
 
            DES des = buildDES(key);
 
            // previousBlock is initialised to the IV bit-vector
            int[] previousBlock = hexToBits(IV);    // 64 bits
 
            while (s.hasNextInt())
            {
                readBlock(s);                        // 'block' = C[i]
 
                // Save C[i] BEFORE anything else — needed as previousBlock
                // on the next iteration, and decryptDES does not modify block[].
                int[] savedCipher = clone64(block);
 
                int[] decrypted = des.decryptDES(block);         // DES_D( C[i] )
                int[] plain     = xor64(decrypted, previousBlock); // P[i]
 
                copy64(plain, block);                // put P[i] into 'block' for writing
                writeBlock(w);
 
                // C[i] becomes the previous block for the next iteration
                previousBlock = savedCipher;
            }
 
            s.close();
            w.close();
        }
        catch (Exception e)
        {
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
