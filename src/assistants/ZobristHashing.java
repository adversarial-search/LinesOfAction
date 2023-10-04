package assistants;

import java.sql.SQLOutput;
import java.util.Objects;
import java.util.Random;

import static assistants.LevelBuild.B;
import static assistants.LevelBuild.W;

public class ZobristHashing {
    // TODO: fix runtime issues

    //3 bits for the depth , one bit for turn (0 is white 1 is black) , and 28 bits for the position.
    // total of 32 bits for the key (int data type is enough)

    public static int[][] hashingMatrixWhite=fillHashingMatrix();
    public static int[][] hashingMatrixBlack=fillHashingMatrix();

    public static int[][] fillHashingMatrix(){
        int[][] filledMatrix = new int[8][8];

        for(byte row = 0; row< 8;row++){
            for(byte column=0; column < 8;column++){
                //268435456
                Random random = new Random();
                int randomlyGenNumber = random.nextInt(0,268435456);
                System.out.print(randomlyGenNumber+" ");
                filledMatrix[row][column]= randomlyGenNumber;
            }
            System.out.println();
        }

        return filledMatrix;
    };

    public static int getZobristKeyForPosition(byte [][]state,byte color, byte depth){
        int returnValue = 0;
        for(byte row = 0; row < 8; row++ ){
            for(byte column = 0; column<8;column++){
                returnValue ^= getValueOfSquare(row,column,state[row][column]);
            }
        }

        //we add a leading bit if black is to move in the position
        returnValue ^=color==W? 0:268435456;

        //we add leading bits for depth;
        byte bitsToShift = (byte)(32 - Math.ceil(Math.log(depth)/Math.log(2)));

        returnValue ^= (int)Math.pow(2,bitsToShift);

        return returnValue;
    }

    public static int getValueOfSquare(byte row, byte column, byte color){
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
