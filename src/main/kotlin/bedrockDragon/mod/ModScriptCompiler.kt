package bedrockDragon.mod

import mu.KotlinLogging
import java.io.File
import java.nio.file.Files
import java.security.MessageDigest
import kotlin.io.path.Path
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
                    if(!Files.exists(Path(".cache/$hash.jar")))
                    {
                        logger.info { "Generating jar for: ${script.locationId}" }
                    }
                    File(".cache", "$hash.jar")
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
