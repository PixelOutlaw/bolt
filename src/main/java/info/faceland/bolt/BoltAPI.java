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
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class BoltAPI {

    private BoltAPI() {
        // do nothing
    }

    public enum LockState {
        UNLOCKED(ChatColor.GREEN + "Unlocked"),
        LOCKED(ChatColor.RED + "Locked"),
        ALLOW_VIEW(ChatColor.YELLOW + "Allow View");

        private final String display;

        LockState(String display) {
            this.display = display;
        }

        public String getDisplay() {
            return display;
        }

        public static LockState fromString(String s) {
            for (LockState ls : values()) {
                if (ls.display.equals(s)) {
                    return ls;
                }
            }
            return UNLOCKED;
        }

        @Override
        public String toString() {
            return getDisplay();
        }

    }

    public static LockState getLockState(Inventory inventory) {
        ItemStack itemStack = inventory.getItem(inventory.getSize() - 1);
        if (itemStack == null || itemStack.getType() != Material.PAPER) {
            return LockState.UNLOCKED;
        }
        HiltItemStack hiltItemStack = new HiltItemStack(itemStack);
        if (hiltItemStack.getName().equals("")) {
            return LockState.UNLOCKED;
        }
        return LockState.fromString(hiltItemStack.getName().replace(ChatColor.GOLD + "Chest Status: ", ""));
    }

    public static boolean canOpen(Inventory inventory, Player opener) {
        LockState lockState = getLockState(inventory);
        return lockState == LockState.UNLOCKED || lockState == LockState.ALLOW_VIEW || getChestOwnerName(inventory) == null ||
               isChestOwner(inventory, opener.getName()) || containsIgnoreCase(getAllowedUsers(inventory), opener.getName())
               || opener.hasPermission("bolt.anylock");
    }

    public static boolean canUse(Inventory inventory, Player opener) {
        LockState lockState = getLockState(inventory);
        return lockState == LockState.UNLOCKED || isChestOwner(inventory, opener.getName()) ||
               containsIgnoreCase(getAllowedUsers(inventory), opener.getName()) || opener.hasPermission("bolt.anylock");
    }

    public static boolean isChestOwner(Inventory inventory, String opener) {
        String owner = getChestOwnerName(inventory);
        return opener != null && opener.equalsIgnoreCase(owner);
    }

    public static String getChestOwnerName(Inventory inventory) {
        if (inventory instanceof DoubleChestInventory) {
            ItemStack itemStack = inventory.getItem(inventory.getSize() - 1);
            if (itemStack == null || itemStack.getType() != Material.PAPER) {
                itemStack = inventory.getItem(inventory.getSize() / 2 - 1);
                if (itemStack == null || itemStack.getType() != Material.PAPER) {
                    return null;
                }
            }
            HiltItemStack hiltItemStack = new HiltItemStack(itemStack);
            if (hiltItemStack.getLore() == null) {
                return null;
            }
            if (hiltItemStack.getLore().size() < 2) {
                return null;
            }
            String ownerLine = hiltItemStack.getLore().get(1);
            return ChatColor.stripColor(ownerLine).replace("Owner: ", "").trim();
        } else {
            ItemStack itemStack = inventory.getItem(inventory.getSize() - 1);
            if (itemStack == null || itemStack.getType() != Material.PAPER) {
                return null;
            }
            HiltItemStack hiltItemStack = new HiltItemStack(itemStack);
            if (hiltItemStack.getLore() == null) {
                return null;
            }
            if (hiltItemStack.getLore().size() < 2) {
                return null;
            }
            String ownerLine = hiltItemStack.getLore().get(1);
            return ChatColor.stripColor(ownerLine).replace("Owner: ", "").trim();
        }
    }

    public static void setChestOwner(Inventory inventory, String owner) {
        if (owner == null) {
            return;
        }
        ItemStack itemStack = inventory.getItem(inventory.getSize() - 1);
        if (itemStack == null || itemStack.getType() != Material.PAPER) {
            return;
        }
        HiltItemStack hiltItemStack = new HiltItemStack(itemStack);
        if (!hiltItemStack.getName().startsWith(ChatColor.GOLD + "Chest Status:")) {
            return;
        }
        List<String> lore = hiltItemStack.getLore();
        lore.set(1, ChatColor.GOLD + "Owner: " + ChatColor.WHITE + owner);
        hiltItemStack.setLore(lore);
        inventory.setItem(inventory.getSize() - 1, hiltItemStack);
    }

    public static List<String> getAllowedUsers(Inventory inventory) {
        if (inventory instanceof DoubleChestInventory) {
            ItemStack itemStack = inventory.getItem(inventory.getSize() - 1);
            if (itemStack == null || itemStack.getType() != Material.PAPER) {
                itemStack = inventory.getItem(inventory.getSize() / 2 - 1);
                if (itemStack == null || itemStack.getType() != Material.PAPER) {
                    return new ArrayList<>();
                }
            }
            HiltItemStack hiltItemStack = new HiltItemStack(itemStack);
            if (!hiltItemStack.getName().startsWith(ChatColor.GOLD + "Chest Status:")) {
                return new ArrayList<>();
            }
            List<String> allowed = new ArrayList<>();
            if (hiltItemStack.getLore().get(2).equals(ChatColor.GRAY + "Type /add <playername> while looking at")) {
                return allowed;
            }
            List<String> subList = hiltItemStack.getLore().subList(2, hiltItemStack.getLore().size());
            for (String s : subList) {
                allowed.add(ChatColor.stripColor(s));
            }
            return allowed;
        } else {
            ItemStack itemStack = inventory.getItem(inventory.getSize() - 1);
            if (itemStack == null || itemStack.getType() != Material.PAPER) {
                return new ArrayList<>();
            }
            HiltItemStack hiltItemStack = new HiltItemStack(itemStack);
            if (!hiltItemStack.getName().startsWith(ChatColor.GOLD + "Chest Status:")) {
                return new ArrayList<>();
            }
            List<String> allowed = new ArrayList<>();
            if (hiltItemStack.getLore().get(2).equals(ChatColor.GRAY + "Type /add <playername> while looking at")) {
                return allowed;
            }
            List<String> subList = hiltItemStack.getLore().subList(2, hiltItemStack.getLore().size());
            for (String s : subList) {
                allowed.add(ChatColor.stripColor(s));
            }
            return allowed;
        }
    }

    public static void setAllowedUsers(Inventory inventory, List<String> users) {
        ItemStack itemStack = inventory.getItem(inventory.getSize() - 1);
        if (itemStack == null || itemStack.getType() != Material.PAPER) {
            return;
        }
        HiltItemStack hiltItemStack = new HiltItemStack(itemStack);
        if (!hiltItemStack.getName().startsWith(ChatColor.GOLD + "Chest Status:")) {
            return;
        }
        String owner = ChatColor.stripColor(hiltItemStack.getLore().get(1)).replace("Owner: ", "").trim();
        List<String> lore = hiltItemStack.getLore();
        lore.clear();
        lore.add(ChatColor.WHITE + "<Click to Toggle>");
        lore.add(ChatColor.GOLD + "Owner: " + ChatColor.WHITE + owner);
        if (users.size() > 0) {
            for (String s : users) {
                lore.add(ChatColor.GRAY + s);
            }
        } else {
            lore.add(ChatColor.GRAY + "Type /add <playername> while looking at");
            lore.add(ChatColor.GRAY + "this chest to allow people to use it.");
        }
        hiltItemStack.setLore(lore);
        inventory.setItem(inventory.getSize() - 1, hiltItemStack);
    }

    private static boolean containsIgnoreCase(List<String> list, String name) {
        if (list == null || name == null) {
            return false;
        }
        for (String s : list) {
            if (s.equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

}
