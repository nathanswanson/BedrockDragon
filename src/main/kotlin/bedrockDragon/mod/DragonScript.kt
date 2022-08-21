package bedrockDragon.mod

import kotlin.script.experimental.annotations.KotlinScript

@KotlinScript(
    fileExtension = "dragon.kts",
    compilationConfiguration = ModScriptCompiler::class,
    evaluationConfiguration = ModScriptEval::class

)

abstract class DragonScript