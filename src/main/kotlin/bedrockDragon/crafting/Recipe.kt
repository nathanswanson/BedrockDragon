package bedrockDragon.crafting

import bedrockDragon.item.Item

class RecipeImpl : Recipe()

@CraftingDSL
sealed class Recipe {
    var ingredients = arrayOfNulls<Item>(9)
    lateinit var product: Item
    var sideProduct: Item? = null
    var shaped = false

    fun validate(item: Item) : Boolean {
        return validate(arrayOf(item))
    }

    fun validate(item: Array<Item?>): Boolean {
        return if(shaped)
            item.contentEquals(ingredients)
        else
            item.subtract(ingredients.toSet()).filterNotNull().isEmpty()
    }
}

@CraftingDSL
fun recipe(shaped: Boolean, lambda: Recipe.() -> Unit = {}): Recipe {
    val recipe = RecipeImpl().apply(lambda)
    recipe.shaped = shaped
    return recipe
}