package bedrockDragon.registry

import bedrockDragon.command.CommandIntTag
import bedrockDragon.command.CommandStringTag
import bedrockDragon.command.registerCommand
import bedrockDragon.player.Player
import dev.romainguy.kotlin.math.Float3

object NativeCommands {
    init {
        registerCommand("bedrockDragon") {

            command("/gamemode") {
                val optionalName = CommandStringTag()
                optionalName.optional = true
                optionalName.default = "{u}"

                args.add(CommandIntTag())
                args.add(optionalName)


                invoke = {
                    player, anies ->
                    player.gamemode = Player.Gamemode.values()[(anies[0] as String).toInt()]
                }
            }
            command("/tp") {
                args.add(CommandIntTag()) //x
                args.add(CommandIntTag()) //y
                args.add(CommandIntTag()) //z

                val optionalName = CommandStringTag()
                optionalName.optional = true
                optionalName.default = "{u}"

                invoke = {
                    player, anies ->
                        player.teleport(Float3(
                            (anies[0] as String).toFloat(),
                            (anies[1] as String).toFloat(),
                            (anies[2] as String).toFloat()
                        ))
                }
            }
        }
    }
}