package nl.svsticky.crazy88.config.model;

import dev.array21.classvalidator.annotations.Required;

import java.util.HashMap;

public class LocationModel {
    @Required
    public String name;
    @Required
    public String geoLat;
    @Required
    public String geoLong;
    /**
     * A map of the assignments.
     * The keys must be > 0 and must be continuous
     */
    @Required
    public HashMap<Integer, String> assignments;
}
