package ernest.linuxcmd;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Created by suqishuo on 2017/3/21.
 * desc:
 */

public class CmdListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<String> cmdList;

    private OnListItemClickListener onListItemClickListener;

    interface OnListItemClickListener {
        void onListItemClick(int position);
    }

    public void setOnListItemClickListener(OnListItemClickListener onListItemClickListener) {
        this.onListItemClickListener = onListItemClickListener;
    }

    public CmdListAdapter(List<String> cmdList) {
        this.cmdList = cmdList;
    }

    @Override

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TextView textView = new TextView(parent.getContext());
        textView.setClickable(true);
        textView.setPadding(8, 8, 8, 8);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textView.setLayoutParams(params);
        return new CmdItemViewHolder(textView);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        holder.itemView.setBackgroundColor(
                holder.itemView.getResources().getColor(position % 2 == 0 ?
                        R.color.item_bg_odd : R.color.item_bg_even));
        ((TextView) holder.itemView).setTextSize(24);
        ((TextView) holder.itemView).setText(cmdList.get(position).replace(".md", ""));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onListItemClickListener != null) {
                    onListItemClickListener.onListItemClick(holder.getAdapterPosition());

                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return cmdList.size();
    }

    private class CmdItemViewHolder extends RecyclerView.ViewHolder {

        CmdItemViewHolder(View itemView) {
            super(itemView);
        }
    }
}
