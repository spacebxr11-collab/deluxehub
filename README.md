# DeluxeHub

**DeluxeHub** is a premium, all-in-one Hub/Lobby management plugin designed specifically for Minecraft 1.8.8 servers. It combines essential hub features—like server selectors, launchpads, and holograms—into a single, lightweight plugin, optimized for performance and ease of use.

## ✨ Features

-   **🖥️ Server Selector GUI**: Fully configurable inventory menu to navigation players to other servers (Survival, Creative, Skywars, etc.).
-   **🚀 Pro Launchpads**:
    -   Create launchpads anywhere with `/launchpad create`.
    -   Includes Sound (Bat Takeoff) and Particle (Flame) effects.
    -   Configurable velocity and height.
-   **👻 Holograms**:
    -   Packet-based hologram system (no dependencies required!).
    -   Create text displays anywhere in your lobby.
-   **💬 Chat Management**:
    -   **Clear Chat**: Wipe the chat history for all players.
    -   **Lock Chat**: Prevent players from chatting during maintenance.
    -   **Anti-Swear**: Block specific words.
    -   **Command Blocker**: Block specific commands (e.g. `/plugins`, `/version`).
-   **📢 Announcements**:
    -   Auto-broadcaster with configurable interval.
    -   Manual broadcast commands (`/announce`, `/broadcast`, `/holobroadcast`).
-   **👋 Join Events**:
    -   **Custom Join/Quit Messages**: Customizable text with placeholders.
    -   **Join Title & Subtitle**: Welcome players in style.
    -   **Credits**: "Plugin developed by..." message in chat (configurable).
    -   **Join Sound & Fireworks**: Celebratory effects on join.
    -   **Spawn Teleport**: Force players to spawn on join/respawn.
-   **🛡️ World Protection**:
    -   Disable block breaking/placing.
    -   Disable PVP, falldamage, and hunger.
    -   **Anti-World Downloader**: Prevents players from downloading your map using client-side mods.
    -   **Void Teleport**: Teleport players back to spawn if they fall into the void.
-   **🏃 Movement & Utilities**:
    -   **Double Jump**: Allow players to double-jump in the lobby.
    -   **Player Hider**: Item to toggle visibility of other players.
    -   **Scoreboard & Tablist**: Fully customizable sidebar and tab header/footer.
    -   **Gamemode & Fly**: Simple commands for staff management.

## 📥 Installation

1.  Download `DeluxeHub-1.0.0-SNAPSHOT.jar`.
2.  Place it in your server's `plugins/` folder.
3.  Restart your server.
4.  Edit `plugins/DeluxeHub/config.yml` to your liking.
5.  Run `/deluxehub reload` to apply changes (some features like Join Items may require a re-join).

## 🛠️ Commands & Permissions

### Main Command
-   `/deluxehub reload` - Reload configuration.
    -   **Permission**: `deluxehub.admin` / `deluxehub.reload`

### Hub & Navigation
-   `/hub`, `/lobby` - Teleport to the hub spawn point.
    -   **Permission**: `deluxehub.hub`
-   `/setlobby`, `/setspawn` - Set the hub spawn point at your location.
    -   **Permission**: `deluxehub.setlobby`
-   `/server` - Open the Server Selector GUI.
    -   **Permission**: `deluxehub.server`

### Features
-   `/fly` - Toggle flight mode.
    -   **Permission**: `deluxehub.fly`
-   `/gamemode <mode>`, `/gm <mode>` - Change your gamemode.
    -   **Permission**: `deluxehub.gamemode`
-   `/vanish` - Toggle invisibility.
    -   **Permission**: `deluxehub.vanish`
-   `/hider` - Toggle player visibility.
    -   **Permission**: `deluxehub.hider`

### Chat & Broadcasts
-   `/clearchat` - Clear global chat.
    -   **Permission**: `deluxehub.clearchat`
-   `/lockchat` - Lock/Unlock global chat.
    -   **Permission**: `deluxehub.lockchat`
-   `/announce <msg>`, `/broadcast <msg>` - Send a global announcement.
    -   **Permission**: `deluxehub.announce`
-   `/holobroadcast <msg>` - Send a hologram announcement to all players.
    -   **Permission**: `deluxehub.holobroadcast`

### World Management
-   `/hologram create <name> <text>` - Create a hologram.
-   `/hologram delete <name>` - Delete a hologram.
-   `/hologram list` - List all holograms.
    -   **Permission**: `deluxehub.hologram`
-   `/launchpad create` - Turn the block you are looking at into a launchpad.
-   `/launchpad remove` - Remove the launchpad you are looking at.
-   `/launchpad list` - List custom launchpads.
    -   **Permission**: `deluxehub.launchpad`

## ⚙️ Configuration

The plugin is highly configurable. The main file is `config.yml`.

### Example: Launchpads
```yaml
launchpads:
  enabled: true
  velocity-multiplier: 2.5
  height-multiplier: 1.2
  plate-types:
    - GOLD_PLATE
    - IRON_PLATE
    - STONE_PLATE
    - WOOD_PLATE
  sound: BAT_TAKEOFF
```

### Example: Join Messages
```yaml
join:
  messages:
    join: "&a%player_name% &7has joined the lobby!"
    quit: "&c%player_name% &7has left the lobby!"
  title:
    enabled: true
    title: "&6&lWELCOME"
    subtitle: "&fTo the DeluxeHub Network"
```

## 🏗️ Building from Source

### Requirements
-   Java 8 JDK
-   Git

### Steps
1.  Clone the repository:
    ```bash
    git clone https://github.com/spacebxr11-collab/DeluxeHub.git
    cd DeluxeHub
    ```
2.  Build using the provided script (Linux/Mac):
    ```bash
    ./build.sh
    ```
    *Or using Gradle:*
    ```bash
    ./gradlew build
    ```
3.  The compiled JAR will be in `target/` (if using script) or `build/libs/` (if using Gradle).
