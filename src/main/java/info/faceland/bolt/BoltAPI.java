package info.faceland.bolt;

import info.faceland.hilt.HiltItemStack;
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
        if (opener.hasPermission("bolt.anylock")) {
            return true;
        }
        LockState lockState = getLockState(inventory);
        return lockState == LockState.UNLOCKED || lockState == LockState.ALLOW_VIEW ||
               isChestOwner(inventory, opener.getName()) || getAllowedUsers(inventory).contains(opener.getName());
    }

    public static boolean canUse(Inventory inventory, Player opener) {
        if (opener.hasPermission("bolt.anylock")) {
            return true;
        }
        LockState lockState = getLockState(inventory);
        return lockState == LockState.UNLOCKED || isChestOwner(inventory, opener.getName()) ||
               getAllowedUsers(inventory).contains(opener.getName());
    }

    public static boolean isChestLocked(Inventory inventory, Player opener) {
        if (opener.hasPermission("bolt.anylock")) {
            return false;
        }
        ItemStack itemStack = inventory.getItem(inventory.getSize() - 1);
        if (itemStack == null || itemStack.getType() != Material.PAPER) {
            return false;
        }
        HiltItemStack hiltItemStack = new HiltItemStack(itemStack);
        if (hiltItemStack.getName().equals(ChatColor.GOLD + "Chest Status: " + ChatColor.GREEN + "Unlocked")) {
            return false;
        }
        String line1 = ChatColor.stripColor(hiltItemStack.getLore().get(1)).replace("Owner: ", "").trim();
        if (line1.equals(opener.getName())) {
            return false;
        }
        for (String s : hiltItemStack.getLore()) {
            String strip = ChatColor.stripColor(s);
            if (strip.equalsIgnoreCase(opener.getName()) || strip.equalsIgnoreCase("Everybody") ||
                    strip.equalsIgnoreCase("Anybody") || strip.equalsIgnoreCase("Everyone") ||
                    strip.equalsIgnoreCase("Anyone")) {
                return false;
            }
        }
        return true;
    }

    public static boolean isChestOwner(Inventory inventory, String opener) {
        String owner = getChestOwnerName(inventory);
        return owner == null || opener != null && opener.equals(owner);
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
            return ChatColor.stripColor(hiltItemStack.getLore().get(1)).replace("Owner: ", "").trim();
        } else {
            ItemStack itemStack = inventory.getItem(inventory.getSize() - 1);
            if (itemStack == null || itemStack.getType() != Material.PAPER) {
                return null;
            }
            HiltItemStack hiltItemStack = new HiltItemStack(itemStack);
            return ChatColor.stripColor(hiltItemStack.getLore().get(1)).replace("Owner: ", "").trim();
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

}
