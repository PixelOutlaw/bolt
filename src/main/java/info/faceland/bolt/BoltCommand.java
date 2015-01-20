/*
 * This file is part of Blight, licensed under the ISC License.
 *
 * Copyright (c) 2014 Richard Harrah
 *
 * Permission to use, copy, modify, and/or distribute this software for any purpose with or without fee is hereby granted,
 * provided that the above copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT,
 * INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF
 * THIS SOFTWARE.
 */
package info.faceland.bolt;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.nunnerycode.kern.methodcommand.Arg;
import org.nunnerycode.kern.methodcommand.Command;

import java.util.List;

public class BoltCommand {

    @Command(identifier = "locks add", permissions = "bolt.add")
    public void addSubcommand(Player sender, @Arg(name = "target") String target) {
        Block b = sender.getTargetBlock(null, 10);
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
        if (allowed.size() >= 4) {
            sender.sendMessage(ChatColor.RED + "You already have four people added to that chest.");
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
        Block b = sender.getTargetBlock(null, 10);
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
        Block b = sender.getTargetBlock(null, 10);
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
    public void setOwnerSubcommand(Player sender, @Arg(name = "target") String target) {
        Block b = sender.getTargetBlock(null, 10);
        if (b == null || !(b.getState() instanceof Chest)) {
            sender.sendMessage(ChatColor.RED + "You cannot set an owner.");
            return;
        }
        Chest chest = (Chest) b.getState();
        BoltAPI.setChestOwner(chest.getInventory(), target.length() > 16 ? target.substring(0, 15) : target);
        sender.sendMessage(ChatColor.GREEN + "You made " + ChatColor.WHITE + target + ChatColor.GREEN +
                                   " the owner of that chest.");
    }

}
