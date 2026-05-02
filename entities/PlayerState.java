package entities;

public class PlayerState {
    public int geo_spent = 0;
    public int essence = 0;
    public int grubs_rescued = 0;
    public int mask_shard = 0;
    public int vessel_frag = 0;
    public int pale_ore = 0;

    public PlayerState(){
        
    }

    public PlayerState(int geo_spent, int essence, int grubs_rescued,
                   int mask_shard, int vessel_frag, int pale_ore) {
        this.geo_spent = geo_spent;
        this.essence = essence;
        this.grubs_rescued = grubs_rescued;
        this.mask_shard = mask_shard;
        this.vessel_frag = vessel_frag;
        this.pale_ore = pale_ore;
    }

    boolean isGreaterThan(PlayerState obtainedAtState) {
        return this.geo_spent >= obtainedAtState.geo_spent &&
                this.essence >= obtainedAtState.essence &&
                this.grubs_rescued >= obtainedAtState.grubs_rescued &&
                this.mask_shard >= obtainedAtState.mask_shard &&
                this.vessel_frag >= obtainedAtState.vessel_frag &&
                this.pale_ore >= obtainedAtState.pale_ore;
    }

    @Override
    public String toString() {
        return "PlayerState{" +
            "geo_spent=" + geo_spent +
            ", essence=" + essence +
            ", grubs_rescued=" + grubs_rescued +
            ", mask_shard=" + mask_shard +
            ", vessel_frag=" + vessel_frag +
            ", pale_ore=" + pale_ore +
            '}';
    }
}