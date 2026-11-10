package com.isaiahnoelpulidosalazar.inpsAndroid;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.isaiahnoelpulidosalazar.inpsandroid.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * A vertical {@link RecyclerView} of string items rendered as buttons, with click and long-click
 * listeners.
 *
 * <p>v2 uses <b>one</b> adapter for the lifetime of the view and fine-grained change notifications
 * (v1 created a new adapter on every change, which reset scroll position, disabled item
 * animations and rebound every row).</p>
 *
 * <pre>{@code
 * SimpleList list = findViewById(R.id.list);
 * list.addItem("Apple");
 * list.setOnItemClickListener(position -> show(list.getItems().get(position)));
 * }</pre>
 */
public class SimpleList extends RecyclerView {

    private final List<String> items = new ArrayList<>();
    private final ItemAdapter adapter = new ItemAdapter();
    private OnItemClickListener clickListener;
    private OnItemLongClickListener longClickListener;
    private int padLeftPx, padTopPx, padRightPx, padBottomPx;

    public SimpleList(Context context) { super(context); init(context); }

    public SimpleList(Context context, AttributeSet attrs) { super(context, attrs); init(context); }

    public SimpleList(Context context, AttributeSet attrs, int defStyle) { super(context, attrs, defStyle); init(context); }

    private void init(Context context) {
        setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        setAdapter(adapter);
    }

    /** Listeners are read at click time, so they can be changed without rebuilding anything. */
    public void setOnItemClickListener(OnItemClickListener listener) { this.clickListener = listener; }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) { this.longClickListener = listener; }

    public void addItem(String item) {
        items.add(item);
        adapter.notifyItemInserted(items.size() - 1);
    }

    public void addItems(Collection<String> newItems) {
        if (newItems.isEmpty()) return;
        int start = items.size();
        items.addAll(newItems);
        adapter.notifyItemRangeInserted(start, newItems.size());
    }

    /** Replaces all items. */
    public void setItems(Collection<String> newItems) {
        items.clear();
        items.addAll(newItems);
        adapter.notifyDataSetChanged();
    }

    /** @throws IndexOutOfBoundsException if position is out of range */
    public void removeItem(int position) {
        items.remove(position);
        adapter.notifyItemRemoved(position);
    }

    public void clear() {
        int count = items.size();
        items.clear();
        adapter.notifyItemRangeRemoved(0, count);
    }

    /** @return a read-only live view of the items; mutate through {@link #addItem}, {@link #removeItem}, etc. */
    public List<String> getItems() {
        return Collections.unmodifiableList(items);
    }

    /** @deprecated Use {@link #getItems()}. The returned list is now read-only. */
    @Deprecated
    public List<String> getData() {
        return getItems();
    }

    /** Uniform item padding in dp. */
    public void setItemPadding(int dp) {
        setItemPadding(dp, dp, dp, dp);
    }

    /** Per-side item padding in dp. Applies to existing rows immediately. */
    public void setItemPadding(int left, int top, int right, int bottom) {
        Context c = getContext();
        padLeftPx = Math.round(RoundedViewHelper.dp(c, left));
        padTopPx = Math.round(RoundedViewHelper.dp(c, top));
        padRightPx = Math.round(RoundedViewHelper.dp(c, right));
        padBottomPx = Math.round(RoundedViewHelper.dp(c, bottom));
        adapter.notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(int position);
    }

    private final class ItemAdapter extends RecyclerView.Adapter<Holder> {
        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_list_item, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            holder.button.setText(items.get(position));
            holder.button.setPadding(padLeftPx, padTopPx, padRightPx, padBottomPx);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }

    private final class Holder extends RecyclerView.ViewHolder {
        final Button button;

        Holder(@NonNull View itemView) {
            super(itemView);
            button = itemView.findViewById(R.id.simple_list_item_id);
            itemView.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position != NO_POSITION && clickListener != null) clickListener.onItemClick(position);
            });
            itemView.setOnLongClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position == NO_POSITION || longClickListener == null) return false;
                longClickListener.onItemLongClick(position);
                return true;
            });
        }
    }
}
