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
package bedrockDragon.network.raknet.map

import java.util.HashMap
import bedrockDragon.network.raknet.map.DynamicKey
import kotlin.Throws
import java.lang.NullPointerException

/**
 * This class is used for using longs as keys in normal maps without having to
 * worry about boxing them.
 *
 * @author "Whirvis" Trent Summerlin
 * @since JRakNet v2.6.0
 */
class LongMap<T> : HashMap<Long?, T>(), MutableMap<Long?, T>, DynamicKey<Long?> {
    /**
     * Returns <tt>true</tt> if this map contains a mapping for the specified
     * key.
     *
     * @param key
     * The key whose presence in this map is to be tested
     * @return <tt>true</tt> if this map contains a mapping for the specified
     * key.
     */
    fun containsKey(key: Long): Boolean {
        return super.containsKey(key)
    }

    /**
     * Returns <tt>true</tt> if this map maps one or more keys to the specified
     * value.
     *
     * @param value
     * value whose presence in this map is to be tested
     * @return <tt>true</tt> if this map maps one or more keys to the specified
     * value
     */
    override fun containsValue(value: T): Boolean {
        return super.containsValue(value)
    }

    /**
     * Returns the value to which the specified key is mapped, or `null`
     * if this map contains no mapping for the key.
     *
     *
     *
     * More formally, if this map contains a mapping from a key `k` to a
     * value `v` such that `(key==null ? k==null :
     * key.equals(k))`, then this method returns `v`; otherwise it returns
     * `null`. (There can be at most one such mapping.)
     *
     *
     *
     * A return value of `null` does not *necessarily* indicate that
     * the map contains no mapping for the key; it's also possible that the map
     * explicitly maps the key to `null`. The [ containsKey][.containsKey] operation may be used to distinguish these two cases.
     *
     * @see .put
     * @param key
     * they key the value is mapped to.
     * @return the value to which the specified key is mapped.
     */
    operator fun get(key: Long): T? {
        return super.get(key)
    }

    /**
     * Associates the specified value with the specified key in this map. If the
     * map previously contained a mapping for the key, the old value is
     * replaced.
     *
     * @param key
     * key with which the specified value is to be associated
     * @param value
     * value to be associated with the specified key
     * @return the previous value associated with <tt>key</tt>, or <tt>null</tt>
     * if there was no mapping for <tt>key</tt>. (A <tt>null</tt> return
     * can also indicate that the map previously associated
     * <tt>null</tt> with <tt>key</tt>.)
     */
    fun put(key: Long, value: T): T? {
        return super.put(key, value)
    }

    /**
     * Removes the mapping for the specified key from this map if present.
     *
     * @param key
     * key whose mapping is to be removed from the map
     * @return the previous value associated with <tt>key</tt>, or <tt>null</tt>
     * if there was no mapping for <tt>key</tt>. (A <tt>null</tt> return
     * can also indicate that the map previously associated
     * <tt>null</tt> with <tt>key</tt>.)
     */
    fun remove(key: Long): T {
        return super<HashMap>.remove(key)!!//TODO
    }

    @Throws(NullPointerException::class)
    override fun renameKey(oldKey: Long?, newKey: Long?) {
        val storedObject: T = this.remove(oldKey) ?: throw NullPointerException("No value associated with old key")
        this[newKey] = storedObject
    }

    companion object {
        private const val serialVersionUID = 4324132003573381634L
    }
}