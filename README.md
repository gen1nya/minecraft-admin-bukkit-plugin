# Minecraft Admin Plugin

Server administration plugin providing `/playerlist` and `/serverstat` commands. Returns player information and server statistics in JSON format.

Supports both **Bukkit/Paper** and **Forge** servers.

## Features

- `/playerlist` — returns whitelisted players with their status (online, OP, banned, gamemode)
- `/serverstat` — returns server statistics (version, online players, memory usage, TPS)

Both commands output JSON, making them useful for external monitoring tools and admin panels.

## Requirements

| Platform | Version |
|----------|---------|
| Bukkit/Paper | 1.20.4+ |
| Forge | 1.20.1+ |

## Installation

### Bukkit/Paper
Copy `WebAdminPlugin-Bukkit.jar` to the `plugins/` folder and restart the server.

### Forge
Copy `WebAdminPlugin-Forge.jar` to the `mods/` folder and restart the server.

**Note:** Forge version is server-side only — clients don't need to install it.

## Building

```bash
# Build all modules
./gradlew build

# Build only Bukkit
./gradlew :bukkit:build

# Build only Forge
./gradlew :forge:build
```

Output JARs:
- `bukkit/build/libs/WebAdminPlugin-Bukkit.jar`
- `forge/build/libs/WebAdminPlugin-Forge.jar`

## Usage

### /playerlist

Returns JSON array of whitelisted players.

```bash
/playerlist
```

Response:
```json
[
  {
    "name": "Player1",
    "uuid": "12c1b1a7-68f4-452b-89ca-846f5927df5d",
    "isOp": true,
    "isOnline": false,
    "isBanned": false,
    "gameMode": "unknown"
  }
]
```

### /serverstat

Returns JSON object with server statistics.

```bash
/serverstat
```

Response:
```json
{
  "version": "1.20.1",
  "onlinePlayers": 0,
  "memoryUsedMB": 3444,
  "memoryAllocatedMB": 16384,
  "tps1m": 20.0,
  "tps5m": 20.0,
  "tps15m": 20.0
}
```

## Permissions

### Bukkit/Paper
| Permission | Description | Default |
|------------|-------------|---------|
| `playerlist.use` | Allow `/playerlist` | true |
| `serverstat.use` | Allow `/serverstat` | true |

Commands also require OP or console access.

### Forge
Commands require OP level 2+ or console access.

## Project Structure

```
minecraft-admin-plugin/
├── common/                     # Shared code (Kotlin)
│   └── src/main/kotlin/
│       └── online/ebatel/common/
│           ├── PlayerData.kt
│           ├── ServerStats.kt
│           ├── JsonSerializer.kt
│           └── MemoryUtils.kt
│
├── bukkit/                     # Bukkit/Paper plugin (Kotlin)
│   └── src/main/kotlin/
│       └── online/ebatel/bukkit/
│           ├── WebAdminPlugin.kt
│           ├── PlayerListCommand.kt
│           ├── ServerStatsCommand.kt
│           └── ext.kt
│
└── forge/                      # Forge mod (Java)
    └── src/main/java/
        └── online/ebatel/forge/
            ├── WebAdminMod.java
            ├── TpsTracker.java
            └── commands/
                ├── PlayerListCommand.java
                └── ServerStatsCommand.java
```

## License

MIT
