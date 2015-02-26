/*
 * This file is part of Bolt, licensed under the ISC License.
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

import com.tealcube.minecraft.bukkit.facecore.shade.hilt.HiltItemStack;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.*;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class BoltListener implements Listener {

    private final BoltPlugin plugin;

    public BoltListener(BoltPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryMoveItem(InventoryMoveItemEvent event) {
        ItemStack is = event.getItem();
        if (is == null || is.getType() != Material.PAPER) {
            return;
        }
        HiltItemStack his = new HiltItemStack(event.getItem());
        if (his.getName().startsWith(ChatColor.GOLD + "Chest Status:")) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        Block b = event.getBlockPlaced();
        if (event.getBlockPlaced().getState() instanceof Hopper) {
            BlockFace[] check =
                    {BlockFace.UP, BlockFace.DOWN, BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};
            for (BlockFace bf : check) {
                if (b.getRelative(bf).getState() instanceof Chest) {
                    if (!BoltAPI.isChestOwner(((Chest) b.getRelative(bf).getState()).getInventory(),
                                              event.getPlayer().getName())) {
                        event.setCancelled(true);
                        event.getPlayer().sendMessage(ChatColor.YELLOW +
                                                      "You cannot place hoppers next to chests you do not own.");
                        return;
                    }
                } else if (b.getRelative(bf).getState() instanceof DoubleChest) {
                    if (!BoltAPI.isChestOwner(((DoubleChest) b.getRelative(bf).getState()).getInventory(),
                                              event.getPlayer().getName())) {
                        event.setCancelled(true);
                        event.getPlayer().sendMessage(ChatColor.YELLOW +
                                                      "You cannot place hoppers next to chests you do not own.");
                        return;
                    }
                }
            }
        } else if (event.getBlockPlaced().getState() instanceof Chest) {
            BlockFace[] check =
                    {BlockFace.UP, BlockFace.DOWN, BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};
            for (BlockFace bf : check) {
                if (b.getRelative(bf).getState() instanceof Chest) {
                    if (!BoltAPI.isChestOwner(((Chest) b.getRelative(bf).getState()).getInventory(),
                                              event.getPlayer().getName())) {
                        event.setCancelled(true);
                        event.getPlayer().sendMessage(ChatColor.YELLOW +
                                                      "You cannot place chests next to chests you do not own.");
                        return;
                    }
                } else if (b.getRelative(bf).getState() instanceof DoubleChest) {
                    if (!BoltAPI.isChestOwner(((DoubleChest) b.getRelative(bf).getState()).getInventory(),
                                              event.getPlayer().getName())) {
                        event.setCancelled(true);
                        event.getPlayer().sendMessage(ChatColor.YELLOW +
                                                      "You cannot place chests next to chests you do not own.");
                        return;
                    }
                }
            }
            Chest chest = (Chest) event.getBlockPlaced().getState();
            HiltItemStack hiltItemStack = new HiltItemStack(Material.PAPER);
            hiltItemStack.setName(ChatColor.GOLD + "Chest Status: " + ChatColor.RED + "Locked");
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.WHITE + "<Click to Toggle>");
            lore.add(ChatColor.GOLD + "Owner: " + ChatColor.WHITE + event.getPlayer().getName());
            List<String> allowedUsers = BoltAPI.getAllowedUsers(chest.getInventory());
            if (allowedUsers.size() > 0) {
                for (String s : allowedUsers) {
                    lore.add(ChatColor.GRAY + s);
                }
            } else {
                lore.add(ChatColor.GRAY + "Type /add <playername> while looking at");
                lore.add(ChatColor.GRAY + "this chest to allow people to use it.");
            }
            hiltItemStack.setLore(lore);
            chest.getInventory().setItem(chest.getInventory().getSize() - 1, hiltItemStack);
            ItemStack old = chest.getInventory().getItem(chest.getInventory().getSize() / 2 - 1);
            if (old != null && old.getType() == Material.PAPER) {
                HiltItemStack his = new HiltItemStack(old);
                if (his.getName().equals(ChatColor.GOLD + "Chest Status: " + ChatColor.RED + "Locked")) {
                    chest.getInventory().setItem(chest.getInventory().getSize() / 2 - 1, null);
                } else if (his.getName().equals(ChatColor.GOLD + "Chest Status: " + ChatColor.GREEN + "Unlocked")) {
                    chest.getInventory().setItem(chest.getInventory().getSize() / 2 - 1, null);
                }
            }
        } else if (event.getBlockPlaced().getState() instanceof DoubleChest) {
            BlockFace[] check =
                    {BlockFace.UP, BlockFace.DOWN, BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};
            for (BlockFace bf : check) {
                if (b.getRelative(bf).getState() instanceof Chest) {
                    if (!BoltAPI.isChestOwner(((Chest) b.getRelative(bf).getState()).getInventory(),
                                              event.getPlayer().getName())) {
                        event.setCancelled(true);
                        event.getPlayer().sendMessage(ChatColor.YELLOW +
                                                      "You cannot place chests next to chests you do not own.");
                        return;
                    }
                } else if (b.getRelative(bf).getState() instanceof DoubleChest) {
                    if (!BoltAPI.isChestOwner(((DoubleChest) b.getRelative(bf).getState()).getInventory(),
                                              event.getPlayer().getName())) {
                        event.setCancelled(true);
                        event.getPlayer().sendMessage(ChatColor.YELLOW +
                                                      "You cannot place chests next to chests you do not own.");
                        return;
                    }
                }
            }
            DoubleChest chest = (DoubleChest) event.getBlockPlaced().getState();
            ItemStack old = chest.getInventory().getItem(chest.getInventory().getSize() / 2 - 1);
            if (old != null && old.getType() == Material.PAPER) {
                HiltItemStack his = new HiltItemStack(old);
                if (his.getName().equals(ChatColor.GOLD + "Chest Status: " + BoltAPI.LockState.LOCKED)) {
                    chest.getInventory().setItem(chest.getInventory().getSize() / 2 - 1, null);
                } else if (his.getName().equals(ChatColor.GOLD + "Chest Status: " + BoltAPI.LockState.UNLOCKED)) {
                    chest.getInventory().setItem(chest.getInventory().getSize() / 2 - 1, null);
                }
                else if (his.getName().equals(ChatColor.GOLD + "Chest Status: " + BoltAPI.LockState.ALLOW_VIEW)) {
                    chest.getInventory().setItem(chest.getInventory().getSize() / 2 - 1, null);
                }
            }
            HiltItemStack hiltItemStack = new HiltItemStack(Material.PAPER);
            hiltItemStack.setName(ChatColor.GOLD + "Chest Status: " + BoltAPI.LockState.LOCKED);
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.WHITE + "<Click to Toggle>");
            lore.add(ChatColor.GOLD + "Owner: " + ChatColor.WHITE + event.getPlayer().getName());
            List<String> allowedUsers = BoltAPI.getAllowedUsers(chest.getInventory());
            if (allowedUsers.size() > 0) {
                for (String s : allowedUsers) {
                    lore.add(ChatColor.GRAY + s);
                }
            } else {
                lore.add(ChatColor.GRAY + "Type /add <playername> while looking at");
                lore.add(ChatColor.GRAY + "this chest to allow people to use it.");
            }
            hiltItemStack.setLore(lore);
            chest.getInventory().setItem(chest.getInventory().getSize() - 1, hiltItemStack);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onBlockBreakDoor(BlockBreakEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (event.getBlock().getType() != Material.IRON_DOOR_BLOCK && event.getBlock().getType() == Material.WOODEN_DOOR) {
            return;
        }
        Block below = event.getBlock().getRelative(0, -2, 0);
        if (below.getType() != Material.CHEST) {
            below = event.getBlock().getRelative(0, -3, 0);
            if (below.getType() != Material.CHEST) {
                return;
            }
        }
        InventoryHolder c = (InventoryHolder) below.getState();
        if (!BoltAPI.isChestOwner(c.getInventory(), event.getPlayer().getName())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockBreakChest(BlockBreakEvent event) {
        if (!(event.getBlock().getState() instanceof Chest) && !(event.getBlock().getState() instanceof DoubleChest)) {
            return;
        }
        if (!BoltAPI.isChestOwner(((InventoryHolder) event.getBlock().getState()).getInventory(),
                                  event.getPlayer().getName())) {
            if (!event.getPlayer().hasPermission("bolt.anylock")) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(ChatColor.RED + "You cannot break this chest.");
                return;
            }
        }
        InventoryHolder holder = (InventoryHolder) event.getBlock().getState();
        ItemStack itemStack = holder.getInventory().getItem(holder.getInventory().getSize() - 1);
        if (itemStack == null) {
            return;
        }
        HiltItemStack his = new HiltItemStack(itemStack);
        if (!his.getName().startsWith(ChatColor.GOLD + "Chest Status:")) {
            return;
        }
        if (holder.getInventory() instanceof DoubleChestInventory) {
            if (holder.getInventory().getItem(holder.getInventory().getSize() / 2 - 1) != null) {
                event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(),
                                                              holder.getInventory().getItem(
                                                                      holder.getInventory().getSize() / 2 - 1));
            }
            HiltItemStack hiltItemStack = new HiltItemStack(Material.PAPER);
            hiltItemStack.setName(ChatColor.GOLD + "Chest Status: " + ChatColor.RED + "Locked");
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.WHITE + "<Click to Toggle>");
            lore.add(ChatColor.GOLD + "Owner: " + ChatColor.WHITE + event.getPlayer().getName());
            List<String> allowedUsers = BoltAPI.getAllowedUsers(holder.getInventory());
            if (allowedUsers.size() > 0) {
                for (String s : allowedUsers) {
                    lore.add(ChatColor.GRAY + s);
                }
            } else {
                lore.add(ChatColor.GRAY + "Type /add <playername> while looking at");
                lore.add(ChatColor.GRAY + "this chest to allow people to use it.");
            }
            hiltItemStack.setLore(lore);
            holder.getInventory().setItem(holder.getInventory().getSize() / 2 - 1, hiltItemStack);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (!(event.getPlayer() instanceof Player)) {
            return;
        }
        if (event.getInventory().getHolder() instanceof Chest) {
            if (!BoltAPI.canOpen(event.getInventory(), (Player) event.getPlayer())) {
                event.setCancelled(true);
                ((Player) event.getPlayer()).sendMessage(ChatColor.YELLOW + "This chest is locked.");
            }
        } else if (event.getInventory().getHolder() instanceof DoubleChest) {
            if (!BoltAPI.canOpen(event.getInventory(), (Player) event.getPlayer())) {
                event.setCancelled(true);
                ((Player) event.getPlayer()).sendMessage(ChatColor.YELLOW + "This chest is locked.");
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        if (event.getInventory().getHolder() instanceof Chest ||
            event.getInventory().getHolder() instanceof DoubleChest) {
            if (!BoltAPI.canUse(event.getInventory(), (Player) event.getWhoClicked())) {
                event.setCancelled(true);
                event.setResult(Event.Result.DENY);
                return;
            }
            ItemStack itemStack = event.getCurrentItem();
            if (itemStack == null || itemStack.getType() != Material.PAPER) {
                return;
            }
            HiltItemStack his = new HiltItemStack(itemStack);
            if (his.getName().equals(ChatColor.GOLD + "Chest Status: " + BoltAPI.LockState.LOCKED.getDisplay())) {
                event.setCancelled(true);
                event.setResult(Event.Result.DENY);
            } else if (his.getName().equals(ChatColor.GOLD + "Chest Status: " + BoltAPI.LockState.UNLOCKED.getDisplay())) {
                event.setCancelled(true);
                event.setResult(Event.Result.DENY);
            } else if (his.getName().equals(ChatColor.GOLD + "Chest Status: " + BoltAPI.LockState.ALLOW_VIEW.getDisplay())) {
                event.setCancelled(true);
                event.setResult(Event.Result.DENY);
            }
            if (BoltAPI.isChestOwner(event.getInventory(), event.getWhoClicked().getName())) {
                if (his.getName().equals(ChatColor.GOLD + "Chest Status: " + BoltAPI.LockState.LOCKED.getDisplay())) {
                    his.setName(ChatColor.GOLD + "Chest Status: " + BoltAPI.LockState.UNLOCKED.getDisplay());
                    event.setCurrentItem(his);
                } else if (his.getName().equals(ChatColor.GOLD + "Chest Status: " + BoltAPI.LockState.UNLOCKED.getDisplay())) {
                    his.setName(ChatColor.GOLD + "Chest Status: " + BoltAPI.LockState.ALLOW_VIEW.getDisplay());
                    event.setCurrentItem(his);
                } else if (his.getName().equals(ChatColor.GOLD + "Chest Status: " + BoltAPI.LockState.ALLOW_VIEW.getDisplay())) {
                    his.setName(ChatColor.GOLD + "Chest Status: " + BoltAPI.LockState.LOCKED.getDisplay());
                    event.setCurrentItem(his);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onItemSpawn(ItemSpawnEvent event) {
        ItemStack itemStack = event.getEntity().getItemStack();
        HiltItemStack his = new HiltItemStack(itemStack);
        if (his.getName().equals(ChatColor.GOLD + "Chest Status: " + BoltAPI.LockState.LOCKED)) {
            event.setCancelled(true);
        }
        if (his.getName().equals(ChatColor.GOLD + "Chest Status: " + BoltAPI.LockState.UNLOCKED)) {
            event.setCancelled(true);
        }
        if (his.getName().equals(ChatColor.GOLD + "Chest Status: " + BoltAPI.LockState.ALLOW_VIEW)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null || event.getClickedBlock().getType() != Material.IRON_DOOR_BLOCK
            && event.getClickedBlock().getType() != Material.WOODEN_DOOR) {
            return;
        }
        Block below = event.getClickedBlock().getRelative(0, -2, 0);
        if (below == null || below.getType() != Material.CHEST) {
            below = event.getClickedBlock().getRelative(0, -3, 0);
            if (below == null || below.getType() != Material.CHEST) {
                return;
            }
        }
        InventoryHolder c = (InventoryHolder) below.getState();
        if (!BoltAPI.canUse(c.getInventory(), event.getPlayer())) {
            event.setCancelled(true);
            event.setUseInteractedBlock(Event.Result.DENY);
            event.setUseItemInHand(Event.Result.DENY);
        }
    }

}
