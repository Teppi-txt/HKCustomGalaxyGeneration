package interface_adapters;
import entities.PlayerState;
import entities.PlayerStateEffect;
import java.util.ArrayList;

import entities.ObtainOption;

public interface IObtainable {
    public boolean canObtain(PlayerState state);
    public ArrayList<ObtainOption> getDependencies();
    public String getName();
    public PlayerStateEffect getMinimalEffect(PlayerState state);
    public boolean isObtained(PlayerState ps);
}