package entities;

import java.util.ArrayList;
import java.util.HashSet;

import interface_adapters.IObtainable;

public class PlayerState {
    public int geo_spent = 0;
    public int essence = 0;
    public int grubs_rescued = 0;

    public ArrayList<Objective> objectives = new ArrayList<>();
    public ArrayList<IObtainable> all_obtained = new ArrayList<>();

    public PlayerState(){
        
    }

    public PlayerState(int geo_spent, int essence, int grubs_rescued) {
        this.geo_spent = geo_spent;
        this.essence = essence;
        this.grubs_rescued = grubs_rescued;
    }

    boolean isGreaterThan(PlayerState obtainedAtState) {
        return this.geo_spent >= obtainedAtState.geo_spent &&
                this.essence >= obtainedAtState.essence &&
                this.grubs_rescued >= obtainedAtState.grubs_rescued;
    }

    @Override
    public String toString() {
        return "PlayerState{" +
            "geo_spent=" + geo_spent +
            ", essence=" + essence +
            ", grubs_rescued=" + grubs_rescued + "}";
    }

    public ArrayList<Objective> getObjectives() {
        return objectives;
    }

    public void obtain(IObtainable o) {
        if (o instanceof Objective objective) {
            this.objectives.add(objective);
        }
        this.all_obtained.add(o);
    }

    public boolean hasItems(HashSet<IObtainable> deps) {
        for (IObtainable dep : deps) {
            if (!all_obtained.contains(dep)) {
                return false;
            }
        }
        return true;
    }
}