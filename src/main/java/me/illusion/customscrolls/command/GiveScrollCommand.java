package me.illusion.customscrolls.command;

import com.google.common.base.Enums;
import com.google.common.base.Optional;
import me.illusion.customscrolls.CustomScrollsPlugin;
import me.illusion.customscrolls.data.ScrollType;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GiveScrollCommand implements CommandExecutor, TabCompleter {

    private final CustomScrollsPlugin main;

    public GiveScrollCommand(CustomScrollsPlugin main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!sender.hasPermission("customscrolls.give")) {
            main.getMessages().sendMessage(sender, "no-permission");
            return true;
        }

        if(args.length < 3 || args.length > 4 || !args[0].equalsIgnoreCase("give")) {
            main.getMessages().sendMessage(sender, "give-scroll-usage");
            return true;
        }

        String scrollName = args[1];
        String playerName = args[2];

        Optional<ScrollType> scrollTypeOptional = Enums.getIfPresent(ScrollType.class, scrollName.toUpperCase());
        if(!scrollTypeOptional.isPresent()) {
            main.getMessages().sendMessage(sender, "invalid-scroll");
            return true;
        }

        ScrollType scrollType = scrollTypeOptional.get();

        Player target = Bukkit.getPlayer(playerName);

        if(target == null) {
            main.getMessages().sendMessage(sender, "player-not-found");
            return true;
        }

        int amount = 1;

        if(args.length == 4) {
            try {
                amount = Integer.parseInt(args[3]);
            }
            catch(NumberFormatException e) {
                main.getMessages().sendMessage(sender, "invalid-number");
            }

        }

        ItemStack item = main.getScrollManager().getItem(scrollType);
        item.setAmount(amount);

        target.getInventory().addItem(item);
        main.getMessages().sendMessage(sender, "give-scroll-success", (str) -> str.replace("%target%", target.getName()));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if(args.length == 1 && "give".startsWith(args[0].toLowerCase())) {
            completions.add("give");
        }
        if(args.length == 2) {
            StringUtil.copyPartialMatches(args[1], ScrollType.names(), completions);
        }

        if(args.length == 3) {
            StringUtil.copyPartialMatches(args[2], Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toSet()), completions);
        }

        return completions;
    }
}
