package info.faceland.bolt;

import info.faceland.facecore.shade.command.*;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

public class OfflinePlayerArgumentHandler extends ArgumentHandler<OfflinePlayer> {
    public OfflinePlayerArgumentHandler() {
        setMessage("player_not_exist", "The player %1 does not exist");
        addVariable("sender", "The command executor", new ArgumentVariable<OfflinePlayer>() {
            @Override
            public OfflinePlayer var(CommandSender sender, CommandArgument argument, String varName) throws CommandError {
                if(!(sender instanceof OfflinePlayer))
                    throw new CommandError(argument.getMessage("cant_as_console"));
                return ((OfflinePlayer)sender);
            }
        });
    }
    @Override
    public OfflinePlayer transform(CommandSender sender, CommandArgument argument, String value) throws TransformError {
        OfflinePlayer p = Bukkit.getOfflinePlayer(value);
        if(p == null)
            throw new TransformError(argument.getMessage("player_not_exist", value));
        return p;
    }
}
