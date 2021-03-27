package eu.vojtechh.takeyourpill.klass

object NumberPickerHelper {

    private const val MAX_COUNT = 20

    private lateinit var values: List<String>

    fun getDisplayValues(): List<String> {
        if (!this::values.isInitialized) {
            values = sequence {
                for (i in 0 until MAX_COUNT) {
                    for (x in listOf(0F, 0.25F, 0.50F, 0.75F)) {
                        if (i + x != 0F) {
                            if (x == 0F) yield(i.toString())
                            else yield((i + x).toString())
                        }
                    }
                }
                yield(MAX_COUNT.toString())
            }.toList()
        }
        return values
    }

    fun convertToString(numberPickerValue: Int) = getDisplayValues().getOrElse(numberPickerValue - 1) { "1" }
    fun convertToPosition(value: String) = getDisplayValues().indexOfFirst { it == value } + 1
}