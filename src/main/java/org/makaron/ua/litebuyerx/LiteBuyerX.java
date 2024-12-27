package org.makaron.ua.litebuyerx;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.makaron.ua.litebuyerx.buyer.utils.HexUtil;
import org.makaron.ua.litebuyerx.commands.MainCommand;
import org.makaron.ua.litebuyerx.commands.OpenCommand;
import org.makaron.ua.litebuyerx.events.InventoryEventHandler;
import org.makaron.ua.litebuyerx.buyer.FirstMenu;

import java.io.File;

public final class LiteBuyerX extends JavaPlugin {

    private Economy economy;
    private FileConfiguration itemsConfig;
    private boolean eventActive;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadItemsConfig();
        getCommand("seller").setExecutor(new OpenCommand(this));
        getCommand("litebuyerx").setExecutor(new MainCommand(this));
        Bukkit.getPluginManager().registerEvents(new InventoryEventHandler(this), this);
        if (!setupEconomy()) {
            getLogger().severe("Vault not found!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        scheduleEvent();
        getLogger().info("§a=================================");
        getLogger().info("LiteBuyerX §aEnable!");
        getLogger().info("Dev: §ehttps://github.com/makaron00");
        getLogger().info("§a=================================");
    }

    @Override
    public void onDisable() {
        getLogger().info("§a=================================");
        getLogger().info("LiteBuyerX §cDisable!");
        getLogger().info("Dev: §ehttps://github.com/makaron00");
        getLogger().info("§a=================================");
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }

    public Economy getEconomy() {
        return economy;
    }

    public FileConfiguration getItemsConfig() {
        return itemsConfig;
    }

    public void loadItemsConfig() {
        File itemsFile = new File(getDataFolder(), "items.yml");
        if (!itemsFile.exists()) {
            saveResource("items.yml", false);
        }
        itemsConfig = YamlConfiguration.loadConfiguration(itemsFile);
    }

    private void scheduleEvent() {
        FileConfiguration config = getConfig();
        int intervalMinutes = config.getInt("event.time");
        int durationSeconds = config.getInt("event.end-time");

        FirstMenu.setTimeLeft(intervalMinutes * 60);

        new BukkitRunnable() {
            @Override
            public void run() {
                activateEvent(durationSeconds);
            }
        }.runTaskTimer(this, 0, intervalMinutes * 60 * 20);
    }

    private void activateEvent(int durationSeconds) {
        eventActive = true;
        Bukkit.broadcastMessage(HexUtil.color(getConfig().getString("started-events")));

        new BukkitRunnable() {
            @Override
            public void run() {
                eventActive = false;
                Bukkit.broadcastMessage(HexUtil.color(getConfig().getString("event-end")));
            }
        }.runTaskLater(this, durationSeconds * 20);
        FirstMenu.setTimeLeft(getConfig().getInt("event.time") * 60);
    }

    public boolean isEventActive() {
        return eventActive;
    }
}