package entities;

public class PlayerStateEffect {
    int grubs_collected = 0;
    int geo_spent = 0;
    int mask_shard = 0;
    int vessel_frag = 0;
    int pale_ore = 0;

    public PlayerStateEffect() {

    }

    public PlayerStateEffect(int grubs_collected, int geo_spent, int mask_shard,
                             int vessel_frag, int pale_ore) {
        this.grubs_collected = grubs_collected;
        this.geo_spent = geo_spent;
        this.mask_shard = mask_shard;
        this.vessel_frag = vessel_frag;
        this.pale_ore = pale_ore;
    }

    public void setGrubsCollected(int grubs_collected) {
        this.grubs_collected = grubs_collected;
    }

    public void setGeoSpent(int geo_spent) {
        this.geo_spent = geo_spent;
    }

    public void setMaskShard(int mask_shard) {
        this.mask_shard = mask_shard;
    }

    public void setVesselFrag(int vessel_frag) {
        this.vessel_frag = vessel_frag;
    }

    public void setPaleOre(int pale_ore) {
        this.pale_ore = pale_ore;
    }

    public void applyToState(PlayerState state) {
        state.geo_spent += this.geo_spent;
        state.grubs_rescued += this.grubs_collected;
        state.mask_shard += this.mask_shard;
        state.vessel_frag += this.vessel_frag;
        state.pale_ore += this.pale_ore;
    }
}