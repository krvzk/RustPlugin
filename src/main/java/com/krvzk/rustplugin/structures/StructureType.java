package com.krvzk.rustplugin.structures;

public enum StructureType {
    FUNDAMENT("Fundament", 5, 1, 5),      // 5x1x5
    SCIANA("Ściana", 5, 5, 1),             // 5x5x1 (depth)
    SUFIT("Sufit", 5, 1, 5);               // 5x1x5

    private final String displayName;
    private final int width;  // X axis
    private final int height; // Y axis
    private final int depth;  // Z axis

    StructureType(String displayName, int width, int height, int depth) {
        this.displayName = displayName;
        this.width = width;
        this.height = height;
        this.depth = depth;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getDepth() {
        return depth;
    }
}
