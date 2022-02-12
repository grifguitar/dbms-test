class Attribute(inputName: String) {
    companion object {
        private val values: HashSet<String> = HashSet()

        fun getValues(): HashSet<String> {
            return values
        }
    }

    private val name: String = inputName

    init {
        if (name == "") {
            throw RuntimeException(
                "expected attribute not from empty string: " + toString()
            )
        }
        for (x in name) {
            if ((x in 'a'..'z') || (x in 'A'..'Z')) {
                // nothing
            } else {
                throw RuntimeException(
                    "expected attribute name from english letters: " + toString()
                )
            }
        }
        values.add(name)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Attribute

        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

    override fun toString(): String {
        return "At($name)"
    }
}

class CommonRule {
    private val left: HashSet<Attribute> = HashSet()
    private val right: HashSet<Attribute> = HashSet()

    fun getLeft(): HashSet<Attribute> {
        return left
    }

    fun getRight(): HashSet<Attribute> {
        return right
    }

    fun addLeft(arg: Attribute) {
        left.add(arg)
    }

    fun addRight(arg: Attribute) {
        right.add(arg)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CommonRule

        if (left != other.left) return false
        if (right != other.right) return false

        return true
    }

    override fun hashCode(): Int {
        var result = left.hashCode()
        result = 31 * result + right.hashCode()
        return result
    }

    override fun toString(): String {
        var ans = "CommonRule {"
        for (x in left) ans += ("$x,")
        if (left.size != 0) ans = ans.dropLast(1)
        ans += "->"
        for (x in right) ans += ("$x,")
        if (right.size != 0) ans = ans.dropLast(1)
        ans += "}"
        return ans
    }
}

class RulesPack {
    private val rules: HashSet<CommonRule> = HashSet()

    fun getRules(): HashSet<CommonRule> {
        return rules
    }

    fun addRule(arg: CommonRule) {
        rules.add(arg)
    }

    override fun toString(): String {
        var ans = "CommonPack {\n"
        for (x in rules) {
            ans += ("    $x\n")
        }
        ans += "}"
        return ans
    }
}

class AttributesPack {
    private val attributes: HashSet<Attribute> = HashSet()

    fun contains(args: HashSet<Attribute>): Boolean {
        for (arg in args) {
            if (!attributes.contains(arg)) {
                return false
            }
        }
        return true
    }

    fun get(): HashSet<Attribute> {
        return attributes
    }

    fun add(arg: Attribute) {
        attributes.add(arg)
    }

    fun add(args: HashSet<Attribute>) {
        attributes.addAll(args)
    }

    fun add(args: AttributesPack) {
        attributes.addAll(args.attributes)
    }

    override fun toString(): String {
        var ans = "AttributePack {"
        for (x in attributes) ans += ("$x,")
        if (attributes.size != 0) ans = ans.dropLast(1)
        ans += "}"
        return ans
    }
}

fun getClosure(attributesPack: AttributesPack, rulesPack: RulesPack): AttributesPack {
    val curr = AttributesPack()
    curr.add(attributesPack)
    while (true) {
        val cnt = curr.get().size
        for (rule in rulesPack.getRules()) {
            if (curr.contains(rule.getLeft())) {
                curr.add(rule.getRight())
            }
        }
        if (curr.get().size == cnt) {
            break
        }
    }
    return curr
}

fun main() {
    println("? enter functional dependencies (one on each line):")

    val rulesPack = RulesPack()

    var line: String? = readLine()
    while (line != null && line != "exit") {
        val rule: List<String> = line.split("->")
        if (rule.size != 2) {
            throw RuntimeException("bad split by \"->\": $line")
        }
        val ruleLeft = rule[0].split(" ")
        val ruleRight = rule[1].split(" ")

        val commonRule = CommonRule()
        for (x in ruleLeft) {
            if (x != "")
                commonRule.addLeft(Attribute(x))
        }
        for (y in ruleRight) {
            if (y != "")
                commonRule.addRight(Attribute(y))
        }

        rulesPack.addRule(commonRule)

        line = readLine()
    }

    println(rulesPack)
    println()
    println("? enter the set of attributes you want to close:")

    val attributesPack = AttributesPack()

    line = readLine()
    while (line != null && line != "exit") {
        val attributes: List<String> = line.split(" ")
        for (x in attributes) {
            if (x != "")
                attributesPack.add(Attribute(x))
        }

        line = readLine()
    }

    println(attributesPack)
    println()
    println("! response, closure:")

    val closure = getClosure(attributesPack, rulesPack)

    println(closure)
    println()

    println("! check, here are all the attributes:")
    println(Attribute.getValues())

}
