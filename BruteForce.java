/*****************************************************
 * CS 326 - Spring 2026 - Assignment #3
 * 
 * Student's full name: _____
 * Student's full name: _____
 * Student's full name: _____
 * 
 *****************************************************/

class BruteForce {
    /*
     * Given two plaintext-ciphertext pairs and the number numBits of
     * significant bits in each key, perform a brute-force search of DoubleDES,
     * that is, try all key pairs from 0 up (in which each key is numBits long)
     * and output ALL of the key pairs that produce the two given ciphertexts
     * given the respective plaintexts. This method also records the time in
     * seconds to find each matching key pair.
     * This method sends to the console window some output formatted as follows:
     * 
     * 000000000000000f 000000000000000d time = 0.021s
     * 000000000000000f 000000000000000e time = 0.021s
     * ....
     * 0000000000000007 0000000000000006 time = 0.029s
     * 0000000000000007 0000000000000007 time = 0.030s
     * # key pairs = 7
     * 
     * where each line contains a matching key pair followed by the runtime
     * since the start of the execution of the method. In the example above,
     * numBits is 4 (only the 4 least significant digits of each key can be
     * equal to 1). Therefore, the brute-force search in this case tried all
     * key pairs in which each key value ranged from 0 to 15 (or 0 to f in hex).
     * In this made-up example, there were 7 key pairs (only 4 are shown) that
     * produced the ciphertext block from the corresponding plaintext block in
     * both input pairs.
     */
    static void bruteForce(String plaintext1, String ciphertext1,
            String plaintext2, String ciphertext2, int numBits) {
        long startTime, elapsedTime;
        startTime = System.currentTimeMillis();

        int[] p1 = Utils.getBitVectorFromHex(plaintext1);
        int[] p2 = Utils.getBitVectorFromHex(plaintext2);
        int limit = 1 << numBits;
        int count = 0;
        for (int k1 = 0; k1 < limit - 1; k1++) {
            for (int k2 = 0; k2 < limit - 1; k2++) {
                String key1 = String.format("%016x", k1);
                String key2 = String.format("%016x", k2);

                DoubleDES cipher = new DoubleDES(key1, key2);

                int[] testC1 = cipher.encrypt(p1);
                int[] testC2 = cipher.encrypt(p2);

                if (Utils.getHex(testC1).equals(ciphertext1) && Utils.getHex(testC2).equals(ciphertext2)) {
                    elapsedTime = System.currentTimeMillis() - startTime;

                    System.out.printf("            %s %s  time = %.3fs\n",
                            key1, key2, elapsedTime / 1000.0);
                    count++;
                }
            }
        }
        System.out.println("# key pairs = " + count);

    }// bruteForce method

    /*
     * Given a test number and a number of bits, this method returns
     * an array of two keys, each of which has only numBits
     * significant bits and has a specific value hardcoded in the test
     * case number. This method also outputs to the console window the
     * value of the two keys it returns.
     * 
     * This method will be called to produce the two keys that
     * DoubleDES uses to encrypt two plaintext blocks and before
     * invoking the bruteForce method. Therefore, the key pair that
     * the brute force search is trying to find is printed just above
     * the output of the search.
     * 
     * Do NOT modify this method.
     */
    static String[] getKeyPair(int testNumber, int numBits) {
        String key1 = "", key2 = "";
        switch (testNumber) {
            case 1:
                key1 = "1";
                key2 = "1";
                for (int i = 1; i <= numBits - 1; i++) {
                    key1 += "1";
                    key2 += "0";
                }
                break;
            case 2:
                key1 = "1";
                key2 = "1";
                for (int i = 1; i <= numBits - 1; i++) {
                    key1 += "1";
                    key2 += (i % 2) + "";
                }
                break;
        }
        while (key1.length() % 4 != 0) {
            key1 = "0" + key1;
            key2 = "0" + key2;
        }
        key1 = Utils.binStringToHex(key1);
        key2 = Utils.binStringToHex(key2);
        while (key1.length() < 16) {
            key1 = "0" + key1;
            key2 = "0" + key2;
        }
        System.out.println("Actual keys: " + key1 + " " + key2 +
                "  numBits = " + numBits);

        return new String[] { key1, key2 };
    }// getKeyPair method

    /*
     * This method is used for testing purposes. This driver code
     * invokes the bruteForce method repeatedly with numBits equal to
     * 2, then to 3, then to 4, etc. You will have to terminate this
     * program (e.g., with a CTRL-C) as soon as the TOTAL runtime of
     * the last brute-force search has exceeded 60 seconds.
     * 
     * Do NOT modify this method.
     */
    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Usage: java BruteForce [test1 or test2] " +
                    "<P1> <P2>");
            System.exit(1);
        }
        String testCase = args[0];
        String P1 = args[1];
        String P2 = args[2];
        String key1 = "", key2 = "";
        int numBits;
        String[] keys = null;
        for (numBits = 2; true; numBits++) {
            // generate the actual key pair
            if (testCase.equals("test1")) {
                keys = getKeyPair(1, numBits);
            } else if (testCase.equals("test2")) {
                keys = getKeyPair(2, numBits);
            } else {
                System.out.println("This test case is not implemented.");
                System.exit(1);
            }
            key1 = keys[0];
            key2 = keys[1];
            // encrypt the plaintexts
            DoubleDES ddes = new DoubleDES(key1, key2);
            String C1 = Utils.getHex(ddes.encrypt(Utils.getBitVectorFromHex(P1)));
            String C2 = Utils.getHex(ddes.encrypt(Utils.getBitVectorFromHex(P2)));
            bruteForce(P1, C1, P2, C2, numBits);
        } // loop on numBits

    }// main method
}// class BruteForce
