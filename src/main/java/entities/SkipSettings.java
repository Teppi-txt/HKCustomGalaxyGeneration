package entities;

import java.util.ArrayList;
import java.util.List;

public class SkipSettings {

    private List<String> settings;
    private boolean darkrooms;

    public SkipSettings() {
        settings = new ArrayList<>();
    }

    public List<String> getSettings() {
        return settings;
    }

    public void addSetting(String setting) {
        this.settings.add(setting);
    }

    public boolean hasDarkrooms() {
        return darkrooms;
    }

    public void setDarkrooms(boolean darkroomsOn) {
        this.darkrooms = darkroomsOn;
    }
}