package interface_adapters;
import entities.PlayerState;
import entities.PlayerStateEffect;
import java.util.ArrayList;

import entities.ObtainOption;

public interface IObtainable {
    public ArrayList<ObtainOption> getDependencies();
    public String getName();
}