package ernest.linuxcmd

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ernest.linuxcmd.databinding.ItemCmdBinding

/**
 * Created by suqishuo on 2017/3/21.
 * desc:
 */
class CmdListAdapter(var cmdList: List<Pair<String, String>>) :
    RecyclerView.Adapter<CmdListAdapter.CmdItemViewHolder>() {
    private var onListItemClickListener: OnListItemClickListener? = null

    interface OnListItemClickListener {
        fun onListItemClick(position: Int)
    }

    fun setOnListItemClickListener(onListItemClickListener: OnListItemClickListener?) {
        this.onListItemClickListener = onListItemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CmdItemViewHolder {
        return CmdItemViewHolder(
            ItemCmdBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CmdItemViewHolder, position: Int) {
        holder.itemView.setBackgroundColor(
            holder.itemView.resources.getColor(if (position % 2 == 0) R.color.item_bg_odd else R.color.item_bg_even)
        )
        holder.binding.tvTitle.text = cmdList[position].first.replace(".md", "")
        holder.binding.tvDesc.text = cmdList[position].second
        holder.itemView.setOnClickListener {
            onListItemClickListener?.onListItemClick(holder.adapterPosition)
        }
    }

    override fun getItemCount(): Int {
        return cmdList.size
    }

    inner class CmdItemViewHolder(var binding: ItemCmdBinding) :
        RecyclerView.ViewHolder(binding.root)
}