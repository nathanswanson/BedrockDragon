/*
 *      ##### ##                  ##                                    /                 ##### ##
 *   ######  /##                   ##                                 #/               /#####  /##
 *  /#   /  / ##                   ##                                 ##             //    /  / ###
 * /    /  /  ##                   ##                                 ##            /     /  /   ###
 *     /  /   /                    ##                                 ##                 /  /     ###
 *    ## ##  /        /##      ### ##  ###  /###     /###     /###    ##  /##           ## ##      ## ###  /###     /###     /###      /###   ###  /###
 *    ## ## /        / ###    ######### ###/ #### / / ###  / / ###  / ## / ###          ## ##      ##  ###/ #### / / ###  / /  ###  / / ###  / ###/ #### /
 *    ## ##/        /   ###  ##   ####   ##   ###/ /   ###/ /   ###/  ##/   /           ## ##      ##   ##   ###/ /   ###/ /    ###/ /   ###/   ##   ###/
 *    ## ## ###    ##    ### ##    ##    ##       ##    ## ##         ##   /            ## ##      ##   ##       ##    ## ##     ## ##    ##    ##    ##
 *    ## ##   ###  ########  ##    ##    ##       ##    ## ##         ##  /             ## ##      ##   ##       ##    ## ##     ## ##    ##    ##    ##
 *    #  ##     ## #######   ##    ##    ##       ##    ## ##         ## ##             #  ##      ##   ##       ##    ## ##     ## ##    ##    ##    ##
 *       /      ## ##        ##    ##    ##       ##    ## ##         ######               /       /    ##       ##    ## ##     ## ##    ##    ##    ##
 *   /##/     ###  ####    / ##    /#    ##       ##    ## ###     /  ##  ###         /###/       /     ##       ##    /# ##     ## ##    ##    ##    ##
 *  /  ########     ######/   ####/      ###       ######   ######/   ##   ### /     /   ########/      ###       ####/ ## ########  ######     ###   ###
 * /     ####        #####     ###        ###       ####     #####     ##   ##/     /       ####         ###       ###   ##  ### ###  ####       ###   ###
 * #                                                                                #                                             ###
 *  ##                                                                               ##                                     ####   ###
 *                                                                                                                        /######  /#
 *                                                                                                                       /     ###/
 * the MIT License (MIT)
 *
 * Copyright (c) 2021-2022 Nathan Swanson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * the above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package bedrockDragon.registry.resource

import bedrockDragon.item.Item
import bedrockDragon.item.registerItem

/**
 * [VanillaItems] is dragon code for vanilla items minecraft provides.
 * @author Nathan Swanson
 * @since BETA
 */
object VanillaItems {
    enum class ItemTag {
        AXE,
        SHOVEL,
        PICKAXE,
        SWORD,
        HOE,
        FOOD
    }

    init {
        registerItem("minecraft") {
            item("stone")
            item("deepslate")
            item("cobbled_deepslate")
            item("polished_deepslate")
            item("calcite")
            item("tuff")
            item("dripstone_block")
            item("grass") {
                alias = "grass_block"
            }
            item("dirt")
            item("podzol")
            item("dirt_with_roots")
            item("crimson_nylium")
            item("warped_nylium")
            item("cobblestone")
            item("planks")
            item("crimson_planks")
            item("warped_planks")
            item("sapling")
            item("bedrock")
            item("sand")
            item("gravel")
            item("coal_ore")
            item("deepslate_coal_ore")
            item("iron_ore")
            item("deepslate_iron_ore")
            item("copper_ore")
            item("deepslate_copper_ore")
            item("gold_ore")
            item("deepslate_gold_ore")
            item("redstone_ore")
            item("deepslate_redstone_ore")
            item("emerald_ore")
            item("deepslate_emerald_ore")
            item("lapis_ore")
            item("deepslate_lapis_ore")
            item("diamond_ore")
            item("deepslate_diamond_ore")
            item("nether_gold_ore")
            item("quartz_ore")
            item("ancient_debris")
            item("coal_block")
            item("raw_iron_block")
            item("raw_copper_block")
            item("raw_gold_block")
            item("amethyst_block")
            item("budding_amethyst")
            item("iron_block")
            item("copper_block")
            item("gold_block")
            item("diamond_block")
            item("netherite_block")
            item("exposed_copper")
            item("weathered_copper")
            item("oxidized_copper")
            item("cut_copper")
            item("exposed_cut_copper")
            item("weathered_cut_copper")
            item("oxidized_cut_copper")
            item("weathered_cut_copper")
            item("oxidized_cut_copper")
            item("cut_copper_stairs")
            item("exposed_cut_copper_stairs")
            item("weathered_cut_copper_stairs")
            item("oxidized_cut_copper_stairs")
            item("cut_copper_slab")
            item("exposed_cut_copper_slab")
            item("weathered_cut_copper_slab")
            item("oxidized_cut_copper_slab")
            item("waxed_copper")
            item("waxed_exposed_copper")
            item("waxed_weathered_copper")
            item("waxed_oxidized_copper")
            item("waxed_cut_copper")
            item("waxed_exposed_cut_copper")
            item("waxed_weathered_cut_copper")
            item("waxed_oxidized_cut_copper")
            item("waxed_cut_copper_stairs")
            item("waxed_exposed_cut_copper_stairs")
            item("waxed_weathered_cut_copper_stairs")
            item("waxed_oxidized_cut_copper_stairs")
            item("waxed_cut_copper_slab")
            item("waxed_exposed_cut_copper_slab")
            item("waxed_weathered_cut_copper_slab")
            item("waxed_oxidized_cut_copper_slab")
            item("log")
            item("log2")
            item("crimson_stem")
            item("warped_stem")
            item("stripped_oak_log")
            item("stripped_spruce_log")
            item("stick")
            item("glowstone")
            item("torch")
            item("anvil")

            item("*") { //food
                tag = ItemTag.FOOD
                of {
                    name = "cooked_porkchop"
                    dynamicFields["foodPt"] = 8.0
                    dynamicFields["saturationPt"] = 12.8
                }
            }

            item("*_pickaxe") {
                maxStackSize = 1
                tag = ItemTag.PICKAXE

                of {
                    name = "wooden"
                    durability = 59
                    damage = 2
                }
                of {
                    name = "stone"
                    durability = 131
                    damage = 3
                }
                of {
                    name = "iron"
                    durability = 250
                    damage = 4
                }
                of {
                    name = "golden"
                    durability = 32
                    damage = 2
                }
                of {
                    name = "diamond"
                    durability = 1561
                    damage = 5
                }
                of {
                    name = "netherite"
                    durability = 2031
                    damage = 6
                }
            }
            item("*_sword") {
                maxStackSize = 1
                tag = ItemTag.SWORD

                of {
                    name = "wooden"
                    durability = 60
                    damage = 5
                }
                of {
                    name = "stone"
                    durability = 132
                    damage = 5
                }
                of {
                    name = "iron"
                    durability = 251
                    damage = 6
                }
                of {
                    name = "golden"
                    durability = 33
                    damage = 7
                }
                of {
                    name = "diamond"
                    durability = 1562
                    damage = 8
                }
                of {
                    name = "netherite"
                    durability = 2032
                    damage = 9
                }
            }
        }
    }
}