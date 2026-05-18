package entities;

import interface_adapters.Obtainable;

import java.util.ArrayList;
import java.util.Objects;

public class MilestoneGoal extends Obtainable {
    private String name;

    private String milestoneType;
    private int milestone;

    public MilestoneGoal(String name, String type, int count) {
        this.name = name;
        this.milestoneType = type;
        this.milestone = count;
    }

    public boolean isObtained(PlayerState state) {
        if (Objects.equals(milestoneType, "grubs")) {
            return state.grubs_rescued >= milestone;
        } else if (Objects.equals(milestoneType, "geo")) {
            return state.geo_spent >= milestone;
        } else if (Objects.equals(milestoneType, "tolls")) {
            return state.tolls_unlocked >= milestone;
        }
        return false;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public ArrayList<ObtainOption> getDependencies() {
        return new ArrayList<>();
    }
}
