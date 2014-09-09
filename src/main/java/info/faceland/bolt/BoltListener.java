package info.faceland.bolt;

import info.faceland.hilt.HiltItemStack;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.Hopper;
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
            return;
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getBlockPlaced().getState() instanceof Hopper) {
            if (event.getBlockPlaced().getRelative(BlockFace.UP).getState() instanceof Chest) {
                if (!BoltAPI.isChestOwner(
                        ((Chest) event.getBlockPlaced().getRelative(BlockFace.UP).getState()).getInventory(),
                        event.getPlayer().getName())) {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(ChatColor.YELLOW + "You cannot place hoppers under chests you do not own.");
                }
            } else if (event.getBlockPlaced().getRelative(BlockFace.UP).getState() instanceof DoubleChest) {
                if (!BoltAPI.isChestOwner(
                        ((DoubleChest) event.getBlockPlaced().getRelative(BlockFace.UP).getState()).getInventory(),
                        event.getPlayer().getName())) {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(ChatColor.YELLOW + "You cannot place hoppers under chests you do not own.");
                }
            }
        } else if (event.getBlockPlaced().getState() instanceof Chest) {
            Chest chest = (Chest) event.getBlockPlaced().getState();
            ItemStack old = chest.getInventory().getItem(chest.getInventory().getSize() / 2 - 1);
            if (old != null && old.getType() == Material.PAPER) {
                HiltItemStack his = new HiltItemStack(old);
                if (his.getName().equals(ChatColor.GOLD + "Chest Status: " + ChatColor.RED + "Locked")) {
                    chest.getInventory().setItem(chest.getInventory().getSize() / 2 - 1, null);
                } else if (his.getName().equals(ChatColor.GOLD + "Chest Status: " + ChatColor.GREEN + "Unlocked")) {
                    chest.getInventory().setItem(chest.getInventory().getSize() / 2 - 1, null);
                }
            }
            HiltItemStack hiltItemStack = new HiltItemStack(Material.PAPER);
            hiltItemStack.setName(ChatColor.GOLD + "Chest Status: " + ChatColor.RED + "Locked");
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.WHITE + "<Click to Toggle>");
            lore.add(ChatColor.GOLD + "Owner: " + ChatColor.WHITE + event.getPlayer().getName());
            lore.add(ChatColor.GRAY + "Type /add <playername> while looking");
            lore.add(ChatColor.GRAY + "at this chest to allow people to use it.");
            hiltItemStack.setLore(lore);
            chest.getInventory().setItem(chest.getInventory().getSize() - 1, hiltItemStack);
        } else if (event.getBlockPlaced().getState() instanceof InventoryHolder &&
                event.getBlockPlaced().getState() instanceof DoubleChest) {
            DoubleChest chest = (DoubleChest) event.getBlockPlaced().getState();
            ItemStack old = chest.getInventory().getItem(chest.getInventory().getSize() / 2 - 1);
            if (old != null && old.getType() == Material.PAPER) {
                HiltItemStack his = new HiltItemStack(old);
                if (his.getName().equals(ChatColor.GOLD + "Chest Status: " + ChatColor.RED + "Locked")) {
                    chest.getInventory().setItem(chest.getInventory().getSize() / 2 - 1, null);
                } else if (his.getName().equals(ChatColor.GOLD + "Chest Status: " + ChatColor.GREEN + "Unlocked")) {
                    chest.getInventory().setItem(chest.getInventory().getSize() / 2 - 1, null);
                }
            }
            HiltItemStack hiltItemStack = new HiltItemStack(Material.PAPER);
            hiltItemStack.setName(ChatColor.GOLD + "Chest Status: " + ChatColor.RED + "Locked");
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.WHITE + "< Click to Toggle >");
            lore.add(ChatColor.GOLD + "Owner: " + ChatColor.WHITE + event.getPlayer().getName());
            List<String> allowedUsers = BoltAPI.getAllowedUsers(chest.getInventory());
            if (allowedUsers.size() > 0) {
                for (String s : allowedUsers) {
                    lore.add(ChatColor.WHITE + s);
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
    public void onBlockBreakEvent(BlockBreakEvent event) {
        if (!(event.getBlock().getState() instanceof InventoryHolder)) {
            return;
        }
        if (!BoltAPI.isChestOwner(((InventoryHolder) event.getBlock().getState()).getInventory(),
                                  event.getPlayer().getName())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "You cannot break this chest.");
            return;
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
                    lore.add(ChatColor.WHITE + s);
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
            if (BoltAPI.isChestLocked(event.getInventory(), (Player) event.getPlayer())) {
                event.setCancelled(true);
                ((Player) event.getPlayer()).sendMessage(ChatColor.YELLOW + "This chest is locked.");
            }
        } else if (event.getInventory().getHolder() instanceof DoubleChest) {
            if (BoltAPI.isChestLocked(event.getInventory(), (Player) event.getPlayer())) {
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
        if (event.getInventory().getHolder() instanceof Chest) {
            ItemStack itemStack = event.getCurrentItem();
            if (itemStack == null || itemStack.getType() != Material.PAPER) {
                return;
            }
            HiltItemStack his = new HiltItemStack(itemStack);
            if (his.getName().equals(ChatColor.GOLD + "Chest Status: " + ChatColor.RED + "Locked")) {
                event.setCancelled(true);
                event.setResult(Event.Result.DENY);
            } else if (his.getName().equals(ChatColor.GOLD + "Chest Status: " + ChatColor.GREEN + "Unlocked")) {
                event.setCancelled(true);
                event.setResult(Event.Result.DENY);
            }
            if (BoltAPI.isChestOwner(event.getInventory(), event.getWhoClicked().getName())) {
                if (his.getName().equals(ChatColor.GOLD + "Chest Status: " + ChatColor.RED + "Locked")) {
                    his.setName(ChatColor.GOLD + "Chest Status: " + ChatColor.GREEN + "Unlocked");
                    event.setCurrentItem(his);
                } else if (his.getName().equals(ChatColor.GOLD + "Chest Status: " + ChatColor.GREEN + "Unlocked")) {
                    his.setName(ChatColor.GOLD + "Chest Status: " + ChatColor.RED + "Locked");
                    event.setCurrentItem(his);
                }
            }
        } else if (event.getInventory().getHolder() instanceof DoubleChest) {
            ItemStack itemStack = event.getCurrentItem();
            if (itemStack == null || itemStack.getType() != Material.PAPER) {
                return;
            }
            HiltItemStack his = new HiltItemStack(itemStack);
            if (his.getName().equals(ChatColor.GOLD + "Chest Status: " + ChatColor.RED + "Locked")) {
                event.setCancelled(true);
                event.setResult(Event.Result.DENY);
            } else if (his.getName().equals(ChatColor.GOLD + "Chest Status: " + ChatColor.GREEN + "Unlocked")) {
                event.setCancelled(true);
                event.setResult(Event.Result.DENY);
            }
            if (BoltAPI.isChestOwner(event.getInventory(), event.getWhoClicked().getName())) {
                if (his.getName().equals(ChatColor.GOLD + "Chest Status: " + ChatColor.RED + "Locked")) {
                    his.setName(ChatColor.GOLD + "Chest Status: " + ChatColor.GREEN + "Unlocked");
                    event.setCurrentItem(his);
                } else if (his.getName().equals(ChatColor.GOLD + "Chest Status: " + ChatColor.GREEN + "Unlocked")) {
                    his.setName(ChatColor.GOLD + "Chest Status: " + ChatColor.RED + "Locked");
                    event.setCurrentItem(his);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onItemSpawn(ItemSpawnEvent event) {
        ItemStack itemStack = event.getEntity().getItemStack();
        HiltItemStack his = new HiltItemStack(itemStack);
        if (his.getName().equals(ChatColor.GOLD + "Chest Status: " + ChatColor.RED + "Locked")) {
            event.setCancelled(true);
        }
        if (his.getName().equals(ChatColor.GOLD + "Chest Status: " + ChatColor.GREEN + "Unlocked")) {
            event.setCancelled(true);
        }
    }

}
