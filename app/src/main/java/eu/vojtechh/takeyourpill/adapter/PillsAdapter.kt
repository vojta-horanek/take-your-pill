package eu.vojtechh.takeyourpill.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import eu.vojtechh.takeyourpill.R
import eu.vojtechh.takeyourpill.model.Pill

class PillsAdapter internal constructor(
    context: Context
) : RecyclerView.Adapter<PillsAdapter.WordViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var pills = emptyList<Pill>()

    inner class WordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.pillTitle)
        val description: TextView = itemView.findViewById(R.id.pillDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val itemView = inflater.inflate(R.layout.pill_item_layout, parent, false)
        return WordViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        val pill = pills[position]
        holder.name.text = pill.name
        holder.description.text = pill.description
        // TODO Open details view
    }

    internal fun setPills(pills: List<Pill>) {
        this.pills = pills
        notifyDataSetChanged()
    }

    override fun getItemCount() = pills.size
}