package assistants;

import java.sql.SQLOutput;
import java.util.Random;

import static assistants.LevelBuild.B;
import static assistants.LevelBuild.W;

public class ZobristHashing {

    //3 bits for the depth , one bit for turn (0 is white 1 is black) , and 28 bits for the position.
    // total of 32 bits for the key (int data type is enough)

    public static final Integer[][] hashingMatrixWhite=fillHashingMatrix();
    public static final Integer[][] hashingMatrixBlack=fillHashingMatrix();

    public static Random random = new Random();

    public static Integer[][] fillHashingMatrix(){
        Integer[][] filledMatrix = new Integer[8][8];

        for(Integer[] row: filledMatrix){
            for(byte i=0; i< 8;i++){
                int randomlyGenNumber = random.nextInt(0,268435456);
                System.out.print(randomlyGenNumber+" ");
                row[i]= randomlyGenNumber;
            }
            System.out.println();
        }

        return filledMatrix;
    };

    public static int getZobristKeyForPosition(byte [][]state,byte color, byte depth){
        int returnValue = 0;
        for(byte row = 0; row < 8; row++ ){
            for(byte column = 0; column<8;column++){
                returnValue+= getValueOfSquare(row,column,state[row][column]);
            }
        }
        //we clear the leading bits with modulo
        returnValue = returnValue % 268435456;

        //we add a leading bit if black is to move in the position
        returnValue+=color==W? 0:268435456;

        //we add leading bits for depth;
        byte bitsToShift = (byte)(32 - Math.ceil(Math.log(depth)/Math.log(2)));

        returnValue += Math.pow(2,bitsToShift);

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
