# RustPlugin - Minecraft Building System

A Spigot/Paper plugin for Minecraft 1.21.1 that provides a building system with structure placement, preview, and persistence.

## Features

- **Structure Types**: Fundament (4x1x4), Ściana (4x4x1), Sufit (4x1x4)
- **Visual Preview**: Green glass for placeable areas, red glass for blocked areas
- **Database Storage**: SQLite persistence for all placed structures
- **Multi-player**: Multiple players can build simultaneously
- **Smart Placement**: Structures snap to grid and cannot overlap
- **Cursor Following**: Preview follows player's cursor/raycast

## Installation

1. Build the plugin with Maven:
   ```bash
   mvn clean package
   ```

2. Copy the generated JAR to your server's `plugins/` folder

3. Restart your server

## Usage

1. Use `/buduj` command to enter building mode
2. Right-click the "Plan Budowy" (Blueprint) paper item to open the build menu
3. Select a structure type (Fundament, Ściana, Sufit)
4. The preview will follow your cursor in green/red glass
5. Left-click to place the structure
6. Repeat step 3-5 to place more structures

## Configuration

No configuration needed! The plugin uses SQLite database stored in `plugins/RustPlugin/database.db`

## Dependencies

- Spigot/Paper 1.21.1
- ProtocolLib
