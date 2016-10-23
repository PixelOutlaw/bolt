/**
 * The MIT License
 * Copyright (c) 2015 Teal Cube Games
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package info.faceland.bolt;

import com.tealcube.minecraft.bukkit.TextUtils;
import com.tealcube.minecraft.bukkit.shade.google.common.collect.Sets;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import se.ranzdo.bukkit.methodcommand.Arg;
import se.ranzdo.bukkit.methodcommand.Command;

import java.util.ArrayList;
import java.util.List;

public class BoltCommand {

    @Command(identifier = "locks add", permissions = "bolt.add")
    public void addSubcommand(Player sender, @Arg(name = "target") String target) {
        Block b = sender.getTargetBlock(Sets.newHashSet(Material.AIR), 10);
        if (b == null || !(b.getState() instanceof Chest)) {
            sender.sendMessage(ChatColor.RED + "Add failed. That block is not a chest.");
            return;
        }
        Chest chest = (Chest) b.getState();
        if (!BoltAPI.isChestOwner(chest.getInventory(), sender.getName())) {
            sender.sendMessage(ChatColor.RED + "Add failed. You do not own that chest.");
            return;
        }
        List<String> allowed = BoltAPI.getAllowedUsers(chest.getInventory());
        if (allowed.size() >= 8) {
            sender.sendMessage(ChatColor.RED + "This chest already has the maximum amount of people added!");
            return;
        }
        allowed.add(target.length() > 16 ? target.substring(0, 15) : target);
        BoltAPI.setAllowedUsers(chest.getInventory(), allowed);
        sender.sendMessage(
                ChatColor.GREEN + "Success! " + ChatColor.WHITE + target + ChatColor.GREEN +
                        " can now use this chest.");
    }

    @Command(identifier = "locks remove", permissions = "bolt.remove")
    public void removeSubcommand(Player sender, @Arg(name = "target") String target) {
        Block b = sender.getTargetBlock(Sets.newHashSet(Material.AIR), 10);
        if (b == null || !(b.getState() instanceof Chest)) {
            sender.sendMessage(ChatColor.RED + "Remove failed. That block is not a chest.");
            return;
        }
        Chest chest = (Chest) b.getState();
        if (!BoltAPI.isChestOwner(chest.getInventory(), sender.getName())) {
            sender.sendMessage(ChatColor.RED + "Remove failed. You do not own that chest.");
            return;
        }
        List<String> allowed = BoltAPI.getAllowedUsers(chest.getInventory());
        allowed.remove(target.length() > 16 ? target.substring(0, 15) : target);
        BoltAPI.setAllowedUsers(chest.getInventory(), allowed);
        sender.sendMessage(
                ChatColor.GREEN + "You removed " + ChatColor.WHITE + target + ChatColor.GREEN +
                        "'s access to this chest.");
    }

    @Command(identifier = "locks makenormal", permissions = "bolt.makenormal")
    public void makeNormalSubcommand(Player sender) {
        Block b = sender.getTargetBlock(Sets.newHashSet(Material.AIR), 10);
        if (b == null || !(b.getState() instanceof Chest)) {
            sender.sendMessage(ChatColor.RED + "You cannot make that normal.");
            return;
        }
        Chest chest = (Chest) b.getState();
        if (!BoltAPI.isChestOwner(chest.getInventory(), sender.getName())) {
            sender.sendMessage(ChatColor.RED + "You cannot make that normal.");
            return;
        }
        chest.getInventory().setItem(chest.getInventory().getSize() - 1, null);
        sender.sendMessage(ChatColor.GREEN + "You made the chest normal.");
    }

    @Command(identifier = "locks setowner", permissions = "bolt.setowner")
    public void setOwnerSubcommand(Player sender, @Arg(name = "OldOwner") String oldOwner, @Arg(name = "NewOwner")
            String newOwner, @Arg(name = "Radius") int radius) {
        Chest chest;
        int changed = 0;
        if (radius > 15) {
            sender.sendMessage(TextUtils.color("&cMaximum radius is &f15&c! Are you tryin' to break shit? ._."));
            radius = 15;
        }
        sender.sendMessage(TextUtils.color("&aScanning in radius &f" + radius + " &afor chests to convert..."));
        for (Block bloc : getBlocks(sender.getLocation().getBlock(), radius)) {
            if (bloc.getType() != Material.CHEST && bloc.getType() != Material.TRAPPED_CHEST) {
                continue;
            }
            chest = (Chest) bloc.getState();
            if (BoltAPI.isChestOwner(chest.getInventory(), oldOwner)) {
                BoltAPI.setChestOwner(chest.getInventory(), newOwner);
                changed++;
            }
        }
        sender.sendMessage(TextUtils.color("&aChanged ownership of &f" + changed + " &achests from &e" + oldOwner +
                " &ato &e" + newOwner + "&a!"));
    }

    private List<Block> getBlocks(Block start, int radius){
        if (radius <= 0) {
            return new ArrayList<>(0);
        }
        int iterations = (radius * 2) + 1;
        List<Block> blocks = new ArrayList<>(iterations * iterations * iterations);
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    blocks.add(start.getRelative(x, y, z));
                }
            }
        }
        return blocks;
    }
}
