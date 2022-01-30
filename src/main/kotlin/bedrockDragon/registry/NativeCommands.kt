package bedrockDragon.registry

import bedrockDragon.command.CommandIntTag
import bedrockDragon.command.CommandStringTag
import bedrockDragon.command.registerCommand
import bedrockDragon.player.Player
import bedrockDragon.world.PaletteGlobal
import dev.romainguy.kotlin.math.Float3

object NativeCommands {
    init {
        registerCommand("bedrockDragon") {

            command("/gamemode") {

                args.add(CommandIntTag()) //gamemode value
                args.add(CommandStringTag().asOptional()) //target (@s if none)


                invoke = {
                    player, anies ->
                    player.gamemode = Player.Gamemode.values()[(anies[0] as String).toInt()]
                }
            }
            command("/tp") {
                args.add(CommandIntTag()) //x
                args.add(CommandIntTag()) //y
                args.add(CommandIntTag()) //z

                args.add(CommandStringTag().asOptional()) //target (@s if none)

                invoke = {
                    player, anies ->
                        player.teleport(Float3(
                            (anies[0] as String).toFloat(),
                            (anies[1] as String).toFloat(),
                            (anies[2] as String).toFloat()
                        ))
                }
            }
            command("/give") {
                args.add(CommandStringTag()) //target
                args.add(CommandStringTag()) //item name
                args.add(CommandIntTag().asDefault(1).asOptional()) // amount
                args.add(CommandIntTag().asOptional()) //data Int
                args.add(CommandStringTag().asOptional()) //components json

                invoke = {
                    player, anies ->
                    PaletteGlobal.itemRegistry[anies[1] as String]?.let {
                        it.count = (anies[2] as String).toInt()
                        player.addItemToPlayerInventory(it)
                    }
                }
            }
            command("/damage") {
                args.add(CommandStringTag())
                args.add(CommandIntTag())
                //damagecause

                invoke = {
                    player, anies ->
                    player.damage((anies[1] as String).toFloat())
                    player.sendAttributes()
                }
            }
            command("/kill") {
                args.add(CommandStringTag().asOptional())

                invoke = {
                    player, anies ->
                        player.kill()
                }
            }
        }
    }
}