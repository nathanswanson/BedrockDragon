package bedrockDragon.network.raknet

import java.util.concurrent.ConcurrentHashMap

const val DATA_TYPE_BYTE = 0
const val DATA_TYPE_SHORT = 1
const val DATA_TYPE_INT = 2
const val DATA_TYPE_FLOAT = 3
const val DATA_TYPE_STRING = 4
const val DATA_TYPE_NBT = 5
const val DATA_TYPE_POS = 6
const val DATA_TYPE_LONG = 7
const val DATA_TYPE_VECTOR3F = 8

class MetaTag {
    val data = ConcurrentHashMap<Int, TypedDefineTag>()

    fun put(id: Int, tag: TypedDefineTag) {
        data[id] = tag
    }

    fun get(id: Int): TypedDefineTag? {
        return data[id]
    }

    fun size(): Int {
        return data.size
    }

    sealed class TypedDefineTag {
        abstract val type: Int
        abstract val data: Any

        class TAGByte(override val data: Byte) : TypedDefineTag() { override val type: Int = 0 }
        class TAGSHORT(override val data: Short): TypedDefineTag() { override val type: Int = 1 }
        class TAGINT(override val data: Int): TypedDefineTag() {override val type: Int = 2}
        class TAGLONG(override val data: Long): TypedDefineTag() {override val type: Int = 7}

    }
}