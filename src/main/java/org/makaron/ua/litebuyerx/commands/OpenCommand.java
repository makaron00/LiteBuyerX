package org.makaron.ua.litebuyerx.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.makaron.ua.litebuyerx.LiteBuyerX;
import org.makaron.ua.litebuyerx.buyer.FirstMenu;

public class OpenCommand implements CommandExecutor {

    private LiteBuyerX plugin;

    public OpenCommand(LiteBuyerX plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;
        FirstMenu firstMenu = new FirstMenu(plugin);
        firstMenu.openMenu(player);
        return true;
    }
}