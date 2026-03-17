/*****************************************************
 * CS 326 - Spring 2026 - Assignment #3
 * 
 * Student's full name: _____
 * Student's full name: _____
 * Student's full name: _____
 * 
 *****************************************************/

class Utils {
    /*
     * given a character string, return the sequence of ASCII codes (in
     * hexadecimal) for the characters in the string. Sample input/output:
     * input: "ABC" output: "414243"
     * input: "\nA\nB\n" output: "0A410A420A"
     * Note that each input character always yields exactly two hex digits.
     */
    static String textToHex(String s) {
        String hexCode = "";
        for (int i = 0; i < s.length(); i++) {
            String hex = Integer.toHexString((int) s.charAt(i)).toUpperCase();
            if (hex.length() == 1) {
                hex = "0" + hex;
            }
            hexCode += hex;
        }
        return hexCode;

    }// textToHex method

    /*
     * given a string of ascii codes (in hexadecimal), return the string of
     * the corresponding characters.
     * input: "414243" output: "ABC"
     * input: "0A410A420A" output: "\nA\nB\n"
     * Note that all input strings have an even length.
     */
    static String hexToText(String s) {
        String result = "";
        for (int i = 0; i < s.length(); i += 2) {
            String hexCode = s.substring(i, i + 2);
            result = result + (char) Integer.parseInt(hexCode, 16);
        }
        return result;
    }// hexTotext method

    /*
     * given a binary string, return the integer array of the same length as
     * the bit string and in which each element is the integer value of the
     * bit in the corresponding position in the string. Sample input/output:
     * input: "01101" output: [0, 1, 1, 0, 1]
     */
    static int[] binStringToIntArray(String bits) {
        int[] result = new int[bits.length()];
        for (int i = 0; i < bits.length(); i++) {
            result[i] = bits.charAt(i) - '0';
        }
        return result;
    }// bitStringToIntArray method

    /*
     * given an integer array containing 0s and 1s exclusively, return
     * the binary string of the same length in which each element is the
     * character ('0' or '1') of the corresponding element in the input array.
     * input: [0, 1, 1, 0, 1] output: "01101"
     */
    static String intArrayToBinString(int[] data) {
        String result = "";
        for (int i = 0; i < data.length; i++) {
            result = result + (char) (data[i] + '0');
        }
        return result;
    }// intArrayToBinString method

    /*
     * given an arbitrary long string of hexadecimal digits and a number
     * of bits, return the binary string of the given length corresponding
     * to the first input. Sample input/output:
     * input: "ABC" 16 output: "0000101010111100"
     * input: "01F3" 16 output: "0000000111110011"
     * Note: You must assume that numBits is always larger than or equal to
     * 4 times the number of hexadecimal digits in the first argument.
     */
    static String hexToBinString(String s, int numBits) {
        java.math.BigInteger num = new java.math.BigInteger(s, 16);
        String binString = num.toString(2);

        while (binString.length() < numBits) {
            binString = "0" + binString;
        }
        return binString;
    }// hexToBinString method

    /*
     * given a binary string, return the hexadecimal representation of the
     * input as a String. Sample input/output:
     * input: "01101110" output: "6E"
     * Note: you must assume that the length of the input is a multiple of 4.
     */
    static String binStringToHex(String bits) {
        String hexString = "";
        for (int i = 0; i < bits.length(); i += 4) {
            String binarySegment = bits.substring(i, i + 4);
            int decimalValue = Integer.parseInt(binarySegment, 2);
            String hexDigit = Integer.toHexString(decimalValue)
                    .toUpperCase();
            hexString += hexDigit;
        }
        return hexString;
    }// binStringToHex method

    /*
     * given two arrays of the same size each containing n integer values
     * equal to 0 or 1 exclusively, return an n-element array containing the
     * bitwise XOR of the pairs of input bits. Sample input/output:
     * input: [0, 0, 1, 1] and [0, 1, 0, 1] output: [0, 1, 1, 0]
     */
    static int[] XOR(int[] a, int[] b) {
        int[] result = new int[a.length];
        for (int i = 0; i < a.length; i++) {
            result[i] = a[i] ^ b[i];
        }
        return result;
    }// XOR method

    /*
     * given an n-long permutation of bit positions ranging from 1 to m and
     * an m-bit vector, return the n-bit vector resulting from applying the
     * permutation to the second vector. Sample input/output:
     * input: [1, 1, 2, 1, 1, 2, 2] and [0, 1]
     * output: [0, 0, 1, 0, 0, 1, 1]
     * Note that the values in the permutation are position indexes
     * starting at 1, not 0. Therefore, the value of the bit at position 1 in
     * the second argument is 0, not 1.
     */
    static int[] applyPermut(int[] perm, int[] data) {
        int[] result = new int[perm.length];
        for (int i = 0; i < perm.length; i++) {
            result[i] = data[perm[i] - 1];
        }
        return result;
    }// applyPermut method

    /*
     * given a text string (in ASCII), return the array containing the
     * bits that, when concatenated together, make up the binary representation
     * of the ASCII codes in the input. Sample input/output:
     * input: "ABC"
     * output: [0, 1, 0, 0, 0, 0, 0, 1,
     * 0, 1, 0, 0, 0, 0, 1, 0,
     * 0, 1, 0, 0, 0, 0, 1, 1]
     * since 01000001 (base 2) = 65 (base 10) = ASCII code of A
     * 01000010 (base 2) = 66 (base 10) = ASCII code of B
     * 01000011 (base 2) = 67 (base 10) = ASCII code of C
     * 
     * This method does not need to handle the case where the input is ""
     */
    static int[] getBitVectorFromText(String text) {
        int [] bits = new int[text.length() * 8];
        int [] nums = new int[text.length()];
        for(int i = 0; i < text.length(); i++){
            nums[i] = (int) text.charAt(i);
        }
        for(int i = 0; i < nums.length; i++){
            for(int j = 7; j >= 0; j--){
                bits[i*8+j] = nums[i]%2;
                nums[i] = nums[i]/2;
            }
        }

        return bits; // only here to please the compiler

    }// getBitVectorFromText method

    /*
     * given a string of hex digits, return the array containing the
     * bits that, when concatenated together, make up the binary
     * representation of the hex digits in the input. Sample input/output:
     * input: "abc"
     * output: [1, 0, 1, 0, 1, 0, 1, 1, 1, 1, 0, 0]
     * since 1010 (base 2) = 10 (base 10) = a (base 16)
     * 1011 (base 2) = 11 (base 10) = b (base 16)
     * 1100 (base 2) = 12 (base 10) = c (base 16)
     * 
     * This method does not need to handle the case where the input is ""
     */
    static int[] getBitVectorFromHex(String hex) {
        int[] bits = new int[hex.length() * 4];
        int[] nums = new int[hex.length()];
        for(int i = 0; i < hex.length(); i++){
            String hexChar = "" + hex.charAt(i); 
            nums[i] = Integer.parseInt(hexChar,16);
        }

        for(int i = 0; i < nums.length; i++){
            for(int j = 3; j >= 0; j--){
                bits[i*4+j] = nums[i]%2;
                nums[i] = nums[i]/2;
            }
        }
        return bits; // only here to please the compiler
    }// getBitVectorFromHex method

    /*
     * given an array of bits (0 or 1), return the string of hex digits
     * whose binary representation is encoded in the input array.
     * Sample input/output:
     * input: [0,1,1,0,1,1,1,1]
     * output: "6f"
     * since 0110 (base 2) = 6 (base 10) = 6 (base 16)
     * 1111 (base 2) = 15 (base 10) = f (base 16)
     * 
     * This method only need handle cases where the length of the
     * input array is a multiple of 4.
     */
    static String getHex(int[] bits) {
        String hex = "";
        for (int i = 0; i < bits.length; i += 4) {
            int value = 0;

            for (int j = 0; j < 4; j++) {
                value = value * 2 + bits[i + j];
            }

            hex += Integer.toHexString(value);
        }

        return hex; // only here to please the compiler
    }// getHex method

    public static void main(String[] args) {
        // int[] hex = { 0, 1, 1, 0, 1, 1, 1, 1 };
        // System.out.println(getHex(hex));

        int[] bin = getBitVectorFromText("ABC");
        for(int i = 0; i < bin.length; i++){
            System.out.print(bin[i]);
        }
    }

}// class Utils
