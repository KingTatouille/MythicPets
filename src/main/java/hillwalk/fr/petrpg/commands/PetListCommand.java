package hillwalk.fr.petrpg.commands;

import hillwalk.fr.petrpg.PetRPG;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PetListCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;

        // Open the GUI with the list of pets
        openPetListGUI(player);

        return true;
    }

    private void openPetListGUI(Player player) {
        File playerDataFolder = new File(PetRPG.getInstance().getDataFolder(), "data");
        File playerFile = new File(playerDataFolder, player.getUniqueId().toString() + ".yml");
        FileConfiguration playerConfig = YamlConfiguration.loadConfiguration(playerFile);

        // Create the inventory
        int inventorySize = 9 * 3; // Adjust the size as needed
        String inventoryTitle = ChatColor.GREEN + "Your Pets";
        Inventory petListInventory = Bukkit.createInventory(null, inventorySize, inventoryTitle);

        // Add the pet items to the inventory
        ConfigurationSection petsSection = playerConfig.getConfigurationSection("pets");
        if (petsSection != null) {
            for (String petName : petsSection.getKeys(false)) {
                EntityType entityType = EntityType.valueOf(petsSection.getString(petName + ".entityType"));

                // Create an ItemStack for each pet
                ItemStack petItem = createPetItem(entityType);

                // Set the display name and lore
                ItemMeta petItemMeta = petItem.getItemMeta();
                petItemMeta.setDisplayName(ChatColor.GREEN + petName);

                petItem.setItemMeta(petItemMeta);

                // Add the ItemStack to the inventory
                petListInventory.addItem(petItem);
            }
        }

        // Open the inventory for the player
        player.openInventory(petListInventory);
    }

    public ItemStack createPetItem(EntityType entityType) {
        String name = entityType.toString();
        String displayName = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
        String owner = PetRPG.getInstance().getHeadsConfig().getString("heads." + name + ".meta.owner");

        ItemStack petItem = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) petItem.getItemMeta();

        if (owner == null) {
            meta.setOwningPlayer(Bukkit.getOfflinePlayer(UUID.fromString("7097e840-3f8c-45c3-9194-4ea88ad4d9f9")));
        } else {
            meta.setOwningPlayer(Bukkit.getOfflinePlayer(owner));
        }

        meta.setDisplayName(ChatColor.RESET + displayName);
        petItem.setItemMeta(meta);

        return petItem;
    }




}
