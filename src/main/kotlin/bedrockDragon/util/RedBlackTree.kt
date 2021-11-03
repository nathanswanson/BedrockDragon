package bedrockDragon.util

sealed class RedBlackTree<E : Comparable<E>> {
    enum class Color { R, B }

    companion object {
        fun <T : Comparable<T>> emptyTree(): RedBlackTree<T> = Empty as RedBlackTree<T>
    }

    object Empty : RedBlackTree<Nothing>()
    data class Tree<E : Comparable<E>>(
        val color: Color,
        val left: RedBlackTree<E>,
        val element: E,
        val right: RedBlackTree<E>
    ) : RedBlackTree<E>() {
        fun balance(): Tree<E> {
            fun buildBalancedTree(
                leftLeft: RedBlackTree<E>,
                leftElement: E,
                leftRight: RedBlackTree<E>,
                midElement: E,
                rightLeft: RedBlackTree<E>,
                rightElement: E,
                rightRight: RedBlackTree<E>
            ) = Tree(
                color = Color.R,
                left = Tree(Color.B, leftLeft, leftElement, leftRight),
                element = midElement,
                right = Tree(Color.B, rightLeft, rightElement, rightRight)
            )

            if (color == Color.B) {
                if (left is Tree<E> && left.color == Color.R) {
                    if (left.left is Tree<E> && left.left.color == Color.R) {
                        return buildBalancedTree(
                            leftLeft = left.left.left,
                            leftElement = left.left.element,
                            leftRight = left.left.right,
                            midElement = left.element,
                            rightLeft = left.right,
                            rightElement = this.element,
                            rightRight = this.right
                        )
                    } else if (left.right is Tree<E> && left.right.color == Color.R) {
                        return buildBalancedTree(
                            leftLeft = left.left,
                            leftElement = left.element,
                            leftRight = left.right.left,
                            midElement = left.right.element,
                            rightLeft = left.right.right,
                            rightElement = this.element,
                            rightRight = this.right
                        )
                    }
                }

                if (right is Tree<E> && right.color == Color.R) {
                    if (right.left is Tree<E> && right.left.color == Color.R) {
                        return buildBalancedTree(
                            leftLeft = this.left,
                            leftElement = this.element,
                            leftRight = right.left.left,
                            midElement = right.left.element,
                            rightLeft = right.left.right,
                            rightElement = right.element,
                            rightRight = right.right
                        )
                    } else if (right.right is Tree<E> && right.right.color == Color.R) {
                        return buildBalancedTree(
                            leftLeft = this.left,
                            leftElement = this.element,
                            leftRight = right.left,
                            midElement = right.element,
                            rightLeft = right.right.left,
                            rightElement = right.right.element,
                            rightRight = right.right.right
                        )
                    }
                }
            }

            return this
        }
    }

    fun contains(element: E): Boolean = when (this) {
        Empty -> false
        is Tree -> when {
            element < this.element -> left.contains(element)
            element > this.element -> right.contains(element)
            else -> true
        }
    }

    fun insert(element: E): Tree<E> {
        fun insertInto(tree: RedBlackTree<E>): Tree<E> =
            when (tree) {
                Empty -> Tree(Color.R, tree, element, tree)
                is Tree -> when {
                    element < tree.element -> tree.copy(left = insertInto(tree.left)).balance()
                    element > tree.element -> tree.copy(right = insertInto(tree.right)).balance()
                    else -> tree
                }
            }

        return insertInto(this).copy(color = Color.B)
    }
}