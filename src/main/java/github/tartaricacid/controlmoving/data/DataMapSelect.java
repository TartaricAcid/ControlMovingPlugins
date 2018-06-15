package github.tartaricacid.controlmoving.data;

import com.flowpowered.math.vector.Vector3i;

public class DataMapSelect {
    private Vector3i pos1;
    private Vector3i pos2;
    private String world;
    private int priority;

    public DataMapSelect(Vector3i pos1, Vector3i pos2, String world, int priority) {
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.world = world;
        this.priority = priority;
    }

    public Vector3i getPos1() {
        return pos1;
    }

    public Vector3i getPos2() {
        return pos2;
    }

    public String getWorld() {
        return world;
    }

    public int getPriority() {
        return priority;
    }
}
