package eu.vojtechh.takeyourpill.model

import com.github.mikephil.charting.data.PieData

data class ChartItem(
    val pieData: PieData,
    val title: String
) : BaseModel() {
    override val itemType = ItemType.CHART
    override fun isSame(newItem: BaseModel) =
        if (newItem is ChartItem) this.title == newItem.title else false

    override fun isContentSame(newItem: BaseModel) = if (newItem is ChartItem) {
        val set = this.pieData.dataSet.getEntriesForXValue(0F)
        val newSet = newItem.pieData.dataSet.getEntriesForXValue(0F)
        set.containsAll(newSet) && newSet.containsAll(set)
    } else false
}
