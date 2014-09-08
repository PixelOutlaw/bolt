package info.faceland.bolt;

import info.faceland.api.FacePlugin;
import info.faceland.facecore.shade.command.CommandHandler;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;

public class BoltPlugin extends FacePlugin {

    @Override
    public void preEnable() {

    }

    @Override
    public void enable() {
        Bukkit.getPluginManager().registerEvents(new BoltListener(this), this);
        CommandHandler commandHandler = new CommandHandler(this);
        commandHandler.registerCommands(new BoltCommand());
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
