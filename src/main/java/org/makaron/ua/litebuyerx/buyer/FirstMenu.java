package org.makaron.ua.litebuyerx.buyer;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.makaron.ua.litebuyerx.LiteBuyerX;
import org.makaron.ua.litebuyerx.buyer.utils.HexUtil;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.stream.Collectors;

public class FirstMenu {

    private LiteBuyerX plugin;
    private static int timeLeft;

    public FirstMenu(LiteBuyerX plugin) {
        this.plugin = plugin;
    }

    public void openMenu(Player player) {
        FileConfiguration config = plugin.getConfig();
        Inventory inv = Bukkit.createInventory(null, 54, HexUtil.color(config.getString("title")));

        ItemStack panelItem = new ItemStack(Material.valueOf(config.getString("panel.material")));
        ItemMeta panelMeta = panelItem.getItemMeta();
        panelMeta.setDisplayName(" ");
        panelItem.setItemMeta(panelMeta);

        ItemStack sellItem = new ItemStack(Material.valueOf(config.getString("sell.material")));
        ItemMeta sellMeta = sellItem.getItemMeta();
        sellMeta.setDisplayName(HexUtil.color(config.getString("sell.name")));
        List<String> lore = config.getStringList("sell.lore").stream()
                .map(HexUtil::color)
                .collect(Collectors.toList());
        sellMeta.setLore(lore);
        sellItem.setItemMeta(sellMeta);
        inv.setItem(config.getInt("sell.slot"), sellItem);

        ItemStack closeItem = new ItemStack(Material.valueOf(config.getString("close.material")));
        ItemMeta closeMeta = closeItem.getItemMeta();
        closeMeta.setDisplayName(HexUtil.color(config.getString("close.name")));
        closeItem.setItemMeta(closeMeta);
        inv.setItem(config.getInt("close.slot"), closeItem);

        ItemStack eventItem = new ItemStack(Material.valueOf(config.getString("event.material")));
        ItemMeta eventMeta = eventItem.getItemMeta();
        eventMeta.setDisplayName(HexUtil.color(config.getString("event.name").replace("{time}", formatTime(timeLeft))));
        List<String> eventLore = config.getStringList("event.lore").stream()
                .map(HexUtil::color)
                .collect(Collectors.toList());
        eventMeta.setLore(eventLore);
        eventItem.setItemMeta(eventMeta);
        inv.setItem(config.getInt("event.slot"), eventItem);

        player.openInventory(inv);
        animatePanelItems(inv, config.getStringList("panel.slot"), panelItem, player, plugin);
        updateEventTimer(inv, eventItem, config.getInt("event.slot"), player);
    }

    private void updateEventTimer(Inventory inv, ItemStack eventItem, int slot, Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (timeLeft <= 0) {
                    this.cancel();
                    return;
                }

                timeLeft--;
                ItemMeta eventMeta = eventItem.getItemMeta();
                eventMeta.setDisplayName(HexUtil.color(plugin.getConfig().getString("event.name").replace("{time}", formatTime(timeLeft))));
                eventItem.setItemMeta(eventMeta);
                inv.setItem(slot, eventItem);
                player.updateInventory();
            }
        }.runTaskTimer(plugin, 0, 20);
    }

    private String formatTime(int seconds) {
        int minutes = seconds / 60;
        int secs = seconds % 60;
        return String.format("%02d:%02d", minutes, secs);
    }

    private static void animatePanelItems(Inventory inv, List<String> panelSlots, ItemStack panelItem, Player player, LiteBuyerX plugin) {
        int middleIndex = panelSlots.size() / 2;

        new BukkitRunnable() {
            int leftIndex = middleIndex;
            int rightIndex = middleIndex;
            boolean leftDone = false;
            boolean rightDone = false;

            @Override
            public void run() {
                if (!leftDone) {
                    int slot = Integer.parseInt(panelSlots.get(leftIndex));
                    inv.setItem(slot, panelItem);
                    player.playSound(player.getLocation(), Sound.BLOCK_STONE_PLACE, 1.0f, 1.0f);
                    leftIndex--;
                    if (leftIndex < 0) {
                        leftDone = true;
                    }
                }

                if (!rightDone) {
                    int slot = Integer.parseInt(panelSlots.get(rightIndex));
                    inv.setItem(slot, panelItem);
                    player.playSound(player.getLocation(), Sound.BLOCK_STONE_PLACE, 1.0f, 1.0f);
                    rightIndex++;
                    if (rightIndex >= panelSlots.size()) {
                        rightDone = true;
                    }
                }

                if (leftDone && rightDone) {
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0, 5);
    }

    public static void setTimeLeft(int time) {
        timeLeft = time;
    }
}