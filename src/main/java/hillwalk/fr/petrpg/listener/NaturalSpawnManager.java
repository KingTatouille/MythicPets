package hillwalk.fr.petrpg.listener;



import hillwalk.fr.petrpg.manager.PetManager;
import hillwalk.fr.petrpg.pets.CustomPet;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class NaturalSpawnManager implements Listener {

    private Random random;
    private Map<EntityType, CustomPet> customPets;

    private PetManager petManager;

    public NaturalSpawnManager(PetManager petManager) {
        random = new Random();
        customPets = new HashMap<>();
        this.petManager = petManager;
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL) {
            LivingEntity entity = event.getEntity();
            EntityType entityType = entity.getType();

            if (customPets.containsKey(entityType)) {
                CustomPet customPet = customPets.get(entityType);

                // Find the nearest player
                Player nearestPlayer = null;
                double minDistance = Double.MAX_VALUE;
                for (Player player : entity.getWorld().getPlayers()) {
                    double distance = player.getLocation().distance(entity.getLocation());
                    if (distance < minDistance) {
                        minDistance = distance;
                        nearestPlayer = player;
                    }
                }

                // Determine the level of the pet based on the nearest player's level
                int petLevel = 1;
                if (nearestPlayer != null) {
                    petLevel = nearestPlayer.getLevel(); // You can adjust this to scale with player level
                }

                // Adjust pet stats based on the level and spawn it
                petManager.spawnCustomPet(customPet, entity.getLocation(), petLevel);

                // Remove the original entity
                event.setCancelled(true);
            }
        }

    }



    // Add methods for determining pet level based on nearby player levels,
    // generating pet stats based on level, and spawning custom pets
}
