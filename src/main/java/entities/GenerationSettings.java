package entities;

public class GenerationSettings {

    private boolean majorAbilities;
    private boolean geoLimits;

    private boolean customCenter;
    private String centerGoal;

    public static double INCREASED_MAJOR_CHANCE = 0.15;

    private boolean singleSaveLines;

    public GenerationSettings() {
    }

    public GenerationSettings(boolean majorAbilities, boolean geoLimits, boolean customCenter,
                              String centerGoal, boolean singleSaveLines) {
        this.majorAbilities = majorAbilities;
        this.geoLimits = geoLimits;
        this.customCenter = customCenter;
        this.centerGoal = centerGoal;
        this.singleSaveLines = singleSaveLines;
    }

    public boolean isMajorAbilities() {
        return majorAbilities;
    }

    public void setMajorAbilities(boolean majorAbilities) {
        this.majorAbilities = majorAbilities;
    }

    public boolean isGeoLimits() {
        return geoLimits;
    }

    public void setGeoLimits(boolean geoLimits) {
        this.geoLimits = geoLimits;
    }

    public boolean useCustomCenter() {
        return customCenter;
    }

    public void setCustomCenter(boolean customCenter) {
        this.customCenter = customCenter;
    }

    public String getCenterGoal() {
        return centerGoal;
    }

    public void setCenterGoal(String centerGoal) {
        this.centerGoal = centerGoal;
    }

    public boolean isSingleSaveLines() {
        return singleSaveLines;
    }

    public void setSingleSaveLines(boolean singleSaveLines) {
        this.singleSaveLines = singleSaveLines;
    }
}