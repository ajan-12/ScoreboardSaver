package top.ageofelysian.scoreboardsaver;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor {

    Commands() {}

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;

            if (p.hasPermission("scoreboardsaver.use")) {

                Bukkit.getLogger().info("Backing up scoreboard data.");
                p.sendMessage(ChatColor.YELLOW + "Backing up scoreboard data.");

                if (ScoreboardSaver.getInstance().backupScoreboard(true)) {
                    p.sendMessage(ChatColor.GREEN + "Successfully backed up scoreboard data.");
                } else p.sendMessage(ChatColor.DARK_RED + "Scoreboard data backing up failed!");
                return true;

            } else {
                p.sendMessage(ChatColor.DARK_RED + "You do not have permission to do this.");
                return true;
            }
        } else {
            Bukkit.getLogger().info("Backing up scoreboard data.");

            if (ScoreboardSaver.getInstance().backupScoreboard(true)) {
                sender.sendMessage("Successfully backed up scoreboard data.");
            } else sender.sendMessage("Scoreboard data backing up failed!");
            return true;
        }
    }
}
