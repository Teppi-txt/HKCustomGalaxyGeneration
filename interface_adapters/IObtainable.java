package interface_adapters;
import entities.PlayerState;
import entities.PlayerStateEffect;
import java.util.ArrayList;
import java.util.HashSet;

public interface IObtainable {
    public boolean canObtain(PlayerState state);
    public ArrayList<HashSet<IObtainable>> getDependencies();
    public String getName();
    public PlayerStateEffect getEffects();
    public boolean isObtained(PlayerState ps);
}