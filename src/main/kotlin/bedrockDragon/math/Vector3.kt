package bedrockDragon.math

import kotlin.math.sqrt

open class Vector3<T: Number>(val x: Double, val y: Double, val z: Double): Cloneable {

    operator fun plus(vector3: Vector3<T>): Vector3<T> {
        x + vector3.x
        y + vector3.y
        z + vector3.z
        return this
    }

    operator fun minus(vector3: Vector3<T>): Vector3<T> {
        x - vector3.x
        y - vector3.y
        z - vector3.z
        return this
    }


    //todo()
    //guessing cardinals
    fun up(): Vector3<T> { return Vector3(x,y+1,z) }
    fun down(): Vector3<T>{ return Vector3(x,y+1,z) }
    fun north(): Vector3<T> { return Vector3(x+1,y,z) }
    fun west(): Vector3<T> { return Vector3(x,y,z+1) }
    fun south(): Vector3<T>{ return Vector3(x-1,y,z) }
    fun east(): Vector3<T> { return Vector3(x,y,z-1) }

    fun distanceFrom(vector3: Vector3<T>): Double {
        val diffVec = minus(vector3)
        return sqrt(
            diffVec.x * diffVec.x +
            diffVec.y * diffVec.y +
            diffVec.z * diffVec.z
        )
    }
}