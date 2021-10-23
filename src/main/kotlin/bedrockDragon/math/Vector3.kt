package bedrockDragon.math

import kotlin.math.sqrt

class Vector3(var x: Double, var y: Double, var z: Double): IVector, Cloneable {
    constructor(x: Int, y: Int, z: Int): this(x.toDouble(), y.toDouble(), z.toDouble())

    operator fun minus(vector3: Vector3): Vector3 { return Vector3(x - vector3.x, y - vector3.y, z - vector3.z) }
    operator fun plus(vector3: Vector3): Vector3 { return Vector3(x + vector3.x, y + vector3.y, z + vector3.z) }
    //todo()
    //guessing cardinals
    //fun up(): Vector3<Number> { return Vector3(x,y+1,z) }
   // fun down(): Vector3<Number>{ return Vector3(x,y+1,z) }
    //fun north(): Vector3<Number> { return Vector3(x+1,y,z) }
   // fun west(): Vector3<Number> { return Vector3(x,y,z+1) }
   // fun south(): Vector3<Number>{ return Vector3(x-1,y,z) }
   // fun east(): Vector3<Number> { return Vector3(x,y,z-1) }

    override fun distanceFrom(iVector: IVector): Double {
        val diffVec = minus(iVector as Vector3)
        return sqrt(
            (diffVec.x * diffVec.x +
            diffVec.y * diffVec.y +
            diffVec.z * diffVec.z)
        )
    }

    override fun toString(): String {
        return "Vector3<$x,$y,$z>"
    }
}



