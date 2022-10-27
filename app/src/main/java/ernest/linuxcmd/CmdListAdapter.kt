package ernest.linuxcmd

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by suqishuo on 2017/3/21.
 * desc:
 */
class CmdListAdapter(var cmdList: List<String>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var onListItemClickListener: OnListItemClickListener? = null

    interface OnListItemClickListener {
        fun onListItemClick(position: Int)
    }

    fun setOnListItemClickListener(onListItemClickListener: OnListItemClickListener?) {
        this.onListItemClickListener = onListItemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val textView = TextView(parent.context)
        textView.isClickable = true
        textView.setPadding(8, 8, 8, 8)
        val params = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        textView.layoutParams = params
        return CmdItemViewHolder(textView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.setBackgroundColor(
            holder.itemView.resources.getColor(if (position % 2 == 0) R.color.item_bg_odd else R.color.item_bg_even)
        )
        (holder.itemView as TextView).textSize = 24f
        (holder.itemView as TextView).text = cmdList[position].replace(".md", "")
        holder.itemView.setOnClickListener {
            onListItemClickListener?.onListItemClick(holder.adapterPosition)
        }
    }

    override fun getItemCount(): Int {
        return cmdList.size
    }

    private inner class CmdItemViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView)
}