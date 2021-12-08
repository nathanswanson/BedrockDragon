package bedrockDragon.util

import it.unimi.dsi.fastutil.io.FastByteArrayOutputStream

fun FastByteArrayOutputStream.writeLInt(i: Int) {
    write(i and 0xFF)
    write((i ushr 8) and 0xFF)
    write((i ushr 16) and 0xFF)
    write((i ushr 24) and 0xFF)
}