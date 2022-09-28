package de.unistuttgart.overworldbackend.data.enums;

public enum Minigame {
    NONE,
    CHICKENSHOCK,
    BUGFINDER,
    CROSSWORDPUZZLE,
    FINITEQUIZ;

    /**
     * Checks if a minigame is configured
     * @param minigame minigame to be checked
     * @return true if minigame is configured, false otherwise
     */
    public static boolean isConfigured(final Minigame minigame) {
        return minigame != null && minigame != Minigame.NONE;
    }
}
