package info.faceland.bolt;

import info.faceland.hilt.HiltItemStack;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class BoltAPI {

    private BoltAPI() {
        // do nothing
    }

    public static boolean isChestLocked(Chest chest, Player opener) {
        if (chest == null || opener.hasPermission("bolt.anylock")) {
            return false;
        }
        Inventory inventory = chest.getBlockInventory();
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

    public static boolean isChestOwner(Chest chest, Player opener) {
        String owner = getChestOwnerName(chest);
        return !(owner == null || opener == null) && owner.equals(opener.getName());
    }

    public static String getChestOwnerName(Chest chest) {
        if (chest == null) {
            return null;
        }
        Inventory inventory = chest.getBlockInventory();
        ItemStack itemStack = inventory.getItem(inventory.getSize() - 1);
        if (itemStack == null || itemStack.getType() != Material.PAPER) {
            return null;
        }
        HiltItemStack hiltItemStack = new HiltItemStack(itemStack);
        return ChatColor.stripColor(hiltItemStack.getLore().get(1)).replace("Owner: ", "").trim();
    }

    public static void setChestOwner(Chest chest, Player owner) {
        if (chest == null || owner == null) {
            return;
        }
        Inventory inventory = chest.getBlockInventory();
        ItemStack itemStack = inventory.getItem(inventory.getSize() - 1);
        if (itemStack == null || itemStack.getType() != Material.PAPER) {
            return;
        }
        HiltItemStack hiltItemStack = new HiltItemStack(itemStack);
        if (!hiltItemStack.getName().startsWith(ChatColor.GOLD + "Chest Status:")) {
            return;
        }
        List<String> lore = hiltItemStack.getLore();
        lore.set(1, ChatColor.GOLD + "Owner: " + ChatColor.WHITE + owner.getName());
        hiltItemStack.setLore(lore);
        inventory.setItem(inventory.getSize() - 1, hiltItemStack);
    }

    public static List<String> getAllowedUsers(Chest chest) {
        if (chest == null) {
            return new ArrayList<>();
        }
        Inventory inventory = chest.getBlockInventory();
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

    public static void setAllowedUsers(Chest chest, List<String> users) {
        if (chest == null) {
            return;
        }
        Inventory inventory = chest.getBlockInventory();
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
