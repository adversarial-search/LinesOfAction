package assistants;

import java.sql.SQLOutput;
import java.util.Objects;
import java.util.Random;

import static assistants.LevelBuild.B;
import static assistants.LevelBuild.W;

public class ZobristHashing {
    // TODO: fix runtime issues

    //4 bits for the depth , one bit for turn (0 is white 1 is black) , and 59 bits for the position.
    // total of 64 bits for the key (long data type)
    public static Random random = new Random();
    public static long[][] hashingMatrixWhite=fillHashingMatrix();
    public static long[][] hashingMatrixBlack=fillHashingMatrix();

    public static String toBinaryWithLeadingZeroes(long numberToDisplay){
        String tailEndOfString = Long.toBinaryString(numberToDisplay);
        String headOfString = "0".repeat(64-tailEndOfString.length());
        return headOfString+tailEndOfString;
    }
    public static long[][] fillHashingMatrix(){
        long[][] filledMatrix = new long[8][8];

        for(byte row = 0; row< 8;row++){
            for(byte column=0; column < 8;column++){
                filledMatrix[row][column]= random.nextLong(0L,576460752303423488L);
            }
            System.out.println();
        }

        return filledMatrix;
    };

    public static long getZobristKeyForPosition(byte [][]state,byte color, byte depth){
        long returnValue = 0;
        for(byte row = 0; row < 8; row++ ){
            for(byte column = 0; column<8;column++){
                returnValue ^= getValueOfSquare(row,column,state[row][column]);
            }
        }

        //we add a leading bit if black is to move in the position
        returnValue ^=color==W? 0:576460752303423488L;

        System.out.println("afterAddingToMove for "+ color +" "+toBinaryWithLeadingZeroes(returnValue));

        //we add leading bits for depth;

        if(depth!=0) {
            long bitMaskForDepth = 576460752303423488L;

            byte amountToShift = (byte) (Math.ceil(Math.log(depth) / Math.log(2)));
            System.out.println("amount to shift: "+amountToShift);
            while (amountToShift >= 0) {
                bitMaskForDepth=bitMaskForDepth<<1;
                amountToShift -= 1;
            }

            System.out.println("Bitmask for depth "+ toBinaryWithLeadingZeroes(bitMaskForDepth));
            returnValue ^= bitMaskForDepth;
        }
        System.out.println("final return value: "+toBinaryWithLeadingZeroes(returnValue));

        return returnValue;
    }

    public static long getValueOfSquare(byte row, byte column, byte color){
        if(color==W){
            return hashingMatrixWhite[row][column];
        }
        else if(color ==B){
            return hashingMatrixBlack[row][column];
        }else{
            return 0;
        }
    }



}
