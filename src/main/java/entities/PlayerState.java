package entities;

import java.util.ArrayList;
import java.util.HashSet;

import interface_adapters.Obtainable;

public class PlayerState {
    public int geo_spent = 0;
    public int essence = 0;
    public int grubs_rescued = 0;
    public int tolls_unlocked = 0;

    public ArrayList<Objective> objectives = new ArrayList<>();
    public ArrayList<Obtainable> all_obtained = new ArrayList<>();

    public PlayerState(){
        
    }

    public PlayerState(int geo_spent, int essence, int grubs_rescued, int tolls_unlocked) {
        this.geo_spent = geo_spent;
        this.essence = essence;
        this.grubs_rescued = grubs_rescued;
        this.tolls_unlocked = tolls_unlocked;
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

    public void obtain(Obtainable o) {
        if (o instanceof Objective) {
            this.objectives.add((Objective) o);
        }
        this.all_obtained.add(o);
    }

    public boolean hasItems(HashSet<Obtainable> deps) {
        for (Obtainable dep : deps) {
            if (!all_obtained.contains(dep)) {
                return false;
            }
        }
        return true;
    }
}