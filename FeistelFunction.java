/*****************************************************
 * CS 326 - Spring 2026 - Assignment #2
 * 
 * Student's full name: Gretchen Rost
 * Student's full name: Ethan Trammell
 * Student's full name: Caleb Wisneski
 *****************************************************/

abstract class FeistelFunction
{
    /* given a 2w-bit block and a k-bit key, return the 2w-bit block 
       resulting from applying the round function to the two inputs
    */
    abstract int[] round(int[] block, int[] key);
    
}// class FeistelFunction

/* implements the Feistel round function that 
   ignores its inputs (data block and key) and 
   always returns all 0 bits
*/
class FeistelAllZeros  extends FeistelFunction
{
    int[] round(int[] block, int[] key)
    {
        return new int[block.length]; 
    }
}// class FeistelAllZeros

/* implements the Feistel round function that 
   ignores its inputs (data block and key) and 
   always returns all 1 bits
*/
class FeistelAllOnes  extends FeistelFunction
{
    int[] round(int[] block, int[] key)
    {
        int[] result = new int[block.length];
        java.util.Arrays.fill(result, 1);
        return result;
    }
}// class FeistelAllOnes


/* implements the Feistel round function that 
   returns the conjunction (bitwise AND) of its
   two inputs.
*/
class FeistelAnd  extends FeistelFunction
{
    int[] round(int[] block, int[] key)
    {
        int[] result = new int[block.length];
        for (int i = 0; i < block.length; i++) {
            result[i] = block[i] & key[i];
        }
        return result;
    }
}// class FeistelAnd
