package hillwalk.fr.petrpg.commands;

import hillwalk.fr.petrpg.manager.PetManager;
import hillwalk.fr.petrpg.pets.CustomPet;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.Map;

public class SpawnMobCommand implements CommandExecutor {

    private final Map<EntityType, CustomPet> customPets;

    private PetManager petManager;

    public SpawnMobCommand(Map<EntityType, CustomPet> customPets, PetManager petManager) {
        this.customPets = customPets;
        this.petManager = petManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length != 2) {
            sender.sendMessage("Usage: /spawnmob <EntityType> <level>");
            return true;
        }

        Player player = (Player) sender;
        String entityTypeString = args[0];
        int level;

        try {
            level = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage("Invalid level. Please enter a valid number.");
            return true;
        }

        EntityType entityType = EntityType.valueOf(args[0].toUpperCase());
        System.out.println("Trying to spawn entityType: " + entityType);

        if (customPets.containsKey(entityType)) {
            CustomPet customPet = customPets.get(entityType);
            Location location = player.getLocation();
            // Call your spawnCustomPet method here
            petManager.spawnCustomPet(customPet, location, level);
            sender.sendMessage("Spawned a " + customPet.getName() + " at level " + level + ".");
        } else {
            sender.sendMessage("This EntityType is not a custom pet.");
        }

        return true;
    }
}
