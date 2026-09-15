package com.krvzk.rustplugin.structures;

public enum StructureType {
    FUNDAMENT("Fundament", 4, 1, 4),      // 4x1x4
    SCIANA("Ściana", 4, 4, 1),             // 4x4x1 (depth)
    SUFIT("Sufit", 4, 1, 4);               // 4x1x4

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
