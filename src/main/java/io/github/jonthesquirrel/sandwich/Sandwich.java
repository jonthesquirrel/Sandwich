package io.github.jonthesquirrel.sandwich;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public class Sandwich extends JavaPlugin implements Listener {

    private FileConfiguration config;
    private Map<String, String> topLinks;
    private Map<String, String> bottomLinks;
    private Logger log;
    private int worldPadding;
    private int worldTop;
    private int worldBottom;
    private int worldHeight;

    @Override
    public void onEnable() {
        log = getLogger();
        getServer().getPluginManager().registerEvents(this, this);
        saveDefaultConfig();
        config = this.getConfig();
        worldPadding = config.getInt("worldPadding");
        worldTop = config.getInt("worldTop");
        worldBottom = config.getInt("worldBottom");
        worldHeight = worldTop - worldBottom;
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
        log.info("World padding: " + worldPadding);
        log.info("World top Y: " + worldTop);
        log.info("World bottom Y: " + worldBottom);
        log.info("World tops linked to: " + topLinks.toString());
        log.info("World bottoms linked to: " + bottomLinks.toString());
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        int currentY = event.getTo().getBlockY();
        if (currentY < worldBottom - worldPadding || currentY > worldTop + worldPadding) {
            World currentWorld = event.getTo().getWorld();
            String currentWorldName = currentWorld.getName();
            World targetWorld = null;
            String targetWorldName = null;
            int targetY = 0;
            if (currentY < worldBottom - worldPadding) {
                // go down
                if (bottomLinks.containsKey(currentWorldName)) {
                    targetWorldName = bottomLinks.get(currentWorldName);
                    targetWorld = Bukkit.getServer().getWorld(targetWorldName);
                    targetY = currentY + worldHeight + (worldPadding * 2);
                }
            } else {
                // go up
                if (topLinks.containsKey(currentWorldName)) {
                    targetWorldName = topLinks.get(currentWorldName);
                    targetWorld = Bukkit.getServer().getWorld(targetWorldName);
                    targetY = currentY - worldHeight - (worldPadding * 2);
                }
            }
            if (targetWorld != null) {
                Player player = event.getPlayer();
                int x = event.getTo().getBlockX();
                int z = event.getTo().getBlockZ();
                float yaw = event.getTo().getYaw();
                float pitch = event.getTo().getPitch();
                Vector velocity = event.getPlayer().getVelocity();
                Location targetLocation = new Location(targetWorld, x, targetY, z, yaw, pitch);
                player.teleport(targetLocation);
                player.setVelocity(velocity);
            }
        }
    }

}
