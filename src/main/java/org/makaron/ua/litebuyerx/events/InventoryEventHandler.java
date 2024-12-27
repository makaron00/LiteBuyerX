package org.makaron.ua.litebuyerx.events;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.makaron.ua.litebuyerx.LiteBuyerX;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class InventoryEventHandler implements Listener {

     private LiteBuyerX plugin;
     private Set<UUID> playersInventorySet;

     public InventoryEventHandler(LiteBuyerX plugin) {
          this.plugin = plugin;
          this.playersInventorySet = new HashSet<>();
     }

     @EventHandler
     public void onInventoryClick(InventoryClickEvent event) {
          if (event.getClickedInventory() == null) return;
          if (!event.getView().getTitle().equals(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("title")))) return;

          Player player = (Player) event.getWhoClicked();
          FileConfiguration config = plugin.getConfig();

          ItemStack currentItem = event.getCurrentItem();
          if (currentItem != null && currentItem.getType() == Material.valueOf(config.getString("panel.material"))) {
               event.setCancelled(true);
               return;
          }

          if (currentItem != null && event.getSlot() == config.getInt("close.slot")) {
               player.closeInventory();
               return;
          }

          if (event.getSlot() == 53) {
               event.setCancelled(true);
               return;
          }

          playersInventorySet.add(player.getUniqueId());

          if (event.getSlot() == config.getInt("sell.slot")) {
               FileConfiguration itemsConfig = plugin.getItemsConfig();
               double total = 0;
               double eventBonus = 0;
               int itemsCount = 0;

               for (int i = 0; i < 45; i++) {
                    ItemStack item = event.getClickedInventory().getItem(i);
                    if (item != null && item.getType() != Material.AIR) {
                         if (itemsConfig.contains("buyer." + item.getType().name())) {
                              int price = itemsConfig.getInt("buyer." + item.getType().name() + ".price");
                              total += price * item.getAmount();
                              itemsCount += item.getAmount();
                              event.getClickedInventory().setItem(i, null);
                         }
                    }
               }

               if (plugin.isEventActive() && itemsCount >= config.getInt("event.items")) {
                    eventBonus = config.getInt("event.money");
               }

               plugin.getEconomy().depositPlayer(player, total + eventBonus);
               player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("sell-message-item").replace("{money}", String.valueOf(total + eventBonus))));
               event.setCancelled(true);
          }
     }

     @EventHandler
     public void onInventoryClose(InventoryCloseEvent event) {
          Player player = (Player) event.getPlayer();
          if (!event.getView().getTitle().equals(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("title")))) return;
          if (playersInventorySet.contains(player.getUniqueId())) {
               for (int i = 0; i < 45; i++) {
                    ItemStack item = event.getInventory().getItem(i);
                    if (item != null && item.getType() != Material.AIR) {
                         player.getInventory().addItem(item);
                         event.getInventory().setItem(i, null);
                    }
               }
               playersInventorySet.remove(player.getUniqueId());
          }
     }
}