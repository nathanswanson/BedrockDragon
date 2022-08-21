package bedrockDragon.mod

import bedrockDragon.registry.DSLBase
import com.charleskorn.kaml.Yaml
import kotlinx.serialization.Serializable
import mu.KotlinLogging
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.script.experimental.api.EvaluationResult
import kotlin.script.experimental.api.ResultWithDiagnostics
import kotlin.script.experimental.host.toScriptSource
import kotlin.script.experimental.jvm.util.isError
import kotlin.script.experimental.jvmhost.BasicJvmScriptingHost
import kotlin.script.experimental.jvmhost.createJvmCompilationConfigurationFromTemplate

class Mod(val path: Path): DSLBase() {

    private val logger = KotlinLogging.logger {}

    private val pluginPath: String = "plugin.yaml"
    val config: Config = Yaml.default.decodeFromString(Config.serializer(), Files.readString(Paths.get("$path/$pluginPath")))

    fun compile()
    {
        Files.walk(path, 1).filter {
            it.toString().endsWith("dragon.kts")
        }.forEach {
            val response = evalFile(it.toFile())
            if(response.isError())
            {
                logger.error { "Failed to compile ${it.fileName}" }
            }
        }
    }

    private fun evalFile(scriptFile: File): ResultWithDiagnostics<EvaluationResult> {
        val compilationConfiguration = createJvmCompilationConfigurationFromTemplate<DragonScript>()
        return BasicJvmScriptingHost().eval(scriptFile.toScriptSource(), compilationConfiguration, null)
    }
    @Serializable
    data class Config (val name: String, val id: String, val version: String, val dependencies: Array<String>)

}