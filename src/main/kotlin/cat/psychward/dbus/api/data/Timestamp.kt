package cat.psychward.dbus.api.data

class Timestamp(val nanoseconds: Long) {

    fun toSeconds(): Double {
        return nanoseconds / 1_000_000_000.0
    }

    fun toMillis(): Double {
        return nanoseconds / 1_000_000.0
    }

    override fun toString(): String {
        return "Timestamp(${toMillis()}ms)"
    }
}

fun fromMillis(milliseconds: Double): Timestamp {
    return Timestamp((milliseconds * 1_000_000).toLong())
}

fun fromSeconds(seconds: Double): Timestamp {
    return Timestamp((seconds * 1_000_000_000).toLong())
}