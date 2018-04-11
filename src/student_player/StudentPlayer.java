package student_player;

import java.util.Random;

import boardgame.Move;
import boardgame.Player;
import tablut.GreedyTablutPlayer;
import tablut.RandomTablutPlayer;
import tablut.TablutBoardState;
import tablut.TablutMove;
import tablut.TablutPlayer;

/** A player file submitted by a student. */
public class StudentPlayer extends TablutPlayer {

    /**
     * You must modify this constructor to return your student number. This is
     * important, because this is what the code that runs the competition uses to
     * associate you with your agent. The constructor should do nothing else.
     */
    public StudentPlayer() {
        super("260614908");
    }

    /**
     * This is the primary method that you need to implement. The ``boardState``
     * object contains the current state of the game, which your agent must use to
     * make decisions.
     */
    public Move chooseMove(TablutBoardState boardState) {

    	/*
    	 * Using MiniMax with Alpha-Beta Pruning to choose a move
    	 * Implementation of the algorithm is in AlphaBetaPruning class
    	 */
    	AlphaBetaPruning abpMiniMax=new AlphaBetaPruning();
    	TablutBoardState cloneBs=(TablutBoardState)boardState.clone();
    	if(boardState.getTurnNumber()==0||boardState.getTurnNumber()==-1){
    		abpMiniMax.alphaBetaPruning(4, cloneBs, Integer.MIN_VALUE, Integer.MAX_VALUE);
    	}else{
    		abpMiniMax.alphaBetaPruning(3, cloneBs, Integer.MIN_VALUE, Integer.MAX_VALUE);
    	}
    	//abpMiniMax.alphaBetaPruning(3, cloneBs, Integer.MIN_VALUE, Integer.MAX_VALUE);
    	Move myMove=abpMiniMax.getMove();

        // Return your move to be processed by the server.
        return myMove;
    }
    
    // For debug only
    public static void main(String[] args) {
        TablutBoardState b = new TablutBoardState();
        Player swede = new StudentPlayer();
        swede.setColor(TablutBoardState.SWEDE);

        // Player swede = new RandomTablutPlayer("RandomSwede");
        // swede.setColor(TablutBoardState.SWEDE);
        //
        Player muscovite = new RandomTablutPlayer();
        muscovite.setColor(TablutBoardState.MUSCOVITE);
        //((GreedyTablutPlayer) muscovite).rand = new Random(4);

        // Player muscovite = new RandomTablutPlayer("RandomMuscovite");
        // muscovite.setColor(TablutBoardState.MUSCOVITE);

        Player player = muscovite;
        while (!b.gameOver()) {
            Move m = player.chooseMove(b);
            b.processMove((TablutMove) m);
            player = (player == muscovite) ? swede : muscovite;
            System.out.println("\nMOVE PLAYED: " + m.toPrettyString());
            b.printBoard();
        }
        System.out.println(TablutMove.getPlayerName(b.getWinner()) + " WIN!");
    }
}