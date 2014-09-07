package info.faceland.bolt;

import info.faceland.hilt.HiltItemStack;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class BoltAPI {

    private BoltAPI() {
        // do nothing
    }

    public static boolean isChestLocked(Chest chest, Player opener) {
        if (chest == null) {
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
        return true;
    }

}
