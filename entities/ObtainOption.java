package entities;

import interface_adapters.IObtainable;
import java.util.HashSet;

public class ObtainOption {
    private HashSet<IObtainable> dependencies;
    private PlayerStateEffect effect;
}