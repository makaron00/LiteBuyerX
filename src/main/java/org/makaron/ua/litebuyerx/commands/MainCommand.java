package org.makaron.ua.litebuyerx.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.makaron.ua.litebuyerx.LiteBuyerX;

import java.util.Collections;
import java.util.List;

public class MainCommand implements TabExecutor {

    private final LiteBuyerX plugin;

    public MainCommand(LiteBuyerX plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            if (sender instanceof Player) {
                if (!sender.hasPermission("litebuyerx.reload")) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("permission-message")));
                    return true;
                }
            }
            plugin.reloadConfig();
            plugin.loadItemsConfig();
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("reload-message")));
            return true;
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            return Collections.singletonList("reload");
        }
        return Collections.emptyList();
    }
}