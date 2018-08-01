package io.github.jonthesquirrel.sandwich;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Sandwich extends JavaPlugin {

    private FileConfiguration config;
    private Map<String, String> topLinks;
    private Map<String, String> bottomLinks;

    public void onEnable() {
        //load config

        config = this.getConfig();
        topLinks = new HashMap<String, String>();
        bottomLinks = new HashMap<String, String>();

        if (config.getConfigurationSection("worlds") == null) {
            config.createSection("worlds");
        }
        Set<String> worldKeys = config.getConfigurationSection("worlds").getKeys(false);
        if (worldKeys != null) {
            for (String worldString : worldKeys) {
                String top = config.getString("worlds." + worldString + ".top", null);
                String bottom = config.getString("worlds." + worldString + ".bottom", null);
                if (top != null) {
                    topLinks.put(worldString, top);
                }
                if (bottom != null) {
                    bottomLinks.put(worldString, bottom);
                }

            }
        }

    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {

    }

}
