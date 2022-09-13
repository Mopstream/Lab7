package com.mopstream.common.data;

/**
 * Enumerates the difficulties of the labworks.
 */
public enum Difficulty {
    VERY_EASY,
    IMPOSSIBLE,
    TERRIBLE;

    /**
     * Generates a beautiful list of enum string values.
     *
     * @return String with all enum values splitted by comma.
     */
    public static String nameList() {
        String nameList = "";
        for (Difficulty difficulty : values()) {
            nameList += difficulty.name() + ", ";
        }
        return nameList.substring(0, nameList.length() - 2);
    }
}
