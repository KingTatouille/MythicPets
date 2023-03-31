package hillwalk.fr.petrpg.pets;

import hillwalk.fr.petrpg.enums.ActionType;
import org.bukkit.entity.EntityType;

import java.util.List;

public class CustomPet {
    private String name;
    private EntityType entityType;
    private boolean tameable;
    private List<String> spawnBiomes;

    private int minLevel;
    private int maxLevel;
    private double health;
    private double damage;
    private double speed;
    private ActionType actionType;

    public CustomPet(String name, EntityType entityType, boolean tameable, List<String> spawnBiomes, int minLevel, int maxLevel, double health, double damage, double speed, ActionType actionType) {
        this.name = name;
        this.entityType = entityType;
        this.tameable = tameable;
        this.spawnBiomes = spawnBiomes;
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
        this.health = health;
        this.damage = damage;
        this.speed = speed;
        this.actionType = actionType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public void setEntityType(EntityType entityType) {
        this.entityType = entityType;
    }

    public boolean isTameable() {
        return tameable;
    }

    public void setTameable(boolean tameable) {
        this.tameable = tameable;
    }

    public List<String> getSpawnBiomes() {
        return spawnBiomes;
    }

    public void setSpawnBiomes(List<String> spawnBiomes) {
        this.spawnBiomes = spawnBiomes;
    }

    public int getMinLevel() {
        return minLevel;
    }

    public void setMinLevel(int minLevel) {
        this.minLevel = minLevel;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    public double getHealth() {
        return health;
    }

    public void setHealth(double health) {
        this.health = health;
    }

    public double getDamage() {
        return damage;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }
}

