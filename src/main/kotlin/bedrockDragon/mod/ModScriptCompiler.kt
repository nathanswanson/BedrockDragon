package bedrockDragon.mod

import mu.KotlinLogging
import java.io.File
import java.nio.file.Files
import java.security.MessageDigest
import kotlin.io.path.Path
import kotlin.io.path.deleteIfExists
import kotlin.io.path.name
import kotlin.script.experimental.api.*
import kotlin.script.experimental.dependencies.*
import kotlin.script.experimental.host.ScriptingHostConfiguration
import kotlin.script.experimental.jvm.compilationCache
import kotlin.script.experimental.jvm.dependenciesFromCurrentContext
import kotlin.script.experimental.jvm.jvm
import kotlin.script.experimental.jvmhost.CompiledScriptJarsCache

object ModScriptCompiler: ScriptCompilationConfiguration({

    val logger = KotlinLogging.logger {}


    defaultImports(DependsOn::class, Repository::class)
    jvm {
        dependenciesFromCurrentContext(wholeClasspath = true)
    }

    hostConfiguration(ScriptingHostConfiguration {
        jvm {
            compilationCache(
                CompiledScriptJarsCache { script, scriptCompilationConfiguration ->
                    val hash = compiledScriptUniqueName(script, scriptCompilationConfiguration)
                    if(!Files.exists(Path(".cache/${script.name}-$hash.jar")))
                    {
                        logger.info { "generating jar for: ${script.locationId}" }
                        Files.list(Path(".cache")).filter {
                            it.name.startsWith(script.name?:"")
                        }.forEach {
                            logger.debug { "deleting cached jar $it" }
                            it.deleteIfExists()
                        }
                    }
                    File(".cache", "${script.name}-$hash.jar")
                }
            )
        }
    })
})

private fun compiledScriptUniqueName(script: SourceCode, scriptCompilationConfiguration: ScriptCompilationConfiguration): String {
    val digestWrapper = MessageDigest.getInstance("MD5")
    digestWrapper.update(script.text.toByteArray())
    scriptCompilationConfiguration.notTransientData.entries
        .sortedBy { it.key.name }
        .forEach {
            digestWrapper.update(it.key.name.toByteArray())
            digestWrapper.update(it.value.toString().toByteArray())
        }

    return digestWrapper.digest().toHex()
}

private fun ByteArray.toHex(): String = joinToString(separator = "") { eachByte -> "%02x".format(eachByte) }
