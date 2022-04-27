package bedrockDragon.network.raknet.protocol.game.world

import bedrockDragon.network.raknet.Packet
import bedrockDragon.network.raknet.protocol.game.MinecraftPacketConstants
import bedrockDragon.network.raknet.protocol.game.PacketPayload

class AdventureSettingsPacket: PacketPayload(MinecraftPacketConstants.ADVENTURE_SETTINGS) {

    //from nukkit
    val PERMISSION_NORMAL = 0
    val PERMISSION_OPERATOR = 1
    val PERMISSION_HOST = 2
    val PERMISSION_AUTOMATION = 3
    val PERMISSION_ADMIN = 4
    //TODO: check level 3
    /**
     * This constant is used to identify flags that should be set on the second field. In a sensible world, these
     * flags would all be set on the same packet field, but as of MCPE 1.2, the new abilities flags have for some
     * reason been assigned a separate field.
     */


    companion object {
        const val BITFLAG_SECOND_SET = 1 shl 16

        val WORLD_IMMUTABLE = 0x01
        val NO_PVP = 0x02
        val AUTO_JUMP = 0x20
        val ALLOW_FLIGHT = 0x40
        val NO_CLIP = 0x80
        val WORLD_BUILDER = 0x100
        val FLYING = 0x200
        val MUTED = 0x400

        val MINE = 0x01 or BITFLAG_SECOND_SET
        val DOORS_AND_SWITCHES = 0x02 or BITFLAG_SECOND_SET
        val OPEN_CONTAINERS = 0x04 or BITFLAG_SECOND_SET
        val ATTACK_PLAYERS = 0x08 or BITFLAG_SECOND_SET
        val ATTACK_MOBS = 0x10 or BITFLAG_SECOND_SET
        val OPERATOR = 0x20 or BITFLAG_SECOND_SET
        val TELEPORT = 0x80 or BITFLAG_SECOND_SET
        val BUILD = 0x100 or BITFLAG_SECOND_SET
        val DEFAULT = 0x200 or BITFLAG_SECOND_SET
    }


    var flags = 0 //varInt

    var commandPermission = PERMISSION_NORMAL //varInt
    var actionPermissions = 0 //varInt
    var permissionLevel = 1 //varInt
    var customStoredPermissions = 0 //varInt
    var userId = 1L //LELong

    override suspend fun encode() {
        writeUnsignedVarInt(flags)
        writeUnsignedVarInt(commandPermission)
        writeUnsignedVarInt(actionPermissions)
        writeUnsignedVarInt(permissionLevel)
        writeUnsignedVarInt(customStoredPermissions)
        writeLongLE(userId)
    }

    override fun decode(packet: Packet) {
        flags = packet.readUnsignedVarInt().toInt()
        commandPermission = packet.readUnsignedVarInt().toInt()
        actionPermissions = packet.readUnsignedVarInt().toInt()
        permissionLevel = packet.readUnsignedVarInt().toInt()
        customStoredPermissions = packet.readUnsignedVarInt().toInt()
        userId = packet.readLongLE()
    }
    fun flag(flag: Int): Boolean {
        if((flag and BITFLAG_SECOND_SET) != 0) {
            return (actionPermissions and flag) != 0
        }
        return (this.flags and flag) != 0
    }

    fun flag(flag: Int, value: Boolean) {
        var aFlags = (flag and BITFLAG_SECOND_SET) != 0
        if(value) {
            if(aFlags) {
                actionPermissions = actionPermissions or flag
            } else {
                flags = flags or flag
            }
        } else {
            if(aFlags) {
                actionPermissions = actionPermissions and flag.inv()
            } else {
                flags = flags and flag.inv()
            }
        }
    }

    override fun toString(): String {
        return """Adventure Packet:
            |   flags: $flags
            |   commandPermission: $commandPermission
            |   actionPermissions: $actionPermissions
            |   permissionLevel: $permissionLevel
            |   customStoredPermissions: $customStoredPermissions
            |   userId: $userId
        """.trimMargin()
    }
}