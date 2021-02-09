package us.spaceclouds42.builders.ext

import us.spaceclouds42.builders.utils.DoubleRange
import kotlin.math.max
import kotlin.math.min

/**
 * Converts two doubles into a double range
 *
 * @param other the other double to form a range with
 * @return the resulting iterable range
 */
fun Double.toRange(other: Double): DoubleRange {
    return DoubleRange(min(this, other), max(this, other))
}