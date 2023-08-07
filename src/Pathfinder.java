
import java.lang.IllegalArgumentException;

/**
 * Pathfinder uses A* search to find a near optimal path
 * between to locations with given terrain.
 */

public class Pathfinder {
    MinPQ<PFNode> pq;
    Stack<Coord> path;
    int N, NodeCount;
    Coord max, min;
    Coord start, end = null;
    float heuristic;
    boolean[][] array;
    Terrain t;
    boolean found = false;
    PFNode endNode;
    /**
     * PFNode will be the key for MinPQ (used in computePath())
     *
     * 1. identify possible paths
     * 2. Determine cost to move
     * 3. add them to the PriorityQ
     * 4. dequeue top and go back to step 1
     */
    private class PFNode implements Comparable<PFNode> {
        // loc: the location of the PFNode
        // fromNode: how did we get here? (linked list back to start)
        Coord currentTile;
        PFNode prevNode;
        float cost;
        public PFNode(Coord loc, PFNode fromNode) {
            currentTile = loc;
            prevNode = fromNode;
            cost = -1;
        }

        // compares this with that, used to find minimum cost PFNode
        public int compareTo(PFNode that) {
            if (this.cost > that.cost) {
                return 1;
            }
            else if (this.cost < that.cost) {
                return -1;
            }
            return 0;
        }

        // returns the cost to travel from starting point to this
        // via the fromNode chain
        public float getCost(float heuristic) {
            // cost(start, neighbor) + travelCost(neighbor, this) + (heuristic * computeDistance(this, end))
            // recursive all the way back to start
            if (this.prevNode != null) {
                this.cost = this.prevNode.cost
                        + t.computeTravelCost(this.prevNode.currentTile, this.currentTile)
                        + (heuristic * t.computeDistance(this.currentTile, getPathEnd()));
                return this.cost;
            } else {
                return 0;
            }
        }
    }

    public Pathfinder(Terrain terrain) {
        t = terrain;
        N = t.getN();
        max = new Coord(N,N);
        min = new Coord(0,0);
        // secondary data structure to keep track of where I have already checked.
        array = new boolean[N][N];
    }

    public void setPathStart(Coord loc) {
        start = loc;
    }

    public Coord getPathStart() { return start; }

    public void setPathEnd(Coord loc) {
        end = loc;
    }

    public Coord getPathEnd() {
        return end;
    }

    public void setHeuristic(float v) {
        heuristic = v;
    }

    public float getHeuristic() {
        return heuristic;
    }

    public void resetPath() {
        //System.out.println("Reset path");
        array = new boolean[N][N];
        found = false;
        NodeCount = 0;

    }

    public void computePath() {
        // sets start node and adds to queue
        pq = new MinPQ<>();
        PFNode s = new PFNode(getPathStart(), null);
        NodeCount++;
        pq.insert(s);
        array[getPathStart().getI()][getPathStart().getJ()] = true;

        if (start == null) {
            throw new IllegalArgumentException("Error: Start has not been set.");
        }
        else if (end == null) {
            throw new IllegalArgumentException("Error: End has not been set.");
        }
        // while priority Q is not empty, dequeue the first item and process it.
        while(!foundPath()) {
            // logic for checking surrounding tiles and then using getCost() to determine which direction to make a
            // new PFNode for.
            PFNode nextTile = pq.delMin();
            if (nextTile.currentTile.equals(end)) {
                nextTile.getCost(heuristic);
                endNode = nextTile;
                for (path = new Stack<>(); nextTile.prevNode != null; nextTile = nextTile.prevNode) {
                    path.push(nextTile.currentTile);
                }
                found = true;
                continue;
            }
            // above of current tile
            if (new Coord(nextTile.currentTile.add(-1,0).getI(), nextTile.currentTile.getJ()).isInBounds(min, max)
                    && !array[nextTile.currentTile.add(-1,0).getI()][nextTile.currentTile.getJ()]) {
                //System.out.println("Above created");
                PFNode above = new PFNode(new Coord(nextTile.currentTile.add(-1,0).getI(), nextTile.currentTile.getJ()), nextTile);
                above.getCost(heuristic);
                array[nextTile.currentTile.add(-1,0).getI()][nextTile.currentTile.getJ()] = true;
                pq.insert(above);
                NodeCount++;
            }
            // below of current tile
            if (new Coord(nextTile.currentTile.add(1,0).getI(), nextTile.currentTile.getJ()).isInBounds(min,max)
                    && !array[nextTile.currentTile.add(1,0).getI()][nextTile.currentTile.getJ()]) {
                PFNode below = new PFNode(new Coord(nextTile.currentTile.add(1,0).getI(), nextTile.currentTile.getJ()), nextTile);
                below.getCost(heuristic);
                array[nextTile.currentTile.add(1,0).getI()][nextTile.currentTile.getJ()] = true;
                pq.insert(below);
                NodeCount++;
            }
            // left current tile
            if (new Coord(nextTile.currentTile.getI(), nextTile.currentTile.add(0,-1).getJ()).isInBounds(min,max)
                    && !array[nextTile.currentTile.getI()][nextTile.currentTile.add(0,-1).getJ()]) {
                PFNode left = new PFNode(new Coord(nextTile.currentTile.getI(), nextTile.currentTile.add(0,-1).getJ()), nextTile);
                array[nextTile.currentTile.getI()][nextTile.currentTile.add(0,-1).getJ()] = true;
                left.getCost(heuristic);
                pq.insert(left);
                NodeCount++;
            }
            // right current tile
            if (new Coord(nextTile.currentTile.getI(), nextTile.currentTile.add(0,1).getJ()).isInBounds(min,max)
                    && !array[nextTile.currentTile.getI()][nextTile.currentTile.add(0,1).getJ()]) {
                PFNode right = new PFNode(new Coord(nextTile.currentTile.getI(), nextTile.currentTile.add(0,1).getJ()), nextTile);
                array[nextTile.currentTile.getI()][nextTile.currentTile.add(0,1).getJ()] = true;
                right.getCost(heuristic);
                pq.insert(right);
                NodeCount++;
            }
        }
    }

    public boolean foundPath() {
        return found;
    }

    public float getPathCost() {
        return endNode.cost;
    }

    public int getSearchSize() { return NodeCount; }

    public Iterable<Coord> getPathSolution() {
        return path;
    }

    public boolean wasSearched(Coord loc) {return array[loc.getI()][loc.getJ()];
    }
}
