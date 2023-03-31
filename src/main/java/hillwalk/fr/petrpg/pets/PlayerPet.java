package hillwalk.fr.petrpg.pets;

import hillwalk.fr.petrpg.PetRPG;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class PlayerPet {
    private String name;
    private double health;
    private double damage;
    private double speed;
    private String actionType;
    private EntityType entityType;
    private Player owner;
    private int level;
    private int experience;

    public PlayerPet(String name, double health, double damage, double speed, String actionType, EntityType entityType, Player owner) {
        this.name = name;
        this.health = health;
        this.damage = damage;
        this.speed = speed;
        this.actionType = actionType;
        this.entityType = entityType;
        this.owner = owner;
        this.level = 1;
        this.experience = 0;
    }

    public void addExperience(int exp) {
        experience += exp;
        checkLevelUp();
    }

    private void checkLevelUp() {
        int requiredExp = getExperienceRequiredForNextLevel();
        while (experience >= requiredExp) {
            levelUp();
            experience -= requiredExp;
            requiredExp = getExperienceRequiredForNextLevel();
        }
    }

    private void levelUp() {
        level++;
        // Update pet's properties based on level
        health += 10;
        damage += 2;
        speed += 1;

        // Get the levelUp message from messages/messages.yml and replace %name% and %level% with the pet's name and level
        String levelUpMessage = PetRPG.getInstance().getMessagesConfig().getString("levelUp");
        levelUpMessage = levelUpMessage.replace("%name%", name).replace("%level%", Integer.toString(level));

        // Notify the owner of the pet's level up
        owner.sendMessage(levelUpMessage);
    }

    private int getExperienceRequiredForNextLevel() {
        // Example: quadratic experience requirement
        return level * level * 100;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public double getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public String getActionType() {
        return actionType;
    }


    public EntityType getEntityType() {
        return entityType;
    }


    public Player getOwner() {
        return owner;
    }


    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }
}
