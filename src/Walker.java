import java.util.Iterator;

/**
 * Walker takes an Iterable of Coords and simulates an individual
 * walking along the path over the given Terrain
 */
public class Walker {
    private Coord location;
    private Terrain terrain;
    private Iterator<Coord> path;
    private boolean doneWalking = false;
    // terrain: the Terrain the Walker traverses
    // path: the sequence of Coords the Walker follows
    public Walker(Terrain terrain, Iterable<Coord> path) {
        this.terrain = terrain;
        this.path = path.iterator();
        if (this.path.hasNext()) {
            this.location = this.path.next();
        }
    }

    // returns the Walker's current location
    public Coord getLocation() {
        return this.location;
    }

    // returns true if Walker has reached the end Coord (last in path)
    public boolean doneWalking() {
        return doneWalking;
    }

    // advances the Walker along path
    // byTime: how long the Walker should traverse (may be any non-negative value)
    public void advance(float byTime) {
        while (byTime >= 0.0f && this.path.hasNext()) {
            Coord next = this.path.next();
            byTime -= this.terrain.computeTravelCost(this.location, next);
            this.location = next;
            doneWalking = !this.path.hasNext();
        }
    }
}
