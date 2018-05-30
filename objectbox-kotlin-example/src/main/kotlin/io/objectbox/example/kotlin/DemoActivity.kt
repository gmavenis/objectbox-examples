package io.objectbox.example.kotlin

import android.app.Activity
import android.os.Bundle
import android.util.Log

class DemoActivity : Activity() {

    open class User(var name: String = "Tom", var age: Int = 30) {

        constructor(map: Map<String, Any?>) : this() {
            this.name = map?.get("name") as String
            this.age = map?.get("age") as Int
        }

        fun copy(name: String = this.name, age: Int = this.age) = User(name, age)

        open fun say() {
            log("${this.name} say")
        }

        open fun walk() {
            log("${this.name} walk")
        }

        operator fun component1(): Any {
            return name
        }

        operator fun component2(): Any {
            return age
        }
    }

    interface Action {
        fun say() {
            log("Action say")
        }

        fun walk() {
            log("Action walk")
        }
    }

    class Member : User(), Action {
        override fun say() {
            super<User>.say()
        }

        override fun walk() {
            super<Action>.walk()
        }
    }

    class Staff(name: String, age: Int) : User(name, age), Action {
        override fun say() {
            super<User>.say()
        }

        override fun walk() {
            super<Action>.walk()
        }

        infix fun sing(s: String) {
            log("sing $s")
        }

        fun multi(vararg strings: String) {
            log("multi $strings")
        }
    }

    open class Outer {
        private val bar: Int = 1
        private var fooz: String = "hello"

        inner class Inner {
            fun foo() = bar
            fun baz() {
                this@Outer.fooz = "hi"
            }
        }

        class Nested {
            fun foo() = 2
        }
    }

    val nested = Outer.Nested().foo() // == 2
    val inner = Outer().Inner().foo() // == 1

    ///////////////////
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*-------- Basic ---------*/
        log("===== Basic")
        val s = "hello"
        val x = 1
        val number = 1_000_000
        val b = true
        var array: Array<Int> = Array(3, { it -> it + 1 })
        array = arrayOf(1, 2, 3, 4)
        var l: Int
        var result: Int
        var t: String

        l = if (s != null) s.length else -1
        l = s?.length ?: -1
        if (s is String) {
        }
        t = s as String

        result = if (b) {
            log("true")
            1
        } else {
            log("false")
            0
        }

        when (x) {
            0 -> log("0")
            1, 2 -> log("1 or 2")
            check(x) -> log("${x}")
            else -> log("")
        }
        result = when (x) {
            0 -> 0
            1 -> 1
            else -> 2
        }

        when (x) {
            in 1..10 -> log("x is in the range")
            in array -> log("x is valid")
            !in 10..20 -> log("x is outside the range")
            else -> log("none of the above")
        }
        for (i in array) {
            log("${i}")
        }
        for (i in array.indices) {
            log(array[i])
        }
        for (i in 1..10 step 2) {

        }
        for (i in 10 downTo 1) {

        }
        loop@ for (i in 1..100) {
            for (j in 1..100) {
                if (j > 2) break@loop
            }
        }
        foo()

        /*-------- Class ---------*/
        log("===== Class")
        checkClass(User::class.java)

        val user = User(mapOf(
                "name" to "John",
                "age" to 25
        ))
        val member1 = Member()
        val member2 = member1.copy("Ryan")
        val staff = Staff("Peter", 10)

        log("- ${user.name}, ${user.age}")
        user.say()
        user.walk()
        val (name, age) = user
        log("$name $age")

        log("- ${member1.name}, ${member1.age}")
        member1.say()
        member1.walk()

        log("- ${member2.name}, ${member2.age}")
        member2.say()
        member2.walk()

        log("- ${staff.name}, ${staff.age}")
        staff.say()
        staff.walk()
        staff.multi("a", "b", "c")

        /*-------- Collection ---------*/
        log("===== Collection")
        var list = mutableListOf(3, 5, 7)
        list.forEach {
            log(it)
        }
        list.any { it == 3 } // true
        list.all { it % 3 == 0 } // true

        /*-------- Function ---------*/
        log("===== Function")
        staff.sing("a song")
        staff sing "a song" // Infix

        higherOrderFunction(x, ::check)

        fun isOdd(x: Int) = x % 2 != 0
        fun isOdd(s: String) = s == "brillig" || s == "slithy" || s == "tove"

        val numbers = listOf(1, 2, 3)
        log(numbers.filter(::isOdd)) // refers to isOdd(x: Int)
    }

    ///////////////////////

    companion object {
        fun log(s: Any?) {
            Log.e("abc", s?.toString() ?: "null")
        }
    }

    fun check(x: Int?): Int {
        fun localFunction(): Int = x!! + 1 // local function
        val result = localFunction()
        return result
    }

    fun higherOrderFunction(x: Int, receiver: (Int?) -> Int) {
        val result = x + receiver(x)
        log("result = $result")
    }

    fun <T> checkClass(cl: Class<T>?) {
        log("${cl?.name}")
    }

    fun foo(): Boolean {
        var ints: Array<Int> = arrayOf(1, 3, 5, 6)

        forEach@ ints.forEach {
            if (it == 0) return forEach@ true
            log(it)
        }
        return false
    }
}