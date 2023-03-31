package hillwalk.fr.petrpg.listener;

import hillwalk.fr.petrpg.PetRPG;
import hillwalk.fr.petrpg.enums.ActionType;
import hillwalk.fr.petrpg.gui.PetInfoGUI;
import hillwalk.fr.petrpg.manager.PetManager;
import hillwalk.fr.petrpg.pets.CustomPet;
import hillwalk.fr.petrpg.pets.PlayerPet;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.io.File;
import java.util.Map;
import java.util.Random;

public class CustomTamer implements Listener {

    private PetManager petManager;
    private Map<EntityType, CustomPet> customPets;

    public CustomTamer(PetManager petManager, Map<EntityType, CustomPet> customPets) {
        this.petManager = petManager;
        this.customPets = customPets;
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();
        EntityType entityType = entity.getType();

        // Check if the player is holding an apple
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        if (itemInHand.getType() != Material.APPLE) {
            PetRPG.getInstance().getLogger().info("Player is not holding an apple.");
            return;
        }

        // Si le joueur est en train de renvoyer son monstre
        if (player.isSneaking() && petManager.activePets.containsKey(player.getUniqueId()) && petManager.activePets.get(player.getUniqueId()) == entity) {
            entity.remove();
            petManager.activePets.remove(player.getUniqueId());
            player.sendMessage("Your pet has been returned.");
            return;
        }

        if (petManager.isCustomEntity(entity)) {
            CustomPet customPet = customPets.get(entityType);

            if (customPet.isTameable()) {
                // Vérifier si l'entité est déjà apprivoisée
                if (entity instanceof Tameable) {
                    Tameable tameableEntity = (Tameable) entity;
                    if (tameableEntity.isTamed()) {
                        if (tameableEntity.getOwner() == player) {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', PetRPG.getInstance().getMessagesConfig().getString("pet.alreadyTamedByYou")));
                        } else {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', PetRPG.getInstance().getMessagesConfig().getString("pet.alreadyTamedByAnother")));
                        }
                        return;
                    }
                }

                // Create a PlayerPet from the CustomPet
                String name = customPet.getName();
                double health = customPet.getHealth();
                double damage = customPet.getDamage();
                double speed = customPet.getSpeed();
                String actionType = String.valueOf(customPet.getActionType());

                PlayerPet playerPet = new PlayerPet(name, health, damage, speed, actionType, entityType, player);

                // Add the tamed pet to the PetManager
                petManager.addPet(playerPet);

                // Create the player's configuration file
                petManager.savePlayerPet(player.getUniqueId(), playerPet);

                // Assign a unique support skill to the tamed pet
                ActionType uniqueSupportSkill = generateUniqueSupportSkill();
                entity.setMetadata("uniqueSupportSkill", new FixedMetadataValue(PetRPG.getInstance(), uniqueSupportSkill));

                // Remove the original entity
                entity.remove();

                // Remove one apple from the player's inventory
                itemInHand.setAmount(itemInHand.getAmount() - 1);

                // Send a message to the player
                String tamedMessage = PetRPG.getInstance().getMessagesConfig().getString("tamed").replace("%name%", name);
                player.sendMessage(tamedMessage);

                PetRPG.getInstance().getLogger().info("Pet tamed successfully.");
            } else {
                PetRPG.getInstance().getLogger().info("Entity is not tameable.");
            }
        } else {
            PetRPG.getInstance().getLogger().info("Entity is not a custom pet.");
        }
    }


    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String petListTitle = ChatColor.translateAlternateColorCodes('&', PetRPG.getInstance().getMessagesConfig().getString("inventory.petListTitle"));
        String petInfoGui = ChatColor.translateAlternateColorCodes('&', PetRPG.getInstance().getMessagesConfig().getString("inventory.petInfoTitle"));

        EntityType entityType = petManager.getEntityTypeFromPlayerConfig((Player) event.getWhoClicked());
        Player player = (Player) event.getWhoClicked();
        PlayerPet playerPet = petManager.getPet(player, entityType);
        CustomPet pet2 = customPets.get(entityType);

        if (event.getView().getTitle().startsWith(petInfoGui)) {
            event.setCancelled(true);
            if (event.getClickedInventory() == event.getView().getTopInventory()) {
                ItemStack clickedItem = event.getCurrentItem();
                if (clickedItem != null && clickedItem.getType() != Material.AIR) {
                    String displayName = clickedItem.getItemMeta().getDisplayName();
                    PlayerPet pet = petManager.getPet(player, entityType);

                    // Ajouter un message de débogage pour vérifier que l'événement est bien appelé

                    if (displayName.equals(ChatColor.GREEN + "Spawn Pet")) {

                        // Invoquer le pet
                        petManager.summonPet(player, petManager.getPetLevelByConfig(player, pet2.getName()), pet2.getName());

                        player.closeInventory();

                    } else if (displayName.equals(ChatColor.RED + "Release Pet")) {
                        // Relâcher le pet
                        petManager.removePet(pet);
                        player.closeInventory();
                    } else if (displayName.equals(ChatColor.AQUA + "Change Pet Name")) {
                        // Changer le nom du pet
                        petManager.startPetNameChange(player, pet);
                        player.closeInventory();
                    }
                }
            }
        }



        //Premier inventaire.
        if (event.getView().getTitle().equals(petListTitle)) {
            event.setCancelled(true);
            if (event.getClickedInventory() == event.getView().getTopInventory()) {

                ItemStack clickedItem = event.getCurrentItem();

                if (clickedItem != null && clickedItem.getType() != Material.AIR) {
                    if (entityType != null) {
                        // Check if the player shift-clicks the item
                        if (event.isShiftClick()) {

                            if (playerPet != null && playerPet.getEntityType().equals(entityType)) {
                                petManager.removePet(playerPet);
                            }
                            player.closeInventory();
                        } else {
                            CustomPet pet = customPets.get(entityType);
                            if (pet != null) {
                                // Open the PetInfoGUI
                                PetInfoGUI petInfoGUI = new PetInfoGUI(player, pet, petManager);
                                player.openInventory(petInfoGUI.getInventory());
                            }
                        }
                    }
                }

            }
        }
    }


    private ActionType generateUniqueSupportSkill() {
        ActionType[] supportSkills = {ActionType.HEALING, ActionType.BUFF, ActionType.DEBUFF};
        int randomIndex = new Random().nextInt(supportSkills.length);
        return supportSkills[randomIndex];
    }
}
