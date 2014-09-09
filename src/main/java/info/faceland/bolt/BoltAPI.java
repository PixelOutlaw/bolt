package info.faceland.bolt;

import info.faceland.hilt.HiltItemStack;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class BoltAPI {

    private BoltAPI() {
        // do nothing
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
            if (ChatColor.stripColor(s).equals(opener.getName())) {
                return false;
            }
        }
        return true;
    }

    public static boolean isChestOwner(Inventory inventory, String opener) {
        String owner = getChestOwnerName(inventory);
        return !(owner == null || opener == null) && owner.equals(opener);
    }

    public static String getChestOwnerName(Inventory inventory) {
        ItemStack itemStack = inventory.getItem(inventory.getSize() - 1);
        if (itemStack == null || itemStack.getType() != Material.PAPER) {
            return null;
        }
        HiltItemStack hiltItemStack = new HiltItemStack(itemStack);
        return ChatColor.stripColor(hiltItemStack.getLore().get(1)).replace("Owner: ", "").trim();
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
        ItemStack itemStack = inventory.getItem(inventory.getSize() - 1);
        if (itemStack == null || itemStack.getType() != Material.PAPER) {
            return new ArrayList<>();
        }
        HiltItemStack hiltItemStack = new HiltItemStack(itemStack);
        if (!hiltItemStack.getName().startsWith(ChatColor.GOLD + "Chest Status:")) {
            return new ArrayList<>();
        }
        List<String> allowed = new ArrayList<>();
        if (hiltItemStack.getLore().size() == 3) {
            allowed.add(hiltItemStack.getLore().get(2));
            return allowed;
        }
        if (hiltItemStack.getLore().get(2).equals(ChatColor.GRAY + "Type /add <playername> while looking")) {
            return allowed;
        }
        List<String> subList = hiltItemStack.getLore().subList(2, hiltItemStack.getLore().size());
        for (String s : subList) {
            allowed.add(ChatColor.stripColor(s));
        }
        return allowed;
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
        lore.add(ChatColor.WHITE + "< Click to Toggle >");
        lore.add(ChatColor.GOLD + "Owner: " + ChatColor.WHITE + owner);
        if (users.size() > 0) {
            for (String s : users) {
                lore.add(ChatColor.WHITE + s);
            }
        } else {
            lore.add(ChatColor.GRAY + "Type /add <playername> while looking");
            lore.add(ChatColor.GRAY + "at this chest to allow people to use it.");
        }
        hiltItemStack.setLore(lore);
        inventory.setItem(inventory.getSize() - 1, hiltItemStack);
    }

}
