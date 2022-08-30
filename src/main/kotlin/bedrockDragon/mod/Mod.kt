package bedrockDragon.mod

import bedrockDragon.registry.DSLBase
import bedrockDragon.util.text.GREEN
import bedrockDragon.util.text.RED
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
    private val texturePath: String = "resource/"
    private val scriptPath: String = "script/"
    private val langPath: String = "lang/"
    val config: Config = Yaml.default.decodeFromString(Config.serializer(), Files.readString(Paths.get("$path/$pluginPath")))

    enum class ModStatus {
        OK,
        WARNING,
        ERROR,
    }

    fun compile()
    {
        Files.walk(path.resolve(scriptPath), 1).filter {
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
        val result = BasicJvmScriptingHost().eval(scriptFile.toScriptSource(), compilationConfiguration, null)
        logger.info { "loading script: ${scriptFile.name}... ${if (result.isError()) "failed".RED() else "success".GREEN()}" }
        return result
    }
    @Serializable
    data class Config (val name: String, val id: String, val version: String, val dependencies: Array<String>)

}