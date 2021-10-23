package bedrockDragon.math

import bedrockDragon.math.IVector
import bedrockDragon.math.Vector3
import kotlin.math.sqrt

class Vector3Int(var x: Int, var y: Int, var z: Int): IVector {

    operator fun minus(vector3: Vector3Int): Vector3Int { return Vector3Int(x - vector3.x, y - vector3.y, z - vector3.z) }
    operator fun plus(vector3: Vector3Int): Vector3Int { return Vector3Int(x + vector3.x, y + vector3.y, z + vector3.z) }

    override fun distanceFrom(iVector: IVector): Double {
        val diffVec = minus(iVector as Vector3Int)
        return sqrt(
            (diffVec.x * diffVec.x +
                    diffVec.y * diffVec.y +
                    diffVec.z * diffVec.z).toDouble()
    )}

    override fun toString(): String {
        return "Vector3<$x,$y,$z>"
    }
}