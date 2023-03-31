package hillwalk.fr.petrpg.enums;

public enum ActionType {
    MELEE("melee"),
    RANGED("ranged"),
    HEALING("healing"),
    BUFF("buff"),
    DEBUFF("debuff");

    private String name;

    ActionType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static ActionType fromName(String name) {
        for (ActionType actionType : ActionType.values()) {
            if (actionType.getName().equalsIgnoreCase(name)) {
                return actionType;
            }
        }
        return null;
    }
}
