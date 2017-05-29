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

import io.pixeloutlaw.minecraft.spigot.hilt.HiltItemStack;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.*;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.HopperMinecart;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BoltListener implements Listener {

    private final BoltPlugin plugin;

    public BoltListener(BoltPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityExplode(EntityExplodeEvent event) {
        Iterator<Block> blockIterator = event.blockList().iterator();
        while (blockIterator.hasNext()) {
            Block b = blockIterator.next();
            if (b.getState() instanceof Chest || b.getState() instanceof DoubleChest) {
                blockIterator.remove();
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryMoveItem(InventoryMoveItemEvent event) {
        if (event.getInitiator().getHolder() instanceof HopperMinecart) {
            if (BoltAPI.getLockState(event.getSource()) != BoltAPI.LockState.UNLOCKED) {
                event.setCancelled(true);
                return;
            }
        }
        ItemStack is = event.getItem();
        if (is == null || is.getType() != Material.PAPER) {
            return;
        }
        if (is.getItemMeta().getDisplayName().startsWith(ChatColor.GOLD + "Chest Status:")) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        Block b = event.getBlockPlaced();
        if (event.getBlockPlaced().getState() instanceof Hopper) {
            BlockFace[] check =
                    {BlockFace.UP, BlockFace.DOWN, BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.NORTH_EAST,
                            BlockFace.NORTH_WEST, BlockFace.SOUTH_EAST, BlockFace.SOUTH_WEST};
            for (BlockFace bf : check) {
                Block relative = b.getRelative(bf);
                if (relative.getState() instanceof Chest) {
                    if (!BoltAPI.isChestOwner(((Chest) relative.getState()).getInventory(), event.getPlayer().getName())) {
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
                    {BlockFace.UP, BlockFace.DOWN, BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.NORTH_EAST,
                            BlockFace.NORTH_WEST, BlockFace.SOUTH_EAST, BlockFace.SOUTH_WEST};
            for (BlockFace bf : check) {
                Block relative = b.getRelative(bf);
                if (relative.getState() instanceof Chest) {
                    if (!BoltAPI.isChestOwner(((Chest) relative.getState()).getInventory(), event.getPlayer().getName())) {
                        event.setCancelled(true);
                        event.getPlayer().sendMessage(ChatColor.YELLOW +
                                                      "You cannot place chests next to chests you do not own.");
                        return;
                    }
                } else if (relative.getState() instanceof DoubleChest) {
                    if (!BoltAPI.isChestOwner(((DoubleChest) relative.getState()).getInventory(), event.getPlayer().getName())) {
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
                Block relative = b.getRelative(bf);
                if (b.getRelative(bf).getState() instanceof Chest) {
                    if (!BoltAPI.isChestOwner(((Chest) relative.getState()).getInventory(), event.getPlayer().getName())) {
                        event.setCancelled(true);
                        event.getPlayer().sendMessage(ChatColor.YELLOW +
                                                      "You cannot place chests next to chests you do not own.");
                        return;
                    }
                } else if (b.getRelative(bf).getState() instanceof DoubleChest) {
                    if (!BoltAPI.isChestOwner(((DoubleChest) relative.getState()).getInventory(), event.getPlayer().getName())) {
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

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockBreakChest(BlockBreakEvent event) {
        if (!(event.getBlock().getState() instanceof Chest) && !(event.getBlock().getState() instanceof DoubleChest)) {
            return;
        }
        if (BoltAPI.getChestOwnerName(((InventoryHolder) event.getBlock().getState()).getInventory()) == null) {
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
                event.getPlayer().sendMessage(ChatColor.YELLOW + "This chest is locked.");
            }
        } else if (event.getInventory().getHolder() instanceof DoubleChest) {
            if (!BoltAPI.canOpen(event.getInventory(), (Player) event.getPlayer())) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(ChatColor.YELLOW + "This chest is locked.");
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
    public void onOpenProximityCheck(InventoryOpenEvent event) {
        if (!(event.getPlayer() instanceof Player)) {
            return;
        }
        if (!(event.getInventory().getHolder() instanceof Hopper || event.getInventory().getHolder() instanceof Furnace)) {
            return;
        }
        Location loc = event.getInventory().getLocation();
        int xInit = loc.getBlockX();
        int yInit = loc.getBlockY();
        int zInit = loc.getBlockZ();
        Block testBlock;
        for (int x = xInit - 2; x < xInit + 3; x++) {
            loc.setX(x);
            for (int y = yInit - 2; y < yInit + 3; y++) {
                loc.setY(y);
                for (int z = zInit - 2; z < zInit + 3; z++) {
                    loc.setZ(z);
                    testBlock = loc.getBlock();
                    if (testBlock.getType().equals(Material.CHEST) || testBlock.getType().equals(Material.TRAPPED_CHEST)) {
                        if (!BoltAPI.canUse(((InventoryHolder)testBlock.getState()).getInventory(), (Player) event.getPlayer())) {
                            event.getPlayer().sendMessage(ChatColor.YELLOW + "This block is locked.");
                            event.setCancelled(true);
                            return;
                        }
                    }
                }
            }
        }
    }

}
