package Maze;

import java.awt.Dimension;
import java.util.*;

public class GreedyBestFirstSearchEngine extends AbstractSearchEngine {
    public GreedyBestFirstSearchEngine(int width, int height) {
        super(width, height);
    }

    public void search() {
        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingInt(Node::getH));
        Set<Dimension> closedSet = new HashSet<>();

        Node startNode = new Node(startLoc, null, heuristic(startLoc));
        openSet.add(startNode);

        while (!openSet.isEmpty()) {
            Node currentNode = openSet.poll();
            currentLoc = currentNode.location;

            if (equals(currentLoc, goalLoc)) {
                reconstructPath(currentNode);
                return;
            }

            closedSet.add(currentLoc);
            for (Dimension move : getPossibleMoves(currentLoc)) {
                if (move != null && !closedSet.contains(move)) {
                    Node neighborNode = new Node(move, currentNode, heuristic(move));

                    if (!openSet.contains(neighborNode)) {
                        openSet.add(neighborNode);
                    }
                }
            }
        }
    }

    private int heuristic(Dimension loc) {
        // Manhattan distance heuristic
        return Math.abs(loc.width - goalLoc.width) + Math.abs(loc.height - goalLoc.height);
    }

    private void reconstructPath(Node node) {
        maxDepth = 0;
        while (node != null) {
            searchPath[maxDepth++] = node.location;
            node = node.parent;
        }
    }

    private static class Node {
        Dimension location;
        Node parent;
        int h; // Heuristic cost

        Node(Dimension location, Node parent, int h) {
            this.location = location;
            this.parent = parent;
            this.h = h;
        }

        int getH() {
            return h;
        }
    }
}
