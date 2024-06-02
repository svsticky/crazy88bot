package nl.svsticky.crazy88.config.model;

import dev.array21.classvalidator.annotations.Required;

import java.util.HashMap;

public class LocationModel {
    @Required
    public String geoLong;
    @Required
    public String geoLat;
    @Required
    public HashMap<Integer, String> assignments;
}
