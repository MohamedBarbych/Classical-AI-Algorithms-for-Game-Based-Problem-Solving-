package Maze;

import java.awt.Dimension;

public class Node {
    Dimension loc;  // Position in the grid (x, y)
    double gCost;   // Cost from start node to this node
    double hCost;   // Heuristic cost from this node to the goal
    double fCost;   // Total cost (gCost + hCost)

    public Node(Dimension loc, double gCost, double hCost) {
        this.loc = loc;
        this.gCost = gCost;
        this.hCost = hCost;
        this.fCost = gCost + hCost;  // Total cost
    }
}
