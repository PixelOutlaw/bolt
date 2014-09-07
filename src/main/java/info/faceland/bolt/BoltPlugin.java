package info.faceland.bolt;

import info.faceland.api.FacePlugin;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;

public class BoltPlugin extends FacePlugin {

    @Override
    public void preEnable() {

    }

    @Override
    public void enable() {
        Bukkit.getPluginManager().registerEvents(new BoltListener(this), this);
    }

    @Override
    public void postEnable() {

    }

    @Override
    public void preDisable() {

    }

    @Override
    public void disable() {
        HandlerList.unregisterAll(this);
    }

    @Override
    public void postDisable() {

    }

}
