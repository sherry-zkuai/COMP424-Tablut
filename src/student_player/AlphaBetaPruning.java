package student_player;

import java.util.ArrayList;
import java.util.Random;

import coordinates.Coord;
import coordinates.Coordinates;
import coordinates.Coordinates.CoordinateDoesNotExistException;
import tablut.TablutBoardState;
import tablut.TablutMove;

public class AlphaBetaPruning {
	
	private TablutMove theMove; // to be returned to StudentPlayer
	
	// Constructor
	public AlphaBetaPruning(){}
	
	/**
	 * MiniMax with alpha-beta pruning to a certain depth
	 * 
	 * @param depth
	 * @param bs
	 * @param alpha
	 * @param beta
	 * @return alpha or beta depends on the player role
	 */
	public int alphaBetaPruning(int depth, TablutBoardState bs,int alpha,int beta){
		// Re-initiate the best move, so that only the move at current depth will be retrieved only
		TablutMove bestMove=null; 
		// The array to store all the best moves at current depth. Since will be re-generated every depth, only the top level moves will be retrieved
		ArrayList<TablutMove> bestMoves=new ArrayList<>();
		int value;
		// Find all legal moves available to current player
		ArrayList<TablutMove> moves=bs.getAllLegalMoves();
		
		/*
		 *  PseudoCode:
		 * int MaxValue(s,a,b)
		 * 	if cutoff(s), return evaluation(s)
		 * 	for each state s' in successors(s)
		 * 		let a=max(a,min(s',a,b))
		 * 		if a>=b return b
		 * 	return a
		 * int MinValue(s,a,b)
		 * 	if cutoff(s), return evaluation(s)
		 * 	for each state s' in successor(s)
		 * 		let b=min(b,max(s',a,b))
		 * 		if a>=b return a
		 * 	return b
		 */
		
		// If the game is over (someone won) or the leaf node is reached, get the value from heuristic
		if(bs.gameOver()||depth==0||moves.isEmpty()){
			return heuristic(bs);
		/* If not at a leaf node:
		 * Find all possible next states, then for each state, find the maxValue of its sucessors if it is Max Player or the MinValue of its sucessors if it is Min Player
		 */
		}else{
			for(TablutMove move:moves){
				TablutBoardState cloneBs=(TablutBoardState)bs.clone(); // Make a copy of the current Tablut board state
				cloneBs.processMove(move); // Find the resulting state through process the move on the copy
				if(bs.getTurnPlayer()==TablutBoardState.SWEDE){ //Swede as Max Player
					value=alphaBetaPruning(depth-1,cloneBs,alpha,beta);
					if(value>alpha){
						alpha=value;
						bestMove=move;
//						bestMoves.clear();
//						bestMoves.add(bestMove);
						if(alpha==100){ // the cutoff is game over
							break;
						}
					}
//					else if(value==alpha){
//						bestMoves.add(move);
//					}
				}else{ // Muscovite as Mini Player
					value=alphaBetaPruning(depth-1,cloneBs,alpha,beta);
					if(value<beta){
						beta=value;
						bestMove=move;
//						bestMoves.clear();
//						bestMoves.add(bestMove);
						if(beta==-100){ // the cutoff is game over
							break;
						}
					}
//					else if(value==alpha){
//						bestMoves.add(move);
//					}
				}
				// cutoff
				if(alpha>=beta){
					break;
				}
			}
			if(bestMove!=null){
				bestMoves.add(bestMove);
			}
			if(!bestMoves.isEmpty()){
				theMove=bestMoves.get(new Random(System.currentTimeMillis()).nextInt(bestMoves.size()));
			}
//			if(bestMove!=null){
//				bestMoves.add(bestMove);
//			}
//			if(!bestMoves.isEmpty()){
//				theMove=bestMoves.get(bestMoves.size()-1);
//			}
//			theMove=(TablutMove) ((bestMove==null)?bs.getRandomMove():bestMove);
			if(bs.getTurnPlayer()==TablutBoardState.SWEDE){
				return alpha;
			}else{
				return beta;
			}
		}
	}
	
	/**
	 * Get the best move
	 * @return theMove
	 */
	public TablutMove getMove(){
		return theMove;
	}
	
	public int heuristic(TablutBoardState bs){
		int sWin=0;
		int mWin=0;
		int toCorner=0;
		int toCaptureK=0;
		if(bs.gameOver()&&bs.getWinner()==TablutBoardState.SWEDE){
			sWin= 100;
		}
		if(bs.gameOver()&&bs.getWinner()==TablutBoardState.MUSCOVITE){
			sWin= -100;
		}
		
		if(sWin!=0&&mWin!=0){
			return (bs.getTurnPlayer()==TablutBoardState.SWEDE)?Math.max(sWin,mWin):Math.min(sWin, mWin); 
		}else{
			if(sWin!=0){
				return sWin;
			}
			if(mWin!=0){
				return mWin;
			}
		}
		for(TablutMove move:bs.getLegalMovesForPosition(bs.getKingPosition())){
			if(Coordinates.isCorner(move.getEndPosition())){
				toCorner= 90;
				break;
			}
		}
		
		int neighboring=100;
		ArrayList<Coord> corners=new ArrayList<>();
		corners.add(Coordinates.get(0, 0));
		corners.add(Coordinates.get(0, 8));
		corners.add(Coordinates.get(8, 0));
		corners.add(Coordinates.get(8, 8));
		ArrayList<Coord> cap=new ArrayList<>(corners);
		cap.add(Coordinates.get(4, 4));
		
		if(!Coordinates.isCenter(bs.getKingPosition())){
			int detNeighbor=0;
			int detAboutToSand=0;
			if(bs.getKingPosition().x==1&&bs.getKingPosition().y==1||bs.getKingPosition().x==1&&bs.getKingPosition().y==7||bs.getKingPosition().x==7&&bs.getKingPosition().y==1||bs.getKingPosition().x==7&&bs.getKingPosition().y==7){
				//for(Coord c:(bs.getTurnPlayer()==TablutBoardState.MUSCOVITE)?bs.getPlayerPieceCoordinates():bs.getOpponentPieceCoordinates()){
					for(TablutMove move:bs.getAllLegalMoves()){
						for(Coord c:cap){
							try{
								if(Coordinates.getSandwichCoord(c, bs.getKingPosition())==move.getEndPosition()){
									toCaptureK=90;
									break;
								}
							}catch (CoordinateDoesNotExistException e){
								
							}
						}

					}
				//}
			}
			for(Coord neighbor:Coordinates.getNeighbors(bs.getKingPosition())){
				for(Coord blackCoord:(bs.getTurnPlayer()==TablutBoardState.MUSCOVITE)?bs.getPlayerPieceCoordinates():bs.getOpponentPieceCoordinates()){
					if(blackCoord.distance(neighbor)==0){
						detNeighbor=1;
						for(Coord other:(bs.getTurnPlayer()==TablutBoardState.MUSCOVITE)?bs.getPlayerPieceCoordinates():bs.getOpponentPieceCoordinates()){
							for(TablutMove move:bs.getLegalMovesForPosition(other)){
								try {
									if(move.getEndPosition().distance(Coordinates.getSandwichCoord(blackCoord, bs.getKingPosition()))==0){
										detAboutToSand=1;
										toCaptureK= -90;
									}
								} catch (CoordinateDoesNotExistException e) {
								}
							}
						}
					}
				}
			}
//			if(detNeighbor==1){
//				neighboring= -1;
//			}
		}
		
		if(toCorner!=0&&toCaptureK!=0){
			return (bs.getTurnPlayer()==TablutBoardState.SWEDE)?Math.max(toCorner,toCaptureK):Math.min(toCaptureK, toCorner);
		}else{
			if(toCorner!=0){
				return toCorner;
			}
			if(toCaptureK!=0){
				return toCaptureK;
			}
		}
		
//		if(bs.getNumberPlayerPieces(TablutBoardState.MUSCOVITE)-bs.getNumberPlayerPieces(TablutBoardState.SWEDE)<=7){
//			return 1;
//		}else{
//			return -1;
//		}
		int kingToCorner=8-Coordinates.distanceToClosestCorner(bs.getKingPosition());
		int diffInPiece=7-bs.getNumberPlayerPieces(TablutBoardState.MUSCOVITE)+bs.getNumberPlayerPieces(TablutBoardState.SWEDE);
		return (bs.getTurnPlayer()==TablutBoardState.SWEDE)?Math.max(kingToCorner,diffInPiece):Math.min(diffInPiece,kingToCorner);
		//return kingToCorner+diffInPiece+neighboring;
		//return diffInPiece;
	}

}
