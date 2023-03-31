package hillwalk.fr.petrpg.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class Config {
    private FileConfiguration config;
    private File configFile;

    public Config(String fileName, JavaPlugin plugin) {
        configFile = new File(plugin.getDataFolder(), fileName);

        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            plugin.saveResource(fileName, false);
        }

        config = YamlConfiguration.loadConfiguration(configFile);
    }


    public String getString(String path) {
        return config.getString(path);
    }


    public ConfigurationSection getConfigurationSection(String path) {
        return config.getConfigurationSection(path);
    }

    private void save() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

