package entities;
public class PlayerStateEffect {

    
    int grubs_collected = 0;
    int geo_spent = 0;
    int essence_collected = 0;

    public PlayerStateEffect() {

    }

    public PlayerStateEffect(int grubs_collected, int geo_spent, int essence_collected) {
        this.grubs_collected = grubs_collected;
        this.geo_spent = geo_spent;
        this.essence_collected = essence_collected;
    }

    public void setGrubsCollected(int grubs_collected) {
        this.grubs_collected = grubs_collected;
    }

    public void setGeoSpent(int geo_spent) {
        this.geo_spent = geo_spent;
    }

    public void applyToState(PlayerState state) {
        state.geo_spent += this.geo_spent;
        state.grubs_rescued += this.grubs_collected;
        state.essence += this.essence_collected;
    }

    public static PlayerStateEffect getMinimal(PlayerStateEffect state, PlayerStateEffect state2) {
        if (state.geo_spent < state2.geo_spent) {
            return state;
        } else if (state.essence_collected < state2.essence_collected) {
            return state;
        } else if (state.grubs_collected < state2.grubs_collected) {
            return state;
        }
        return state2;
    }

    public static PlayerStateEffect spend_geo(int i) {
        return new PlayerStateEffect(0, i, 0);
    }

    public static PlayerStateEffect save_grubs(int i) {
        return new PlayerStateEffect(i, 0, 0);
    }

    public static PlayerStateEffect collect_essence(int i) {
        return new PlayerStateEffect(0, 0, i);
    }
}