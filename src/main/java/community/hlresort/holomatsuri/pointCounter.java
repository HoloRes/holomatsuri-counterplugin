package community.hlresort.holomatsuri;

import org.bukkit.plugin.java.JavaPlugin;

public class pointCounter extends JavaPlugin {
    @Override
    public void onEnable() {
        saveDefaultConfig();
        getLogger().info("Plugin loaded!");
    }

    @Override
    public void onDisable() {

    }
}
