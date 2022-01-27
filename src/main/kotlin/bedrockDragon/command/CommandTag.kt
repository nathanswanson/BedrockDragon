package bedrockDragon.command

abstract class CommandTag<T>() {
    open var value: T? = null
    abstract val message: String
    abstract var optional: Boolean
    abstract var default: T

    fun asOptional(): CommandTag<T> {
        optional = true
        return this
    }
    fun asDefault(value: T): CommandTag<T> {
        default = value
        return this
    }
}
class CommandIntTag : CommandTag<Int>() {
    override val message: String = "I"
    override var default: Int = 0
    override var value: Int? = 0 //unInit
    override var optional: Boolean = false
}
class CommandStringTag : CommandTag<String>() {
    override val message: String = "I"
    override var default: String = ""
    override var value: String? = "" //unInit
    override var optional: Boolean = false
}
