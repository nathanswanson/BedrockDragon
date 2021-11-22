package bedrockDragon.player

import bedrockDragon.network.raknet.protocol.game.world.AdventureSettingsPacket

class AdventureSettings {

    enum class Type(val id: Int,val  defaultValue: Boolean) {
        WORLD_IMMUTABLE(AdventureSettingsPacket.WORLD_IMMUTABLE, false),
        AUTO_JUMP(AdventureSettingsPacket.AUTO_JUMP, true),
        ALLOW_FLIGHT(AdventureSettingsPacket.ALLOW_FLIGHT, false),
        NO_CLIP(AdventureSettingsPacket.NO_CLIP, false),
        WORLD_BUILDER(AdventureSettingsPacket.WORLD_BUILDER, true),
        FLYING(AdventureSettingsPacket.FLYING, false),
        MUTED(AdventureSettingsPacket.MUTED, false),
        BUILD_AND_MINE(AdventureSettingsPacket.BUILD_AND_MINE, true),
        DOORS_AND_SWITCHED(AdventureSettingsPacket.DOORS_AND_SWITCHES, true),
        OPEN_CONTAINERS(AdventureSettingsPacket.OPEN_CONTAINERS, true),
        ATTACK_PLAYERS(AdventureSettingsPacket.ATTACK_PLAYERS, true),
        ATTACK_MOBS(AdventureSettingsPacket.ATTACK_MOBS, true),
        OPERATOR(AdventureSettingsPacket.OPERATOR, false),
        TELEPORT(AdventureSettingsPacket.TELEPORT, false);
    }
}