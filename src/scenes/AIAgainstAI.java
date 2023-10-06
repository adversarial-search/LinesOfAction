package scenes;

import assistants.LevelBuild;
import main.Game;
import objects.StateInfo;
import ui.MyButton;

import java.awt.*;
import java.util.*;
import java.util.stream.Collectors;

import static assistants.MinMaxFunctions.getAlphaBetaMiniMaxState;
import static assistants.MinMaxFunctions.getBasicMiniMaxState;
import static main.GameStates.MENU;
import static main.GameStates.SetGameState;

public class AIAgainstAI extends GameScene implements SceneMethods {
    private static final byte WHITE_DEPTH_BASIC = 0;
    private static final byte WHITE_DEPTH_AB = 2;
    private static final byte BLACK_DEPTH_BASIC = 0;
    private static final byte BLACK_DEPTH_AB = 2;
    protected static boolean whiteChosen = false;
    protected static boolean blackChosen = false;
    protected byte whiteAiType;
    protected byte blackAiType;
    private static final Random random = new Random ( );
    private static long startTime = 0L;
    private static long endTime = 0L;

    private static int movesPerGameCounter = 0;
    private static int gamesPlayedCounter = 0;
    private static int gamesWonByWhite = 0;
    private static int gamesWonByBlack = 0;
    private static final int gamesToPlay = 201;
    private static int gamesThrashed = 0;
    public static Set<StateInfo> stateHistory = new HashSet<> ( );
    private static ArrayList<Integer> numMovesTakenPerGame = new ArrayList<> ( );
    private static ArrayList<Long> timeTakenPerGame = new ArrayList<> ( );
    private static Map<Integer, ArrayList<Integer>> timeTakenPerMove = new HashMap<> (  );
    private MyButton
            bReset,
            bMenu,
            bChooseNoPruningWhite,
            bChooseAlphaBetaWhite,
            bChooseNoPruningBlack,
            bChooseAlphaBetaBlack,
            bNextMove,
            drawTextPickWhiteAiType,
            drawTextPickBlackAiType;


    public AIAgainstAI ( Game game ) {
        super ( game );
        initButtons ( );
    }

    public static void setUpInitialGameState () {
        piecesPositions = LevelBuild.getInitialPiecesPositions ( );
        turn = BLACK_TURN;
        gameWon = false;
        winner = -1;
        whiteChosen = false;
        blackChosen = false;
        for(int i=0; i<1_000; i++) timeTakenPerMove.put ( i, new ArrayList<> (  ) );
    }

    private void initButtons () {
        bChooseNoPruningWhite = new MyButton ( "Simple MinMax", 192, 128, 256, 128 );
        bChooseAlphaBetaWhite = new MyButton ( "Alpha Beta MinMax", 192, 416, 256, 128 );
        bChooseNoPruningBlack = new MyButton ( "Simple MinMax", 192, 128, 256, 128 );
        bChooseAlphaBetaBlack = new MyButton ( "Alpha Beta MinMax", 192, 416, 256, 128 );
        drawTextPickWhiteAiType = new MyButton ( "Choose white ai type", 192, 60, 256, 50 );
        drawTextPickBlackAiType = new MyButton ( "Choose black ai type", 192, 60, 256, 50 );

        bMenu = new MyButton ( "Menu", 14, 12, 100, 40 );
        bReset = new MyButton ( "Reset", 128, 12, 100, 40 );
        // TODO: Think of a better looking design for the next button
        bNextMove = new MyButton ( ">>", 580, 650, 50, 40 );
    }

    private void makeAiMove ( byte currentAiId ) {
        byte aiType = currentAiId == W ? whiteAiType : blackAiType;

        if (turn == W) {
            switch (aiType) {
                case CLASSIC_MINMAX ->
                        piecesPositions = getBasicMiniMaxState ( currentAiId, WHITE_DEPTH_BASIC, piecesPositions );
                case PRUNING_MINMAX ->
                        piecesPositions = getAlphaBetaMiniMaxState ( currentAiId, WHITE_DEPTH_AB, piecesPositions );
            }
        } else if (turn == B) {
            switch (aiType) {
                case CLASSIC_MINMAX ->
                        piecesPositions = getBasicMiniMaxState ( currentAiId, BLACK_DEPTH_BASIC, piecesPositions );
                case PRUNING_MINMAX ->
                        piecesPositions = getAlphaBetaMiniMaxState ( currentAiId, BLACK_DEPTH_AB, piecesPositions );
            }
        }


        checkWinningConditions ( );
        changeTurn ( );
    }


    @Override
    protected void resetGame () {
        resetValidMovesAndActivePiece ( );
        piecesPositions = LevelBuild.getInitialPiecesPositions ( );
        gameWon = false;
        turn = BLACK_TURN;
        blackChosen = false;
        whiteChosen = false;
        stateHistory = new HashSet<> ( );
    }

    @Override
    protected void changeTurn () {
        if (turn == BLACK_TURN) turn = WHITE_TURN;
        else turn = BLACK_TURN;
    }

    @Override
    protected void makeMove ( int x, int y ) {

    }

    private static long moveStartTime;
    private static long moveEndTime;

    @Override
    public void render ( Graphics g ) {

        if (whiteChosen && blackChosen) {
            //draw background
            drawBoardBackground ( g );

            //draw piecesPositions
            drawPieces ( g );

            //draw active piece
            drawActive ( g );

            //draw valid moves
            drawValidMoves ( g );

            //draw buttons
            drawButtons ( g );

            //display winner
            displayWinner ( g );


            if (!gameWon) {
                StateInfo stateInfo = new StateInfo ( piecesPositions, turn == B );

                if (stateHistory.contains ( stateInfo )) System.out.println ( "==========THRASHED==========" );
                else stateHistory.add ( stateInfo );

                if(gamesPlayedCounter != 0)
                    moveStartTime = System.currentTimeMillis ();

                makeNextMove ( );

                if(gamesPlayedCounter != 0) {
                    moveEndTime = System.currentTimeMillis ( );
                    ArrayList<Integer> tmp = timeTakenPerMove.get ( movesPerGameCounter - 1 );
                    tmp.add ( (int) (moveEndTime - moveStartTime) );
                    timeTakenPerMove.put ( movesPerGameCounter - 1, tmp );
                }
            } else {
                System.out.println ("Games played: " + (gamesPlayedCounter+1) );
                if(gamesPlayedCounter != 0) {
                    endTime = System.currentTimeMillis ( );
                    timeTakenPerGame.add ( endTime - startTime );
                    startTime = endTime;

                    if (winner == W) gamesWonByWhite += 1;
                    if (winner == B) gamesWonByBlack += 1;
                }
                gamesPlayedCounter += 1;
                if(gamesPlayedCounter != 1)
                    numMovesTakenPerGame.add ( movesPerGameCounter );

                if (gamesPlayedCounter < gamesToPlay) {
                    resetValidMovesAndActivePiece ( );
                    piecesPositions = LevelBuild.getInitialPiecesPositions ( );
                    gameWon = false;
                    turn = BLACK_TURN;
                    stateHistory = new HashSet<> ( );
                    movesPerGameCounter = 0;
                } else {
                    System.out.println ("Games won by white: " + gamesWonByWhite );
                    System.out.println ("Games won by black: " + gamesWonByBlack );
                    System.out.println ("White win ratio: " + (gamesWonByWhite*100d/(gamesWonByWhite+gamesWonByBlack)) + "%");
                    System.out.println ("Black win ratio: " + (gamesWonByBlack*100d/(gamesWonByWhite+gamesWonByBlack)) + "%");

                    System.out.println ( );
                    System.out.println ("Moves per game: " + numMovesTakenPerGame.stream ( ).sorted ( ).toList ( ) );
                    System.out.println ("Average number of moves per game: " + numMovesTakenPerGame.stream ().mapToInt ( Integer::intValue ).average ().orElse ( 0.0d ) );

                    System.out.println ( );
                    System.out.println ("Time per game: " + timeTakenPerGame.stream ( ).sorted (  ).map ( num -> num / 1000d + "s" ).toList ( ) );
                    System.out.println ("Average time taken per game: " + timeTakenPerGame.stream ().mapToDouble ( num -> num/1000d ).average ().orElse ( 0.0d ) + "s" );


                    System.out.println ( );
                    System.out.println ("Average time taken per move: " );
                    Map<Integer, Double> frequencies = new LinkedHashMap<> (  );
                    for(int i=0; i < timeTakenPerMove.keySet ().stream ().max ( Integer::compareTo ).orElse ( 0 ); i++) {
                        ArrayList<Integer> arr = timeTakenPerMove.get ( i );
                        if(arr.isEmpty ()) continue;

                        frequencies.put ( i, arr.stream ().mapToInt ( Integer::valueOf ).average ().orElse ( -1.0d ) );
                    }
                    for(int i=0; i < timeTakenPerMove.keySet ().stream ().max ( Integer::compareTo ).orElse ( 0 ); i++)
                        if(frequencies.containsKey ( i ))
                            System.out.println ((i+1) + ": " + frequencies.get ( i ) );


                    System.exit ( 0 );
                }
            }
        } else {
            if (!blackChosen) {
                drawMenuBackground ( g );
                drawPickText ( g, B );
                drawChooseAiTypeButtonsBlack ( g );
            } else {
                drawMenuBackground ( g );
                drawPickText ( g, W );
                drawChooseAiTypeButtonsWhite ( g );
            }
        }
    }

    public void drawPickText ( Graphics g, byte color ) {
        if (color == W) {
            drawTextPickWhiteAiType.drawTextOnly ( g );
        } else {
            drawTextPickBlackAiType.drawTextOnly ( g );
        }
    }

    private void drawButtons ( Graphics g ) {
        bMenu.draw ( g );
        bReset.draw ( g );
        bNextMove.draw ( g );
    }

    private void drawChooseAiTypeButtonsWhite ( Graphics g ) {
        bChooseAlphaBetaWhite.draw ( g );
        bChooseNoPruningWhite.draw ( g );
    }

    private void drawChooseAiTypeButtonsBlack ( Graphics g ) {
        bChooseAlphaBetaBlack.draw ( g );
        bChooseNoPruningBlack.draw ( g );
    }

    @Override
    public void mouseMoved ( int x, int y ) {
        bNextMove.setMouseOver ( false );
        bMenu.setMouseOver ( false );
        bReset.setMouseOver ( false );
        bChooseAlphaBetaWhite.setMouseOver ( false );
        bChooseNoPruningWhite.setMouseOver ( false );
        bChooseAlphaBetaBlack.setMouseOver ( false );
        bChooseNoPruningBlack.setMouseOver ( false );

        if (bMenu.getBounds ( ).contains ( x, y ))
            bMenu.setMouseOver ( true );
        else if (bReset.getBounds ( ).contains ( x, y ))
            bReset.setMouseOver ( true );
        else if (bChooseNoPruningBlack.getBounds ( ).contains ( x, y ))
            bChooseNoPruningBlack.setMouseOver ( true );
        else if (bChooseAlphaBetaBlack.getBounds ( ).contains ( x, y ))
            bChooseAlphaBetaBlack.setMouseOver ( true );
        else if (bNextMove.getBounds ( ).contains ( x, y ))
            bNextMove.setMouseOver ( true );


        if (bChooseNoPruningWhite.getBounds ( ).contains ( x, y ))
            bChooseNoPruningWhite.setMouseOver ( true );
        else if (bChooseAlphaBetaWhite.getBounds ( ).contains ( x, y ))
            bChooseAlphaBetaWhite.setMouseOver ( true );


    }

    @Override
    public void mouseClicked ( int x, int y ) {

        if (!blackChosen) {
            chooseBlackAiType ( x, y );
            return;
        } else if (!whiteChosen) {
            chooseWhiteAiType ( x, y );
            startTime = System.currentTimeMillis ( );
            return;
        }


        // check if clicked management buttons
        if (bMenu.getBounds ( ).contains ( x, y )) {
            resetGame ( );
            SetGameState ( MENU );
        } else if (bReset.getBounds ( ).contains ( x, y )) {
            resetGame ( );
        } else if (bNextMove.getBounds ( ).contains ( x, y ) && !gameWon) {
            makeNextMove ( );
        }


    }

    @Override
    public void mousePressed ( int x, int y ) {
        if (bMenu.getBounds ( ).contains ( x, y ))
            bMenu.setMousePressed ( true );
        else if (bReset.getBounds ( ).contains ( x, y ))
            bReset.setMousePressed ( true );
        else if (bChooseNoPruningWhite.getBounds ( ).contains ( x, y ))
            bChooseNoPruningWhite.setMousePressed ( true );
        else if (bChooseAlphaBetaWhite.getBounds ( ).contains ( x, y ))
            bChooseAlphaBetaWhite.setMousePressed ( true );
        else if (bNextMove.getBounds ( ).contains ( x, y ))
            bNextMove.setMousePressed ( true );

        if (bChooseNoPruningBlack.getBounds ( ).contains ( x, y ))
            bChooseNoPruningBlack.setMousePressed ( true );
        else if (bChooseAlphaBetaBlack.getBounds ( ).contains ( x, y ))
            bChooseAlphaBetaBlack.setMousePressed ( true );

    }

    @Override
    public void mouseReleased ( int x, int y ) {
        resetButtons ( );
    }

    private void chooseWhiteAiType ( int x, int y ) {
        if (bChooseAlphaBetaWhite.getBounds ( ).contains ( x, y )) {
            whiteAiType = PRUNING_MINMAX;
            whiteChosen = true;
        } else if (bChooseNoPruningWhite.getBounds ( ).contains ( x, y )) {
            whiteAiType = CLASSIC_MINMAX;
            whiteChosen = true;
        }
    }

    private void chooseBlackAiType ( int x, int y ) {
        if (bChooseAlphaBetaBlack.getBounds ( ).contains ( x, y )) {
            blackAiType = PRUNING_MINMAX;
            blackChosen = true;
        } else if (bChooseNoPruningBlack.getBounds ( ).contains ( x, y )) {
            blackAiType = CLASSIC_MINMAX;
            blackChosen = true;
        }
    }

    private void makeNextMove () {
        makeAiMove ( turn );
        movesPerGameCounter += 1;
    }

    private void resetButtons () {
        bChooseAlphaBetaBlack.resetBooleans ( );
        bChooseAlphaBetaWhite.resetBooleans ( );
        bChooseNoPruningBlack.resetBooleans ( );
        bChooseNoPruningWhite.resetBooleans ( );
        bMenu.resetBooleans ( );
        bReset.resetBooleans ( );
        bNextMove.resetBooleans ( );
    }
}
