package bedrockDragon.debug.junit

import bedrockDragon.network.raknet.Packet
import bedrockDragon.network.raknet.RakNet
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.net.InetAddress
import java.net.InetSocketAddress
import kotlin.random.Random
import kotlin.random.nextInt

@OptIn(ExperimentalUnsignedTypes::class)
internal class PacketTest {

    @Test
    fun read() {
    }

    @Test
    fun readByte() {
    }

    @Test
    fun readUnsignedByte() {
    }

    @Test
    fun readBoolean() {
    }

    @Test
    fun readChar() {
    }

    @Test
    fun readCharLE() {
    }

    @Test
    fun readShort() {
    }

    @Test
    fun readShortLE() {
    }

    @Test
    fun readUnsignedShort() {
    }

    @Test
    fun readUnsignedShortLE() {
    }

    @Test
    fun readTriad() {
    }

    @Test
    fun readTriadLE() {
    }

    @Test
    fun readUnsignedTriad() {
    }

    @Test
    fun readUnsignedTriadLE() {
    }

    @Test
    fun readInt() {
    }

    @Test
    fun readIntLE() {
    }

    @Test
    fun readUnsignedInt() {
    }

    @Test
    fun readUnsignedIntLE() {
    }

    @Test
    fun readLong() {
        val packet = Packet()
        val long = generateLong()
        packet.buffer().writeLong(long)

        assert(long == packet.readLong())
    }

    @Test
    fun readLongLE() {
    }

    @Test
    fun readUnsignedLong() {
    }

    @Test
    fun readUnsignedLongLE() {
    }

    @Test
    fun readFloat() {
    }

    @Test
    fun readFloatLE() {
    }

    @Test
    fun readDouble() {
    }

    @Test
    fun readDoubleLE() {
    }

    @Test
    fun readVarInt() {
    }

    @Test
    fun readUnsignedVarInt() {
    }

    @Test
    fun readVarLong() {
    }

    @Test
    fun readUnsignedVarLong() {
    }

    @Test
    fun readString() {
    }

    @Test
    fun readStringLE() {
    }

    @Test
    fun readAddress() {
        val address = generateIPAddress()
        val packet = Packet()
        println(address)
        packet.writeAddress(address)
        println(packet.readAddress())
        assert(packet.readAddress() == address)
    }

    @Test
    fun readUUID() {
    }

    @Test
    fun writeB() {
    }

    @Test
    fun write() {
    }

    @Test
    fun writeByte() {
        val packet = Packet()
        val byte = generateSignedByte()
        //Byte parameter
        packet.writeByte(byte)
        assert(packet.buffer().getByte(0) == byte)
        //Int parameter
        packet.writeByte(byte.toInt())
        assert(packet.buffer().getByte(0) == byte)

    }

    @Test
    fun writeUnsignedByte() {
        val packet = Packet()
        val byte = generateUnsignedByte()
        //Byte parameter
        packet.writeUnsignedByte(byte)
        assert(packet.buffer().getUnsignedByte(0).toUByte() == byte)
        //Int parameter
        packet.writeByte(byte.toInt())
        assert(packet.buffer().getUnsignedByte(0).toUByte() == byte)
    }

    @Test
    fun writeBoolean() {
        val packet = Packet()
        val byte = Random.nextBoolean()
        //Byte parameter
        packet.writeBoolean(byte)
        assert(packet.buffer().getBoolean(0) == byte)
    }

    @Test
    fun writeShort() {
        val packet = Packet()
        val byte = generateShort()
        //Byte parameter
        packet.writeShort(byte)
        assert(packet.buffer().getShort(0) == byte)
        //Int parameter
        packet.writeByte(byte.toInt())
        assert(packet.buffer().getShort(0) == byte)
    }

    @Test
    fun writeShortLE() {
    }

    @Test
    fun writeUnsignedShort() {
    }

    @Test
    fun writeUnsignedShortLE() {
    }

    @Test
    fun writeTriad() {
    }

    @Test
    fun writeTriadLE() {
    }

    @Test
    fun writeUnsignedTriad() {
    }

    @Test
    fun writeUnsignedTriadLE() {
    }

    @Test
    fun writeInt() {
        val packet = Packet()
        val byte = generateInt()
        //Byte parameter
        packet.writeInt(byte)
        assert(packet.buffer().getInt(0) == byte)
    }

    @Test
    fun writeUnsignedInt() {
        val packet = Packet()
        val byte = generateUInt()
        //Byte parameter
        packet.writeUnsignedInt(byte)
        assert(packet.buffer().getUnsignedInt(0).toUInt() == byte)
        //Long parameter
        packet.writeUnsignedInt(byte.toLong())
        assert(packet.buffer().getUnsignedInt(0) == byte.toLong())
    }

    @Test
    fun writeIntLE() {
    }

    @Test
    fun writeUnsignedIntLE() {
    }

    @Test
    fun writeLong() {
        val packet = Packet()
        val long = generateLong()

        packet.writeLong(long)
        assert(packet.buffer().readLong() == long)
    }

    @Test
    fun writeLongLE() {
    }

    @Test
    fun writeUnsignedLong() {
    }

    @Test
    fun testWriteUnsignedLong() {
    }

    @Test
    fun writeUnsignedLongLE() {
    }

    @Test
    fun testWriteUnsignedLongLE() {
    }

    @Test
    fun writeFloat() {
    }

    @Test
    fun writeFloatLE() {
    }

    @Test
    fun writeDouble() {
    }

    @Test
    fun writeDoubleLE() {
    }

    @Test
    fun writeVarInt() {
    }

    @Test
    fun writeUnsignedVarInt() {
    }

    @Test
    fun writeVarLong() {
    }

    @Test
    fun writeUnsignedVarLong() {
    }

    @Test
    fun writeString() {
    }

    @Test
    fun writeAddress() {
        val packet = Packet()
        val address = generateIPAddress()
        println(address.address)
        packet.writeAddress(address)
        when(packet.buffer().readUnsignedByte().toInt()) {
            4 -> {
                assert(
                    InetSocketAddress(
                        "${packet.buffer().readUnsignedByte()}.${
                            packet.buffer().readUnsignedByte()
                        }.${packet.buffer().readUnsignedByte()}.${packet.buffer().readUnsignedByte()}",
                        packet.buffer().readUnsignedShort()
                    ) == address
                )
            }
            6 -> {

            }
            else -> {
                assert(false)
            }

        }

        //assert("${packet.buffer().readUnsignedByte()}.${packet.buffer().readUnsignedByte()}")
    }

    @Test
    fun writeUUID() {
    }

    /*
    BEGIN HELPER FUNCTIONS
     */
    private val RAKNET_VERSION = 10

    private fun generateSignedByte() : Byte {
        return Random.nextInt(-128,127).toByte()
    }

    private fun generateUnsignedByte() : UByte {
        return Random.nextInt(0,255).toUByte()
    }

    private fun generateShort() : Short {
        return Random.nextInt(-32678, 32677).toShort()
    }

    private fun generateInt(): Int {
        return Random.nextInt()
    }

    private fun generateUInt(): UInt {
        return Random.nextLong(0,4294967295).toUInt()
    }

    private fun generateUShort() : UShort {
        return Random.nextInt(0, 65535).toUShort()
    }

    private fun generateIPAddress(): InetSocketAddress {
        //just IPv4 for now
        //generate random address
        val ip1 = generateUnsignedByte()
        val ip2 = generateUnsignedByte()
        val ip3 = generateUnsignedByte()
        val ip4 = generateUnsignedByte()

        val port = generateUShort().toInt()

        return InetSocketAddress("$ip1.$ip2.$ip3.$ip4", port)
    }

    private fun generateLong(): Long {
        return Random.nextLong()
    }

    private fun generateULong() : ULong {
        return (Random.nextLong() + 0xFFFFFFFF).toULong()
    }
}