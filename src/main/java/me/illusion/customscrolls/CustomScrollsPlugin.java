package me.illusion.customscrolls;

import lombok.Getter;
import me.illusion.customscrolls.command.GiveScrollCommand;
import me.illusion.customscrolls.listener.*;
import me.illusion.customscrolls.lore.PacketHandler;
import me.illusion.customscrolls.manager.ScrollManager;
import me.illusion.utilities.storage.MessagesFile;
import me.illusion.utilities.storage.YMLBase;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class CustomScrollsPlugin extends JavaPlugin {

    private MessagesFile messages;
    private FileConfiguration scrollFile;
    private ScrollManager scrollManager;

    @Override
    public void onEnable() {
        // Plugin startup logic
        messages = new MessagesFile(this);
        scrollFile = new YMLBase(this, "scrolls.yml").getConfiguration();
        scrollManager = new ScrollManager(this);

        new PacketHandler(this);
        registerListeners();
        registerCommands();
    }

    private void registerListeners() {
        PluginManager pluginManager = Bukkit.getPluginManager();

        pluginManager.registerEvents(new BlockBreakTrackerListener(), this);
        pluginManager.registerEvents(new ItemBreakListener(), this);
        pluginManager.registerEvents(new KillTrackerListener(), this);
        pluginManager.registerEvents(new PlayerDeathListener(this), this);
        pluginManager.registerEvents(new ScrollApplyListener(this), this);
    }

    private void registerCommands() {
        GiveScrollCommand command = new GiveScrollCommand(this);

        getCommand("scroll").setExecutor(command);
        getCommand("scroll").setTabCompleter(command);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
