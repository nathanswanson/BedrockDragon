package bedrockDragon.network.raknet.protocol.game.crafting

import bedrockDragon.crafting.Recipe
import bedrockDragon.network.raknet.Packet

class RecipePayload(val recipes: Array<Recipe>): Packet() {
    //recipe length uVInt
    //RECIPE:
    //vInt type

    fun encode() {
        writeUnsignedVarInt(recipes.size)
        for(recipe in recipes) {
            writeVarInt(if(recipe.shaped) 1 else 0) //todo for others
            if(recipe.shaped) {

            }
        }
    }


}