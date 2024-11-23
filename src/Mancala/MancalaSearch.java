package Mancala;// public class MancalaSearch extends GameSearch {

//     @Override
//     public boolean drawnPosition(Position p) {
//         MancalaPosition pos = (MancalaPosition) p;
//         return pos.isGameOver() && pos.getScore(0) == pos.getScore(1);
//     }

//     @Override
//     public boolean wonPosition(Position p, boolean player) {
//         MancalaPosition pos = (MancalaPosition) p;
//         int playerScore = pos.getScore(player ? 0 : 1);
//         int opponentScore = pos.getScore(player ? 1 : 0);
//         return pos.isGameOver() && playerScore > opponentScore;
//     }

//     @Override
//     public float positionEvaluation(Position p, boolean player) {
//         MancalaPosition pos = (MancalaPosition) p;
//         int playerScore = pos.getScore(player ? 0 : 1);
//         int opponentScore = pos.getScore(player ? 1 : 0);

//         if ("offensive".equals(getStrategy())) {
//             return playerScore - opponentScore;
//         } else if ("defensive".equals(getStrategy())) {
//             return opponentScore - playerScore;
//         }
//         return playerScore - opponentScore; // Default heuristic
//     }

//     @Override
//     public Position[] possibleMoves(Position p, boolean player) {
//         MancalaPosition pos = (MancalaPosition) p;
//         return pos.getAllPossibleMoves(player);
//     }

//     @Override
//     public Position makeMove(Position p, boolean player, Move move) {
//         MancalaPosition pos = (MancalaPosition) p;
//         MancalaMove mancalaMove = (MancalaMove) move;
//         MancalaPosition newPos = pos.simulateMove(mancalaMove);
//         return newPos;
//     }

//     @Override
//     public boolean reachedMaxDepth(Position p, int depth) {
//         return depth >= getComplexity();
//     }
// }
