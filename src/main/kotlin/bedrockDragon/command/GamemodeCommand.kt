package bedrockDragon.command

import bedrockDragon.player.Player

class GamemodeCommand {
    fun invoke(sender: Player, string: Array<String>) {
        sender.gamemode = Player.Gamemode.CREATIVE
    }
}