package bedrockDragon.mod

import kotlin.script.experimental.api.ScriptEvaluationConfiguration
import kotlin.script.experimental.api.implicitReceivers
import kotlin.script.experimental.api.scriptsInstancesSharing

object ModScriptEval: ScriptEvaluationConfiguration({
    scriptsInstancesSharing(true)
    implicitReceivers("")
})