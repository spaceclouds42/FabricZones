package us.spaceclouds42.builders.utils

/**
 * A way to make double ranges iterable
 */
class DoubleRange(min: Double, max: Double) {
    /**
     * The list of doubles that are formed from the [min] and [max]
     */
    private val elements = mutableListOf<Double>()

    /**
     * How much the range should increment between elements
     */
    private val step = (max - min) / ((max - min) * 2)

    /**
     * Populates elements with all the doubles from min to max using step as the interval
     */
    init {
        elements.add(min)
        while (elements.last() < (max + step)) {
            elements.add(elements.last() + step)
        }
        elements.add(max)
    }

    /**
     * A public getter
     *
     * @return an immutable list with the elements in [elements]
     */
    fun getElements(): List<Double> {
        return elements
    }
}