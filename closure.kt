class Attribute(inputName: String) {
    companion object {
        private val values: HashSet<String> = HashSet()

        fun getValues(): HashSet<String> {
            return values
        }
    }

    private var name: String = inputName

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
    private val left = AttributesPack()
    private val right = AttributesPack()

    fun getLeft(): AttributesPack {
        return left
    }

    fun getRight(): AttributesPack {
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
        for (x in left.get()) ans += ("$x,")
        if (left.get().size != 0) ans = ans.dropLast(1)
        ans += "->"
        for (x in right.get()) ans += ("$x,")
        if (right.get().size != 0) ans = ans.dropLast(1)
        ans += "}"
        return ans
    }
}

class SimpleRule {
    private var left = AttributesPack()
    private var right: Attribute? = null

    fun getLeft(): AttributesPack {
        return left
    }

    fun getRight(): Attribute {
        if (right == null) {
            throw RuntimeException("unexpected null exception")
        }
        return right as Attribute
    }

    fun addLeft(arg: AttributesPack) {
        left.add(arg)
    }

    fun addRight(arg: Attribute) {
        right = arg
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SimpleRule

        if (left != other.left) return false
        if (right != other.right) return false

        return true
    }

    override fun hashCode(): Int {
        var result = left.hashCode()
        result = 31 * result + (right?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        var ans = "SimpleRule {"
        for (x in left.get()) ans += ("$x,")
        if (left.get().size != 0) ans = ans.dropLast(1)
        ans += "->"
        ans += ("$right")
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

class SimpleRulesPack {
    val rules: HashSet<SimpleRule> = HashSet()

    fun addRule(arg: SimpleRule) {
        rules.add(arg)
    }

    fun copy(arg: SimpleRulesPack) {
        rules.addAll(arg.rules)
    }

    fun removeRule(arg: SimpleRule) {
        rules.remove(arg)
    }

    override fun toString(): String {
        var ans = "SimpleRulesPack {\n"
        for (x in rules) {
            ans += ("    $x\n")
        }
        ans += "}"
        return ans
    }
}

class AttributesPack {
    private val attributes: HashSet<Attribute> = HashSet()

    fun containsAll(args: AttributesPack): Boolean {
        for (arg in args.get()) {
            if (!attributes.contains(arg)) {
                return false
            }
        }
        return true
    }

    fun contains(arg: Attribute): Boolean {
        if (!attributes.contains(arg)) {
            return false
        }
        return true
    }

    fun get(): HashSet<Attribute> {
        return attributes
    }

    fun add(arg: Attribute) {
        attributes.add(arg)
    }

    fun add(args: AttributesPack) {
        attributes.addAll(args.attributes)
    }

    fun remove(arg: Attribute) {
        attributes.remove(arg)
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
            if (curr.containsAll(rule.getLeft())) {
                curr.add(rule.getRight())
            }
        }
        if (curr.get().size == cnt) {
            break
        }
    }
    return curr
}

fun getClosure(attributesPack: AttributesPack, rulesPack: SimpleRulesPack): AttributesPack {
    val curr = AttributesPack()
    curr.add(attributesPack)
    while (true) {
        val cnt = curr.get().size
        for (rule in rulesPack.rules) {
            if (curr.containsAll(rule.getLeft())) {
                curr.add(rule.getRight())
            }
        }
        if (curr.get().size == cnt) {
            break
        }
    }
    return curr
}

fun closureIsLess(firstArg: RulesPack, secondArg: RulesPack): Boolean {
    for (rule in firstArg.getRules()) {
        val closure: AttributesPack = getClosure(rule.getLeft(), secondArg)
        if (!closure.containsAll(rule.getRight())) {
            return false
        }
    }
    return true
}

fun closureEquals(firstArg: RulesPack, secondArg: RulesPack): Boolean {
    return closureIsLess(firstArg, secondArg) && closureIsLess(secondArg, firstArg)
}

fun getIrreducible(rulesPack: RulesPack): SimpleRulesPack {
    var curr = SimpleRulesPack()
    for (rule in rulesPack.getRules()) {
        for (r in rule.getRight().get()) {
            val newRule = SimpleRule()
            newRule.addLeft(rule.getLeft())
            newRule.addRight(r)
            curr.addRule(newRule)
        }
    }

    val deq: ArrayDeque<SimpleRule> = ArrayDeque()
    for (x in curr.rules) {
        deq.add(x)
    }

    while (!deq.isEmpty()) {
        val rule = deq.removeFirst()
        val left = rule.getLeft()
        val right = rule.getRight()
        for (a in left.get()) {
            val newLeft = AttributesPack()
            newLeft.add(left)
            newLeft.remove(a)
            val newRight = getClosure(newLeft, rulesPack)
            if (newRight.contains(right)) {
                val newRule = SimpleRule()
                newRule.addLeft(newLeft)
                newRule.addRight(right)
                curr.addRule(newRule)
                deq.addLast(newRule)
                curr.removeRule(rule)
            }
        }
    }

    for (rule in curr.rules) {
        val left = rule.getLeft()
        val right = rule.getRight()
        val currCopy = SimpleRulesPack()
        currCopy.copy(curr)
        currCopy.removeRule(rule)
        val newRight = getClosure(left, currCopy)
        if (newRight.contains(right)) {
            curr = currCopy
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
        val ruleLeft = rule[0].split(Regex("\\s|,"))
        val ruleRight = rule[1].split(Regex("\\s|,"))

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
    println(getIrreducible(rulesPack))
    println()
    println("? enter the set of attributes you want to close:")

    val attributesPack = AttributesPack()

    line = readLine()
    while (line != null && line != "exit") {
        val attributes: List<String> = line.split(Regex("\\s|,"))
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
