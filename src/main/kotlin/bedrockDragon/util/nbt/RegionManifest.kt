package bedrockDragon.util.nbt

import java.io.InputStream
import java.nio.file.Path
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.collections.LinkedHashSet
import kotlin.io.path.inputStream

class RegionManifest(val region: Path) {
    private val manifest = LinkedList<UnallocatedSection>()
    private var lastSlotPointer = 0

    //full read
    fun readManifest() { //O(n)

        region.inputStream().use {
            var size = 0
            var start = 0
            for(i in 1..1024) {

                //then it exists
                if(readTryte(it) == 0 && it.read() == 0) {
                    if(size == 0) {
                        start = i
                    }
                    size++
                } else if (size != 0) {
                    manifest.add(UnallocatedSection(start, size))
                    size = 0
                }

                if(i == 1023) {
                    manifest.add(UnallocatedSection(start, size))
                }
            }
        }

    }

    fun getFirstUsableSlot(size: Int): Int {

        var lastIndex = 0

        val section = manifest.firstOrNull() {
            lastIndex = it.end
            it.take(size)
        }
            ?: return lastIndex //null if none found

        return section.start-size



    }

    fun unAllocate(start: Int, end: Int) {

    }

    private class UnallocatedSection(var start: Int, var end: Int) {

        fun take(size: Int): Boolean {
            if(start + size > this.end)
                return false

            start+=size
            return true
        }

    }

    fun readTryte(inputStream: InputStream): Int {

        return (inputStream.read() shl 16) +
                (inputStream.read() shl 8) +
                inputStream.read()

    }
}