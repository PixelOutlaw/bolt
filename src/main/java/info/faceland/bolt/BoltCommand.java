package info.faceland.bolt;

import info.faceland.facecore.shade.command.Arg;
import info.faceland.facecore.shade.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;

import java.util.List;

public class BoltCommand {

    @Command(identifier = "locks add", permissions = "bolt.add")
    public void addSubcommand(Player sender, @Arg(name = "target") OfflinePlayer target) {
        Block b = sender.getTargetBlock(null, 10);
        if (b == null || !(b.getState() instanceof Chest)) {
            sender.sendMessage(ChatColor.RED + "You cannot add to that.");
            return;
        }
        Chest chest = (Chest) b.getState();
        if (!BoltAPI.isChestOwner(chest, sender)) {
            sender.sendMessage(ChatColor.RED + "You cannot add to that.");
            return;
        }
        List<String> allowed = BoltAPI.getAllowedUsers(chest);
        if (allowed.size() >= 4) {
            sender.sendMessage(ChatColor.RED + "You already have four people added to that chest.");
            return;
        }
        allowed.add(target.getName());
        BoltAPI.setAllowedUsers(chest, allowed);
        sender.sendMessage(
                ChatColor.GREEN + "You added " + ChatColor.WHITE + target.getName() + ChatColor.GREEN +
                        " to your chest.");
    }

    @Command(identifier = "locks remove", permissions = "bolt.remove")
    public void removeSubcommand(Player sender, @Arg(name = "target") OfflinePlayer target) {
        Block b = sender.getTargetBlock(null, 10);
        if (b == null || !(b.getState() instanceof Chest)) {
            sender.sendMessage(ChatColor.RED + "You cannot remove from that.");
            return;
        }
        Chest chest = (Chest) b.getState();
        if (!BoltAPI.isChestOwner(chest, sender)) {
            sender.sendMessage(ChatColor.RED + "You cannot remove from that.");
            return;
        }
        List<String> allowed = BoltAPI.getAllowedUsers(chest);
        allowed.remove(target.getName());
        BoltAPI.setAllowedUsers(chest, allowed);
        sender.sendMessage(
                ChatColor.GREEN + "You removed " + ChatColor.WHITE + target.getName() + ChatColor.GREEN +
                        " from your chest.");
    }

    @Command(identifier = "locks makenormal", permissions = "bolt.makenormal")
    public void makeNormalSubcommand(Player sender) {
        Block b = sender.getTargetBlock(null, 10);
        if (b == null || !(b.getState() instanceof Chest)) {
            sender.sendMessage(ChatColor.RED + "You cannot make that normal.");
            return;
        }
        Chest chest = (Chest) b.getState();
        if (!BoltAPI.isChestOwner(chest, sender)) {
            sender.sendMessage(ChatColor.RED + "You cannot make that normal.");
            return;
        }
        chest.getInventory().setItem(chest.getInventory().getSize() - 1, null);
        sender.sendMessage(ChatColor.GREEN + "You made the chest normal.");
    }

    @Command(identifier = "locks setowner", permissions = "bolt.setowner")
    public void setOwnerSubcommand(Player sender, @Arg(name = "target") OfflinePlayer target) {
        Block b = sender.getTargetBlock(null, 10);
        if (b == null || !(b.getState() instanceof Chest)) {
            sender.sendMessage(ChatColor.RED + "You cannot set an owner.");
            return;
        }
        Chest chest = (Chest) b.getState();
        BoltAPI.setChestOwner(chest, target);
        sender.sendMessage(ChatColor.GREEN + "You made " + ChatColor.WHITE + target.getName() + ChatColor.GREEN +
                                   " the owner of that chest.");
    }

}
