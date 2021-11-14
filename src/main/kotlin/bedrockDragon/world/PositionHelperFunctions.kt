package bedrockDragon.world

import com.curiouscreature.kotlin.math.Float2

fun Float2.floor() {
    this.x=this.x.toInt().toFloat()
    this.y=this.y.toInt().toFloat()

}
fun Float2.toChunkSpace() {
    this.x = (this.x.toInt() shr 4).toFloat()
    this.y = (this.y.toInt() shr 4).toFloat()

}

fun Float2.toChunkRelaySpace() {
    this.x = (this.x.toInt() shr 6).toFloat()
    this.y = (this.y.toInt() shr 6).toFloat()
}