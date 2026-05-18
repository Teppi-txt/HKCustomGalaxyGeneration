package entities;

import interface_adapters.Obtainable;

import java.util.ArrayList;

public class PlayerData {
    private ArrayList<Obtainable> line;
    private GoalPool goalPool;
    public String name;

    public PlayerData(String name) {
        line = new ArrayList<>();
        goalPool = new GoalPool();
        this.name = name;
    }

    public ArrayList<Obtainable> getLine() {
        return line;
    }

    public boolean add(Obtainable ob) {
        line.add(ob);
        return true;
    }

    public GoalPool getGoalPool() {
        return goalPool;
    }

    public void setGoalPool(ArrayList<Obtainable> goals) {
        this.goalPool = new GoalPool(goals);
    }
}
