package scenes;

import AITypes.AIType;
import AITypes.AlphaBetaMinMaxAi;
import AITypes.ClassicMinMaxAi;
import assistants.LevelBuild;
import main.Game;
import ui.MyButton;

import java.awt.*;
import java.util.Random;

import static assistants.MinMaxFunctions.makeBasicMiniMaxMove;
import static assistants.MinMaxFunctions.makeAlphaBetaMiniMaxMove;
import static main.GameStates.MENU;
import static main.GameStates.SetGameState;
// TODO : debug the clicking issues
public class AIAgainstAI extends GameScene implements SceneMethods{
    private static byte MAX_DEPTH=0;
    protected static boolean whiteChosen = false;
    protected static boolean blackChosen = false;
    protected static boolean useTranspositionTable = false;
    protected static boolean transpositionTableChosen = false;
    protected static int turnCounter=0;
    protected AIType whiteAiType;
    protected AIType blackAiType;
    private static final Random random = new Random ( );

    private MyButton
            bReset,
            bMenu,
            bChooseNoPruningWhite,
            bChooseAlphaBetaWhite,
            bChooseNoPruningBlack,
            bChooseAlphaBetaBlack,
            bNextMove,
            drawTextPickWhiteAiType,
            drawTextPickBlackAiType,
            bUseTranspositionTable,
            bDontUseTranspositionTable,
            drawTextUseTranspositionTable,
            bIncreaseDepth,
            bDecreaseDepth;



    public AIAgainstAI(Game game) {
        super(game);
        initButtons();


    }

    public static void setUpInitialGameState() {
        piecesPositions = LevelBuild.getInitialPiecesPositions();
        turn = BLACK_TURN;
        gameWon = false;
        winner = -1;
        whiteChosen = false;
        blackChosen = false;
        transpositionTableChosen=false;
    }

    private void initButtons() {
        bChooseNoPruningWhite = new MyButton("Simple MinMax", 192, 128, 256, 128);
        bChooseAlphaBetaWhite = new MyButton("Alpha Beta MinMax", 192, 416, 256, 128);
        bChooseNoPruningBlack = new MyButton("Simple MinMax", 192, 128, 256, 128);
        bUseTranspositionTable = new MyButton("YES", 192, 416, 256, 128);
        bDontUseTranspositionTable = new MyButton("NO", 192, 128, 256, 128);
        bChooseAlphaBetaBlack = new MyButton("Alpha Beta MinMax", 192, 416, 256, 128);
        drawTextPickWhiteAiType = new MyButton("Choose white ai type",192, 60, 256, 50);
        drawTextPickBlackAiType = new MyButton("Choose black ai type",192, 60, 256, 50);
        drawTextUseTranspositionTable = new MyButton("Use transposition table?",192, 60, 256, 50);
        bIncreaseDepth = new MyButton ( "++Depth", 242, 37, 100, 25 );
        bDecreaseDepth = new MyButton ( "--Depth", 242, 6, 100, 25 );

        bMenu = new MyButton("Menu", 14, 12, 100, 40);
        bReset = new MyButton("Reset", 128, 12, 100, 40);
        // TODO: Think of a better looking design for the next button
        bNextMove = new MyButton(">>",580,650,50,40);
    }

    private void makeAiMove(byte currentAiId) {
        AIType currentAI = currentAiId==W?whiteAiType:blackAiType;
        if(!useTranspositionTable||turnCounter==0)
            currentAI.makeMove(currentAiId,MAX_DEPTH,piecesPositions);
        else
            currentAI.makeMoveWithTranspositionTable(currentAiId,MAX_DEPTH,piecesPositions);

        turnCounter+=1;
        checkWinningConditions();
        changeTurn();

        showNumStatesEvaluated = statesEvaluated;
        statesEvaluated=0;
    }


    @Override
    protected void resetGame() {
        resetValidMovesAndActivePiece();
        piecesPositions = LevelBuild.getInitialPiecesPositions();
        gameWon = false;
        turn = BLACK_TURN;
        blackChosen=false;
        whiteChosen=false;
        transpositionTableChosen=false;
        turnCounter=0;
    }

    @Override
    protected void changeTurn() {
        if (turn == BLACK_TURN) turn = WHITE_TURN;
        else turn = BLACK_TURN;
    }

    @Override
    protected void makeMove(int x, int y) {

    }

    @Override
    public void render(Graphics g) {

        if(whiteChosen&&blackChosen&&transpositionTableChosen){
            //draw background
            drawBoardBackground(g);

            //draw piecesPositions
            drawPieces(g);

            //draw active piece
            drawActive(g);

            //draw valid moves
            drawValidMoves(g);

            //draw buttons
            drawButtons(g);

            //display winner
            displayWinner(g);

            //draw depth management buttons
            drawDepthButtons(g);

            //display current depth
            displayDepthAndNumStates(g);
        } else {
            if(!blackChosen) {
                drawMenuBackground(g);
                drawPickText(g,B);
                drawChooseAiTypeButtonsBlack(g);

            } else if(!whiteChosen){
                drawMenuBackground(g);
                drawPickText(g,W);
                drawChooseAiTypeButtonsWhite(g);
            }else{
                drawMenuBackground(g);
                drawChooseTranspositionTable(g);
                drawPickTranspositionText(g);
            }
        }
    }
    private void displayDepthAndNumStates ( Graphics g ) {
        if(!gameWon) {
            g.setColor ( new Color ( 168, 212, 190 ) );
            g.fillRect ( 356, 12, 40, 40 );
            g.setColor ( new Color ( 88, 69, 47 ) );
            g.drawRect ( 356, 12, 40, 40 );

            String depthStr = String.valueOf(MAX_DEPTH);
            g.drawString ( depthStr, 372, 37 );

            g.setColor ( new Color ( 168, 212, 190 ) );
            g.fillRect ( 408, 12, 100, 40 );
            g.setColor ( new Color ( 88, 69, 47 ) );
            g.drawRect ( 408, 12, 100, 40 );
            g.drawString ( "States Evaluated", 412, 28 );
            g.drawString ( String.valueOf ( showNumStatesEvaluated ), 412, 45 );
        }
    }
    private void drawDepthButtons ( Graphics g ) {
        if(!gameWon) {
            bIncreaseDepth.draw ( g );
            bDecreaseDepth.draw ( g );
        }
    }
    public void drawPickTranspositionText(Graphics g){
        drawTextUseTranspositionTable.drawTextOnly(g);
    }
    public void drawPickText(Graphics g,byte color){
        if(color == W){
            drawTextPickWhiteAiType.drawTextOnly(g);
        }else{
            drawTextPickBlackAiType.drawTextOnly(g);
        }
    }
    private void drawButtons(Graphics g) {
        bMenu.draw(g);
        bReset.draw(g);
        bNextMove.draw(g);
    }
    private void drawChooseAiTypeButtonsWhite(Graphics g){
        bChooseAlphaBetaWhite.draw(g);
        bChooseNoPruningWhite.draw(g);
    }
    private void drawChooseAiTypeButtonsBlack(Graphics g){
        bChooseAlphaBetaBlack.draw(g);
        bChooseNoPruningBlack.draw(g);
    }
    private void drawChooseTranspositionTable(Graphics g){
        bUseTranspositionTable.draw(g);
        bDontUseTranspositionTable.draw(g);
    }
    @Override
    public void mouseMoved(int x, int y) {
        bNextMove.setMouseOver(false);
        bMenu.setMouseOver(false);
        bReset.setMouseOver(false);
        bChooseAlphaBetaWhite.setMouseOver(false);
        bChooseNoPruningWhite.setMouseOver(false);
        bChooseAlphaBetaBlack.setMouseOver(false);
        bChooseNoPruningBlack.setMouseOver(false);
        bUseTranspositionTable.setMouseOver(false);
        bDontUseTranspositionTable.setMouseOver(false);
        bIncreaseDepth.setMouseOver ( false );
        bDecreaseDepth.setMouseOver ( false );


        if (bMenu.getBounds().contains(x, y))
            bMenu.setMouseOver(true);
        else if (bReset.getBounds().contains(x, y))
            bReset.setMouseOver(true);
        else if (bChooseNoPruningBlack.getBounds().contains(x, y))
            bChooseNoPruningBlack.setMouseOver(true);
        else if (bChooseAlphaBetaBlack.getBounds().contains(x, y))
            bChooseAlphaBetaBlack.setMouseOver(true);
        else if (bNextMove.getBounds().contains(x, y))
            bNextMove.setMouseOver(true);
        else if (bIncreaseDepth.getBounds ().contains ( x, y ))
            bIncreaseDepth.setMouseOver ( true );
        else if (bDecreaseDepth.getBounds ().contains ( x, y ))
            bDecreaseDepth.setMouseOver ( true );


        if (bChooseNoPruningWhite.getBounds().contains(x, y))
            bChooseNoPruningWhite.setMouseOver(true);
        else if (bChooseAlphaBetaWhite.getBounds().contains(x, y))
            bChooseAlphaBetaWhite.setMouseOver(true);

        if (bDontUseTranspositionTable.getBounds().contains(x, y))
            bDontUseTranspositionTable.setMouseOver(true);
        else if (bUseTranspositionTable.getBounds().contains(x, y))
            bUseTranspositionTable.setMouseOver(true);

    }

    @Override
    public void mouseClicked(int x, int y) {

        if (!blackChosen) {
            chooseBlackAiType(x, y);
            return;
        }else if(!whiteChosen){
            chooseWhiteAiType(x,y);
            return;
        }else if(!transpositionTableChosen){
            chooseTranspositionTable(x,y);
            return;
        }


        // check if clicked management buttons
        if (bMenu.getBounds().contains(x, y)) {
            resetGame();
            SetGameState(MENU);
            return;
        } else if (bReset.getBounds().contains(x, y)) {
            resetGame();
            return;
        }
        else if(bNextMove.getBounds().contains(x,y)&&!gameWon){
            makeNextMove();
            return;
        }else if (bIncreaseDepth.getBounds ().contains ( x, y )){
            increaseDepth();
        } else if (bDecreaseDepth.getBounds ().contains ( x, y )){
            decreaseDepth();
        }


    }

    @Override
    public void mousePressed(int x, int y) {
        if (bMenu.getBounds().contains(x, y))
            bMenu.setMousePressed(true);
        else if (bReset.getBounds().contains(x, y))
            bReset.setMousePressed(true);
        else if (bChooseNoPruningWhite.getBounds().contains(x, y))
            bChooseNoPruningWhite.setMousePressed(true);
        else if (bChooseAlphaBetaWhite.getBounds().contains(x, y))
            bChooseAlphaBetaWhite.setMousePressed(true);
        else if (bNextMove.getBounds().contains(x, y))
            bNextMove.setMousePressed(true);
        else if (bIncreaseDepth.getBounds ().contains ( x, y ))
            bIncreaseDepth.setMousePressed ( true );
        else if (bDecreaseDepth.getBounds ().contains ( x, y ))
            bDecreaseDepth.setMousePressed ( true );

        if (bChooseNoPruningBlack.getBounds().contains(x, y))
            bChooseNoPruningBlack.setMousePressed(true);
        else if (bChooseAlphaBetaBlack.getBounds().contains(x, y))
            bChooseAlphaBetaBlack.setMousePressed(true);

        if (bDontUseTranspositionTable.getBounds().contains(x, y))
            bDontUseTranspositionTable.setMousePressed(true);
        else if (bUseTranspositionTable.getBounds().contains(x, y))
            bUseTranspositionTable.setMousePressed(true);

    }

    @Override
    public void mouseReleased(int x, int y) {
        resetButtons();
    }
    private void chooseWhiteAiType(int x, int y){
        if(bChooseAlphaBetaWhite.getBounds().contains(x,y)){
            whiteAiType= new AlphaBetaMinMaxAi();
            whiteChosen=true;
        }
        else if(bChooseNoPruningWhite.getBounds().contains(x,y)){
            whiteAiType=new ClassicMinMaxAi();
            whiteChosen=true;
        }
    }
    private void chooseTranspositionTable(int x, int y){
        if(bUseTranspositionTable.getBounds().contains(x,y)){
            useTranspositionTable=true;
            transpositionTableChosen=true;
        }else if(bDontUseTranspositionTable.getBounds().contains(x,y)){
            useTranspositionTable=false;
            transpositionTableChosen=true;
        }
    }
    private void chooseBlackAiType(int x, int y){
        if(bChooseAlphaBetaBlack.getBounds().contains(x,y)){
            blackAiType=new AlphaBetaMinMaxAi();
            blackChosen=true;
        }
        else if(bChooseNoPruningBlack.getBounds().contains(x,y)){
            blackAiType=new ClassicMinMaxAi();
            blackChosen=true;
        }
    }

    private void makeNextMove(){
        makeAiMove(turn);
    }

    private void increaseDepth () {
        MAX_DEPTH= (byte) Math.min ( MAX_DEPTH+1, 4 );
    }
    private void decreaseDepth () {
        MAX_DEPTH = (byte) Math.max ( MAX_DEPTH-1, 0 );
    }

    private void resetButtons() {
        bChooseAlphaBetaBlack.resetBooleans();
        bChooseAlphaBetaWhite.resetBooleans();
        bChooseNoPruningBlack.resetBooleans();
        bChooseNoPruningWhite.resetBooleans();
        bMenu.resetBooleans();
        bReset.resetBooleans();
        bNextMove.resetBooleans();
        bUseTranspositionTable.resetBooleans();
        bDontUseTranspositionTable.resetBooleans();
    }
}
