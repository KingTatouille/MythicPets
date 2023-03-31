package hillwalk.fr.petrpg.manager;

import hillwalk.fr.petrpg.PetRPG;
import hillwalk.fr.petrpg.config.Config;
import hillwalk.fr.petrpg.enums.ActionType;
import hillwalk.fr.petrpg.pets.CustomPet;
import hillwalk.fr.petrpg.pets.PlayerPet;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import net.wesjd.anvilgui.AnvilGUI;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class PetManager {
    private List<PlayerPet> pets;
    private Map<EntityType, CustomPet> customPets;

    public Map<UUID, LivingEntity> activePets;

    private Set<UUID> customEntities;

    private Set<PlayerPet> playerPets;


    public PetManager() {
        pets = new ArrayList<>();
        customPets = new HashMap<>();
        customEntities = new HashSet<>();
        activePets = new HashMap<>();
        playerPets = new HashSet<>();
    }

    public void addCustomEntity(Entity entity) {
        customEntities.add(entity.getUniqueId());
    }

    public boolean isCustomEntity(Entity entity) {
        return customEntities.contains(entity.getUniqueId());
    }

    public void addPet(PlayerPet pet) {
        pets.add(pet);
    }

    public void removePet(PlayerPet pet) {
        pets.remove(pet);
    }

    public PlayerPet getPet(Player player, EntityType entityType) {
        for (PlayerPet playerPet : playerPets) {
            if (playerPet.getOwner().equals(player) && playerPet.getEntityType().equals(entityType)) {
                return playerPet;
            }
        }
        return null;
    }

    public EntityType getEntityTypeFromPlayerConfig(Player player) {

        File playerDataFolder = new File(PetRPG.getInstance().getDataFolder(), "data");
        File playerFile = new File(playerDataFolder, player.getUniqueId().toString() + ".yml");
        FileConfiguration playerConfig = YamlConfiguration.loadConfiguration(playerFile);

        ConfigurationSection petsSection = playerConfig.getConfigurationSection("pets");

        if (petsSection != null) {
            for (String petSection : petsSection.getKeys(false)) {
                String entityTypeStr = petsSection.getString(petSection + ".entityType");
                if (entityTypeStr != null) {
                    try {
                        EntityType entityType = EntityType.valueOf(entityTypeStr);
                        return entityType;
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return null;
    }

    public int getPetIndexFromPlayerConfig(Player player, EntityType entityType) {
        File playerDataFolder = new File(PetRPG.getInstance().getDataFolder(), "data");

        File playerFile = new File(playerDataFolder, player.getUniqueId() + ".yml");
        if (!playerFile.exists()) {
            return -1;
        }

        FileConfiguration playerConfig = YamlConfiguration.loadConfiguration(playerFile);
        List<String> petList = playerConfig.getStringList("pets");
        for (int i = 0; i < petList.size(); i++) {
            String petName = petList.get(i);
            if (petName.equalsIgnoreCase(entityType.name())) {
                return i;
            }
        }

        return -1;
    }

    public UUID getActivePetUUID(Player player) {
        for (Map.Entry<UUID, LivingEntity> entry : activePets.entrySet()) {
            if (entry.getValue().getUniqueId().equals(player.getUniqueId())) {
                return entry.getKey();
            }
        }
        return null;
    }

    public List<String> getPlayerPetInfo(Player player, String petName) {

        File playerDataFolder = new File(PetRPG.getInstance().getDataFolder(), "data");
        File playerFile = new File(playerDataFolder, player.getUniqueId().toString() + ".yml");
        FileConfiguration playerConfig = YamlConfiguration.loadConfiguration(playerFile);

        ConfigurationSection petsSection = playerConfig.getConfigurationSection("pets");

        if (petsSection != null) {
            ConfigurationSection petSection = petsSection.getConfigurationSection(petName);
            if (petSection != null) {
                try {
                    double health = petSection.getDouble("health");
                    double damage = petSection.getDouble("damage");
                    double speed = petSection.getDouble("speed");
                    String actionType = petSection.getString("actionType");
                    int level = petSection.getInt("level");

                    List<String> infoList = new ArrayList<>();
                    infoList.add(ChatColor.translateAlternateColorCodes('&', PetRPG.getInstance().getMessagesConfig().getString("pet.informations.level")) + " " + level);
                    infoList.add(ChatColor.translateAlternateColorCodes('&', PetRPG.getInstance().getMessagesConfig().getString("pet.informations.health")) + " " + health);
                    infoList.add(ChatColor.translateAlternateColorCodes('&', PetRPG.getInstance().getMessagesConfig().getString("pet.informations.damage")) + " " + damage);
                    infoList.add(ChatColor.translateAlternateColorCodes('&', PetRPG.getInstance().getMessagesConfig().getString("pet.informations.speed")) + " " + speed);
                    infoList.add(ChatColor.translateAlternateColorCodes('&', PetRPG.getInstance().getMessagesConfig().getString("pet.informations.actiontype")) + " " + actionType);

                    return infoList;
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public int getPetLevelByConfig(Player player, String petName) {

        File playerDataFolder = new File(PetRPG.getInstance().getDataFolder(), "data");
        File playerFile = new File(playerDataFolder, player.getUniqueId().toString() + ".yml");
        FileConfiguration playerConfig = YamlConfiguration.loadConfiguration(playerFile);

        ConfigurationSection petsSection = playerConfig.getConfigurationSection("pets");

        if (petsSection != null) {
            ConfigurationSection petSection = petsSection.getConfigurationSection(petName);
            System.out.println(petSection);
            if (petSection != null) {
                try {
                    System.out.println("entrée.");
                    int level = petSection.getInt("level");
                    System.out.println(level);

                    return level;
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }
        }
        return 0;
    }


    public void startPetNameChange(Player player, PlayerPet pet) {
        new AnvilGUI.Builder()
                .onClose(playerClosing -> {
                    // Logic on GUI close if needed
                })
                .onComplete((playerChangingName, newName) -> {
                    // Check if the new name is valid and not too long
                    if (newName.length() > 0 && newName.length() <= 16) {
                        // Update the pet's name
                        pet.setName(newName);

                        // Save the new name to the configuration file
                        savePetNameToConfig(player, pet, newName);

                        // Inform the player that the name has been changed successfully
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', PetRPG.getInstance().getMessagesConfig().getString("pet.petNameChanged")) + newName);
                        return AnvilGUI.Response.close();
                    } else {
                        // Inform the player that the name is invalid
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', PetRPG.getInstance().getMessagesConfig().getString("pet.invalidPetName")));
                        return AnvilGUI.Response.text("Invalid name");
                    }
                })
                .text("Enter name")
                .itemLeft(new ItemStack(Material.NAME_TAG))
                .title(ChatColor.translateAlternateColorCodes('&', PetRPG.getInstance().getMessagesConfig().getString("inventory.changePetNameTitle")))
                .plugin(PetRPG.getInstance())
                .open(player);
    }

    private void savePetNameToConfig(Player player, PlayerPet pet, String newName) {
        // Implémentez la logique pour enregistrer le nouveau nom dans le fichier de configuration
    }


    public void summonPet(Player player, int level, String petName) {

        // Si le joueur a déjà un monstre invoqué, ne faites rien
        if (activePets.containsKey(player.getUniqueId())) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', PetRPG.getInstance().getMessagesConfig().getString("inventory.alreadyPet")));
            return;
        }

        // Récupérer les informations du CustomPet à partir du fichier de configuration
        File playerDataFolder = new File(PetRPG.getInstance().getDataFolder(), "data");
        File playerFile = new File(playerDataFolder, player.getUniqueId().toString() + ".yml");
        FileConfiguration playerConfig = YamlConfiguration.loadConfiguration(playerFile);
        EntityType petEntityType = EntityType.valueOf(playerConfig.getString("pets." + petName + ".entityType"));
        boolean petTameable = playerConfig.getBoolean("pets." + petName + ".tameable");
        List<String> petSpawnBiomes = playerConfig.getStringList("pets." + petName + ".spawnBiomes");
        int petMinLevel = playerConfig.getInt("pets." + petName + ".minLevel");
        int petMaxLevel = playerConfig.getInt("pets." + petName + ".maxLevel");
        double petHealth = playerConfig.getDouble("pets." + petName + ".health");
        double petDamage = playerConfig.getDouble("pets." + petName + ".damage");
        double petSpeed = playerConfig.getDouble("pets." + petName + ".speed");
        ActionType petActionType = ActionType.valueOf(playerConfig.getString("pets." + petName + ".actionType"));


        // Créer l'objet CustomPet correspondant
        CustomPet customPet = new CustomPet(petName, petEntityType, petTameable, petSpawnBiomes, petMinLevel, petMaxLevel, petHealth, petDamage, petSpeed, petActionType);

        // Invoquer le monstre et stockez une référence à l'entité dans la HashMap
        if (customPet != null) {
            Location location = player.getLocation();
            LivingEntity petEntity = spawnCustomPet(customPet, location, level);
            activePets.put(player.getUniqueId(), petEntity);
            makePetFollow(player, petEntity);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', PetRPG.getInstance().getMessagesConfig().getString("pet.summonedPet")));
        }
    }


    public void loadCustomPetsFromConfig() {
        loadCustomEntitiesFromConfig(PetRPG.getInstance().getAnimalsConfig(), PetRPG.getInstance().customPets);
        loadCustomEntitiesFromConfig(PetRPG.getInstance().getMonstersConfig(), PetRPG.getInstance().customPets);
    }

    private void loadCustomEntitiesFromConfig(Config config, Map<EntityType, CustomPet> customPets) {
        ConfigurationSection petsSection = config.getConfigurationSection("pets");

        if (petsSection == null) {
            PetRPG.getInstance().getLogger().warning("No 'pets' section found in the config file. Skipping...");
            return;
        }

        for (String key : petsSection.getKeys(false)) {
            ConfigurationSection petSection = petsSection.getConfigurationSection(key);
            String name = petSection.getString("name");
            EntityType entityType = EntityType.valueOf(petSection.getString("entityType"));
            boolean tamable = petSection.getBoolean("tamable");
            List<String> spawnBiomes = petSection.getStringList("spawnBiomes");
            int minLevel = petSection.getInt("minLevel");
            int maxLevel = petSection.getInt("maxLevel");
            double health = petSection.getDouble("health");
            double damage = petSection.getDouble("damage");
            double speed = petSection.getDouble("speed");
            ActionType actionType = ActionType.valueOf(petSection.getString("actionType"));

            CustomPet customPet = new CustomPet(name, entityType, tamable, spawnBiomes, minLevel, maxLevel, health, damage, speed, actionType);
            customPets.put(entityType, customPet);
            System.out.println("Loaded custom pet: " + customPet.getName() + " (" + entityType + ")");
        }
    }

    public void savePlayerPet(UUID uuid, PlayerPet playerPet) {
        File playerDataFolder = new File(PetRPG.getInstance().getDataFolder(), "data");
        File playerFile = new File(playerDataFolder, uuid.toString() + ".yml");

        FileConfiguration playerConfig = YamlConfiguration.loadConfiguration(playerFile);

        // Save player information
        playerConfig.set("player.name", playerPet.getName());
        playerConfig.set("player.level", playerPet.getLevel());

        // Save pet information
        String petPath = "pets." + playerPet.getName();
        playerConfig.set(petPath + ".level", playerPet.getLevel());
        playerConfig.set(petPath + ".entityType", playerPet.getEntityType().name());
        playerConfig.set(petPath + ".health", playerPet.getHealth());
        playerConfig.set(petPath + ".damage", playerPet.getDamage());
        playerConfig.set(petPath + ".speed", playerPet.getSpeed());
        playerConfig.set(petPath + ".actionType", playerPet.getActionType());

        try {
            playerConfig.save(playerFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void makePetFollow(Player player, LivingEntity petEntity) {
        System.out.println("makePetFollow() - Start");
        System.out.println("makePetFollow() - activePets: " + activePets.toString());
        System.out.println("makePetFollow() - player: " + player.getName());
        System.out.println("makePetFollow() - petEntity: " + petEntity.getName());
        if (activePets.containsValue(petEntity)) {
            UUID playerUUID = null;
            for (Map.Entry<UUID, LivingEntity> entry : activePets.entrySet()) {
                if (entry.getValue().equals(petEntity)) {
                    playerUUID = entry.getKey();
                    break;
                }
            }
            System.out.println("makePetFollow() - playerUUID: " + playerUUID);

            Player owner = Bukkit.getPlayer(playerUUID);
            System.out.println("makePetFollow() - owner: " + owner);
            if (owner != null && owner.isOnline()) {
                Tameable pet = (Tameable) petEntity;
                pet.setOwner(owner);
                Entity entity = (Entity) petEntity;
                ((Mob) entity).setTarget(owner);
                ((Mob) entity).setAI(true);
                System.out.println("makePetFollow() - Pet set to follow owner: " + owner.getName());
            } else {
                petEntity.remove();
                activePets.remove(playerUUID);
                System.out.println("makePetFollow() - Owner is not online, removing pet from activePets");
            }
        } else {
            player.sendMessage("This is not your active pet.");
            System.out.println("makePetFollow() - Pet entity is not an active pet");
        }
        System.out.println("makePetFollow() - End");
    }




    public LivingEntity spawnCustomPet(CustomPet customPet, Location location, int level) {
        EntityType entityType = customPet.getEntityType();
        LivingEntity entity = (LivingEntity) location.getWorld().spawnEntity(location, entityType);

        // Set the custom name and level
        setMobNameWithLevel(entity, customPet.getName(), level);

        if (entity instanceof Monster) {
            // Définissez les attributs spécifiques aux monstres.
            AttributeInstance maxHealthAttribute = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH);
            if (maxHealthAttribute != null) {
                maxHealthAttribute.setBaseValue(customPet.getHealth());
            }

            AttributeInstance attackDamageAttribute = entity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);
            if (attackDamageAttribute != null) {
                attackDamageAttribute.setBaseValue(customPet.getDamage());
            }
        } else if (entity instanceof Animals) {
            // Définissez les attributs spécifiques aux animaux.
            AttributeInstance movementSpeedAttribute = entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
            if (movementSpeedAttribute != null) {
                movementSpeedAttribute.setBaseValue(customPet.getSpeed());
            }
        }

        // Adjust the pet's health, damage, and speed based on its level
        adjustPetStats(entity, customPet, level);

        // Set any other custom attributes or behaviors for the pet
        // For example, you can set the pet to be persistent, so it doesn't despawn
        entity.setPersistent(true);

        customPets.put(entityType, customPet);
        addCustomEntity(entity);
        return entity;
    }


    private void setMobNameWithLevel(LivingEntity entity, String name, int level) {
        // Set the custom name and make it visible
        entity.setCustomName(name + " Lvl " + level);
        entity.setCustomNameVisible(true);
    }

    private void adjustPetStats(LivingEntity entity, CustomPet customPet, int level) {
        // Adjust the pet's health, damage, and speed based on its level
        // You can customize the formulas to scale the stats as needed
        // Health scaling example
        double baseHealth = customPet.getHealth();
        double adjustedHealth = baseHealth + (level * 2); // You can change the formula
        entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(adjustedHealth);
        entity.setHealth(adjustedHealth);

    // Speed scaling example
        double baseSpeed = customPet.getSpeed();
        double adjustedSpeed = baseSpeed + (level * 0.005); // You can change the formula
        entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(adjustedSpeed);

    // Add any other attribute adjustments here

    // If the custom pet is a monster, adjust its damage attribute as well
        if (entity instanceof Monster) {
            double baseDamage = customPet.getDamage();
            double adjustedDamage = baseDamage + (level * 1.5); // You can change the formula
            entity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(adjustedDamage);
        }
    }
}
