package bedrockDragon.network.raknet.protocol.game.type

class AttributeBR(modifiedAttributes: Array<Attribute>) {

    val attributes = arrayOf(
        Attribute(0, "minecraft:absorption", 0.0f, Float.MAX_VALUE, 0.0f), //absorption
        Attribute(1, "minecraft:player.saturation", 0.00f, 20.00f, 5.00f), //saturation
        Attribute(2, "minecraft:player.exhaustion", 0.0f, 5.00f, 0.41f),
        Attribute(3, "minecraft:knockback_resistance", 0.0f, 1.00f, 0.41f),
        Attribute(4, "minecraft:health", 0.00f, 20.00f, 20.00f),
        Attribute(5, "minecraft:movement", 0.00f, Float.MAX_VALUE, 0.1f),
        //Attribute(6, "minecraft:follow_range", 0.00f, 2048f, 16f),
        Attribute(7, "minecraft:player.hunger", 0.00f, 20.00f, 10.00f),
        Attribute(8, "minecraft:attack_damage", 0.00f, Float.MAX_VALUE, 1f),
        Attribute(9, "minecraft:player.level", 0.00f, 24791f, 0.00f),
        Attribute(10, "minecraft:player.experience", 0.00f, 1f, 0f),
        Attribute(11, "minecraft:luck", -1024f, 1024f, 0f)
    )


    data class Attribute(val id: Int,val name: String,val minValue: Float,val maxValue: Float,val defaultValue: Float)
    {
        var value: Float = Float.MIN_VALUE

        fun getValueOrDefault(): Float {
            return if (value != Float.MIN_VALUE) value else defaultValue
        }
    }
}