package hillwalk.fr.petrpg;

import hillwalk.fr.petrpg.commands.PetListCommand;
import hillwalk.fr.petrpg.commands.SpawnMobCommand;
import hillwalk.fr.petrpg.config.Config;
import hillwalk.fr.petrpg.listener.CustomTamer;
import hillwalk.fr.petrpg.manager.PetManager;
import hillwalk.fr.petrpg.pets.CustomPet;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class PetRPG extends JavaPlugin {

    private static PetRPG instance;
    private PetManager petManager;

    public Map<EntityType, CustomPet> customPets;
    private Config headsConfig;
    private Config messagesConfig;
    private Config animalsConfig;
    private Config monstersConfig;

    @Override
    public void onEnable() {

        // Définir l'instance de la classe principale
        instance = this;

        petManager = new PetManager();

        customPets = new HashMap<>();

        animalsConfig = new Config("wild/animals.yml", this);

        monstersConfig = new Config("wild/monsters.yml", this);

        messagesConfig = new Config("messages/messages.yml", this);

        headsConfig = new Config("inventory/heads.yml", this);

        petManager.loadCustomPetsFromConfig();

        System.out.println("Custom pets loaded: " + customPets.size());


        // Register the spawnmob command
        getCommand("pets").setExecutor(new PetListCommand());
        getCommand("spawnmob").setExecutor(new SpawnMobCommand(customPets, petManager));


        // Register CustomTamer events
        CustomTamer customTamer = new CustomTamer(petManager, customPets);
        getServer().getPluginManager().registerEvents(customTamer, this);

    }


    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }


    public Config getMessagesConfig() {
        return messagesConfig;
    }

    public static PetRPG getInstance() {
        return instance;
    }

    public Config getHeadsConfig(){return headsConfig;}

    public Config getAnimalsConfig() {
        return animalsConfig;
    }

    public Config getMonstersConfig() {
        return monstersConfig;
    }


}
