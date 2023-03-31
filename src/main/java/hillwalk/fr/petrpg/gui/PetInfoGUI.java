package hillwalk.fr.petrpg.gui;

import hillwalk.fr.petrpg.PetRPG;
import hillwalk.fr.petrpg.manager.PetManager;
import hillwalk.fr.petrpg.pets.CustomPet;
import hillwalk.fr.petrpg.pets.PlayerPet;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PetInfoGUI implements InventoryHolder {

    private final Inventory inventory;

    public PetInfoGUI(Player player, CustomPet pet, PetManager petManager) {
        inventory = Bukkit.createInventory(this, 27, ChatColor.translateAlternateColorCodes('&', PetRPG.getInstance().getMessagesConfig().getString("inventory.petInfoTitle")) + " " + pet.getName());

        // Créer un ItemStack pour invoquer le pet
        ItemStack spawnPetItem = new ItemStack(Material.LIME_CONCRETE);
        ItemMeta spawnPetMeta = spawnPetItem.getItemMeta();
        spawnPetMeta.setDisplayName(ChatColor.GREEN + "Spawn Pet");
        spawnPetItem.setItemMeta(spawnPetMeta);

        // Créer un ItemStack pour afficher les informations du pet
        ItemStack petInfoItem = new ItemStack(Material.BOOK);
        ItemMeta petInfoMeta = petInfoItem.getItemMeta();

        petInfoMeta.setDisplayName(ChatColor.BLUE + "Pet Information");
        petInfoMeta.setLore(petManager.getPlayerPetInfo(player, pet.getName()));
        petInfoItem.setItemMeta(petInfoMeta);

        // Créer un ItemStack pour relâcher le pet
        ItemStack releasePetItem = new ItemStack(Material.RED_CONCRETE);
        ItemMeta releasePetMeta = releasePetItem.getItemMeta();
        releasePetMeta.setDisplayName(ChatColor.RED + "Release Pet");
        releasePetItem.setItemMeta(releasePetMeta);

        // Créer un ItemStack pour voir l'inventaire du pet (si applicable)
        ItemStack petInventoryItem = new ItemStack(Material.CHEST);
        ItemMeta petInventoryMeta = petInventoryItem.getItemMeta();
        petInventoryMeta.setDisplayName(ChatColor.YELLOW + "Pet Inventory");
        petInventoryItem.setItemMeta(petInventoryMeta);

        // Placer les ItemStacks dans l'inventaire
        inventory.setItem(10, spawnPetItem);
        inventory.setItem(12, petInfoItem);
        inventory.setItem(14, releasePetItem);
        inventory.setItem(16, petInventoryItem);
    }


    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
