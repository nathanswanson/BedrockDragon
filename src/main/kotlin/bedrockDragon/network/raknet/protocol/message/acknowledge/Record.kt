/*
 *    __     ______     ______     __  __     __   __     ______     ______  
 *   /\ \   /\  == \   /\  __ \   /\ \/ /    /\ "-.\ \   /\  ___\   /\__  _\
 *  _\_\ \  \ \  __<   \ \  __ \  \ \  _"-.  \ \ \-.  \  \ \  __\   \/_/\ \/  
 * /\_____\  \ \_\ \_\  \ \_\ \_\  \ \_\ \_\  \ \_\\"\_\  \ \_____\    \ \_\ 
 * \/_____/   \/_/ /_/   \/_/\/_/   \/_/\/_/   \/_/ \/_/   \/_____/     \/_/                                                                          
 *
 * the MIT License (MIT)
 *
 * Copyright (c) 2016-2020 "Whirvis" Trent Summerlin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * the above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package bedrockDragon.network.raknet.protocol.message.acknowledge

import kotlin.Throws
import bedrockDragon.network.raknet.map.IntMap
import java.lang.IllegalArgumentException
import java.util.*
import kotlin.collections.ArrayList

/**
 * Represents a packet record which is used in acknowledgement packets to
 * indicate a packet was either acknowledged (received) or not acknowledged
 * (lost in transmission).
 *
 * @author "Whirvis" Trent Summerlin
 * @since JRakNet v1.0.0
 */
class Record(index: Int, endIndex: Int) {
    private var index: Int
    private var endIndex: Int

    /**
     * Returns the sequence IDs contained within this record.
     *
     * @return the sequence IDs contained within this record.
     * @see .getSequenceId
     */
    lateinit var sequenceIds: IntArray
        private set

    /**
     * Creates a single record.
     *
     * @param id
     * the sequence ID.
     *
     * @throws IllegalArgumentException
     * if the `id` is less than `0`.
     */
    constructor(id: Int) : this(id, NOT_RANGED) {}

    /**
     * Updates the sequence IDs within the record.
     */
    private fun updateSequenceIds() {
        if (!isRanged) {
            sequenceIds = intArrayOf(getIndex())
        } else {
            val ranged = IntArray(getEndIndex() - getIndex() + 1)
            for (i in ranged.indices) {
                ranged[i] = i + getIndex()
            }
            sequenceIds = ranged
        }
    }

    /**
     * Returns the starting index of the record.
     *
     * @return the starting index of the record.
     */
    fun getIndex(): Int {
        return index
    }

    /**
     * Sets the starting index of the record.
     *
     * @param index
     * the starting index.
     * @throws IllegalArgumentException
     * if the `index` is negative.
     */
    @Throws(IllegalArgumentException::class)
    fun setIndex(index: Int) {
        require(index >= 0) { "Index cannot be negative" }
        this.index = index
        updateSequenceIds()
    }

    /**
     * Returns the ending index of the record.
     *
     * @return the ending index of the record, {@value #NOT_RANGED} if the
     * record is not ranged.
     * @see .isRanged
     */
    fun getEndIndex(): Int {
        return endIndex
    }

    /**
     * Sets the ending index of the record.
     *
     * @param endIndex
     * the ending index, a value of {@value #NOT_RANGED} or lower or
     * to the value of the index itself indicates that the record is
     * not ranged.
     */
    fun setEndIndex(endIndex: Int) {
        var endIndex = endIndex
        if (endIndex <= index) {
            endIndex = NOT_RANGED
        }
        this.endIndex = endIndex
        updateSequenceIds()
    }

    /**
     * Returns whether or not the record is ranged.
     *
     * @return `true` if the record is ranged, `false`
     * otherwise.
     */
    val isRanged: Boolean
        get() = endIndex > NOT_RANGED

    /**
     * Returns the sequence ID contained within this record. This is the
     * equivalent of calling [.getIndex], however an error will be
     * thrown if the record is ranged.
     *
     * @return the sequence ID contained within this record.
     * @throws ArrayStoreException
     * if the record is ranged according to the [.isRanged]
     * method.
     * @see .getSequenceIds
     */
    @get:Throws(ArrayStoreException::class)
    val sequenceId: Int
        get() {
            if (isRanged) {
                throw ArrayStoreException("Record is ranged, there are multiple IDs")
            }
            return getIndex()
        }

    override fun hashCode(): Int {
        return Objects.hash(index, endIndex)
    }

    override fun equals(o: Any?): Boolean {
        if (o === this) {
            return true
        } else if (o !is Record) {
            return false
        }
        val r = o
        return index == r.index && endIndex == r.endIndex
    }

    override fun toString(): String {
        return "Record [index=$index, endIndex=$endIndex]"
    }

    companion object {
        /**
         * The record is not ranged.
         */
        const val NOT_RANGED = -1

        /**
         * Returns the sequence IDs contained within the specified records.
         *
         * @param records
         * the records to get the sequence IDs from.
         * @return the sequence IDs contained within the specified records.
         */
        fun getSequenceIds(vararg records: Record): IntArray {
            // Get sequence IDs from records
            val sequenceIdsList = ArrayList<Int>()
            for (record in records) {
                for (recordId in record.sequenceIds) {
                    if (!sequenceIdsList.contains(recordId)) {
                        sequenceIdsList.add(recordId)
                    }
                }
            }

            // Convert boxed values to sorted native array
            val sequenceIds = IntArray(sequenceIdsList.size)
            for (i in sequenceIds.indices) {
                sequenceIds[i] = sequenceIdsList[i]
            }
            Arrays.sort(sequenceIds)
            return sequenceIds
        }

        /**
         * Returns the sequence IDs contained within the specified records.
         *
         * @param records
         * the records to get the sequence IDs from.
         * @return the sequence IDs contained within the specified records.
         */
        fun getSequenceIds(records: List<Record>): IntArray {
            return getSequenceIds(*records.toTypedArray())
        }

        /**
         * Simplifies the specified sequence IDs into a `Record[]` with
         * all sequence IDs having their own dedicated record to make handling them
         * easier.
         *
         * @param sequenceIds
         * the sequence IDs to simplify.
         * @return the simplified records
         */
        fun simplify(vararg sequenceIds: Int): Array<Record> {
            val simplified = IntMap<Record>()
            for (i in sequenceIds.indices) {
                if (!simplified.containsKey(sequenceIds[i])) {
                    simplified[sequenceIds[i]] =
                        Record(sequenceIds[i])
                }
            }
            return simplified.values.toTypedArray()
        }

        /**
         * Simplifies the specified records into a `Record[]` with all
         * sequence IDs within the records having their own dedicated record to make
         * handling them easier.
         *
         * @param records
         * the records to simplify.
         * @return the simplified records
         */
        fun simplify(vararg records: Record): Array<Record> {
            return simplify(*getSequenceIds(*records))
        }

        /**
         * Simplifies the specified records into a `Record[]` with all
         * sequence IDs within the records having their own dedicated record to make
         * handling them easier.
         *
         * @param records
         * the records to simplify.
         * @return the simplified records
         */
		@JvmStatic
		fun simplify(records: List<Record>): Array<Record> {
            return simplify(*records.toTypedArray())
        }

        /**
         * Condenses the specified records into a `Record[]` with all
         * ranges of sequence IDs being in ranged records to save memory.
         *
         * @param records
         * the records to condense.
         * @return the condensed records.
         */
        fun condense(vararg records: Record): Array<Record> {
            /*
		 * Get sequence IDs and sort them in ascending order. This is crucial in
		 * order for condensing to occur.
		 */
            val sequenceIds: IntArray = getSequenceIds(*records)
            Arrays.sort(sequenceIds)

            // Condense records
            val condensed = ArrayList<Record>()
            var i = 0
            while (i < sequenceIds.size) {
                val startIndex = sequenceIds[i]
                var endIndex = startIndex
                if (i + 1 < sequenceIds.size) {
                    while (endIndex + 1 == sequenceIds[i + 1]) {
                        endIndex = sequenceIds[++i] // This value is sequential
                        if (i + 1 >= sequenceIds.size) {
                            break
                        }
                    }
                }
                condensed.add(Record(startIndex, if (endIndex == startIndex) -1 else endIndex))
                i++
            }
            return condensed.toTypedArray()
        }

        /**
         * Condenses the specified records into a `Record[]` with all
         * ranges of sequence IDs being in ranged records to save memory.
         *
         * @param records
         * the records to condense.
         * @return the condensed records.
         */
        fun condense(records: List<Record>): Array<Record> {
            return condense(*records.toTypedArray())
        }

        /**
         * Condenses the specified sequence IDs into a `Record[]` with
         * all ranges of sequence IDs being in ranged records to save memory.
         *
         * @param sequenceIds
         * the sequence IDs to condense.
         * @return the condensed records.
         */
        fun condense(vararg sequenceIds: Int): Array<Record> {
            val records = ArrayList<Record>()
            for (i in records.indices) {
                records.add(Record(sequenceIds[i]))
            }
            return condense(records)
        }
    }

    /**
     * Creates a ranged record.
     *
     * @param index
     * the starting index.
     * @param endIndex
     * the ending index.
     * @throws IllegalArgumentException
     * if the `index` is negative.
     */
    init {
        require(index >= 0) { "Index cannot be negative" }
        this.index = index
        this.endIndex = endIndex
        updateSequenceIds()
    }
}