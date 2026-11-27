package com.isaiahnoelpulidosalazar.inpsandroid.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.AsyncDifferConfig;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.isaiahnoelpulidosalazar.inpsandroid.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * A vertical list of string items backed by an encapsulated {@link RecyclerView} and
 * {@link ListAdapter} with {@link DiffUtil}, supporting click and long-click listeners.
 *
 * <pre>{@code
 * SimpleList list = findViewById(R.id.list);
 * list.addItem("Apple");
 * list.setOnItemClickListener(position -> show(list.getItems().get(position)));
 * }</pre>
 */
public class SimpleList extends FrameLayout {

    private static final DiffUtil.ItemCallback<String> DIFF_CALLBACK = new DiffUtil.ItemCallback<String>() {
        @Override
        public boolean areItemsTheSame(@NonNull String oldItem, @NonNull String newItem) {
            return Objects.equals(oldItem, newItem);
        }

        @Override
        public boolean areContentsTheSame(@NonNull String oldItem, @NonNull String newItem) {
            return Objects.equals(oldItem, newItem);
        }
    };

    private final List<String> items = new ArrayList<>();
    private final RecyclerView recyclerView;
    private final ItemAdapter adapter;
    private OnItemClickListener clickListener;
    private OnItemLongClickListener longClickListener;
    private int padLeftPx, padTopPx, padRightPx, padBottomPx;

    public SimpleList(@NonNull Context context) {
        super(context);
        recyclerView = new RecyclerView(context);
        adapter = new ItemAdapter();
        init(context);
    }

    public SimpleList(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        recyclerView = new RecyclerView(context, attrs);
        adapter = new ItemAdapter();
        init(context);
    }

    public SimpleList(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        recyclerView = new RecyclerView(context, attrs, defStyle);
        adapter = new ItemAdapter();
        init(context);
    }

    private void init(Context context) {
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        addView(recyclerView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }

    /** Listeners are read at click time, so they can be changed without rebuilding anything. */
    public void setOnItemClickListener(OnItemClickListener listener) { this.clickListener = listener; }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) { this.longClickListener = listener; }

    public void addItem(String item) {
        items.add(item);
        submitSnapshot();
    }

    public void addItems(Collection<String> newItems) {
        if (newItems.isEmpty()) return;
        items.addAll(newItems);
        submitSnapshot();
    }

    /** Replaces all items, snapshotting {@code newItems} before clearing so {@code setItems(getItems())} is safe. */
    public void setItems(Collection<String> newItems) {
        List<String> snapshot = new ArrayList<>(newItems);
        items.clear();
        items.addAll(snapshot);
        adapter.submitList(snapshot);
    }

    /** @throws IndexOutOfBoundsException if position is out of range */
    public void removeItem(int position) {
        items.remove(position);
        submitSnapshot();
    }

    public void clear() {
        items.clear();
        submitSnapshot();
    }

    /** @return a read-only live view of the items; mutate through {@link #addItem}, {@link #removeItem}, etc. */
    public List<String> getItems() {
        return Collections.unmodifiableList(items);
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

    /** Package-private accessor for tests needing to inspect or lay out the internal RecyclerView. */
    RecyclerView getRecyclerView() {
        return recyclerView;
    }

    private void submitSnapshot() {
        adapter.submitList(new ArrayList<>(items));
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(int position);
    }

    private final class ItemAdapter extends ListAdapter<String, Holder> {
        ItemAdapter() {
            super(new AsyncDifferConfig.Builder<>(DIFF_CALLBACK)
                    .setBackgroundThreadExecutor(Runnable::run)
                    .build());
        }

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            TextView row = (TextView) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.inps_simple_list_item, parent, false);
            return new Holder(row);
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            TextView row = (TextView) holder.itemView;
            row.setText(getItem(position));
            row.setPadding(padLeftPx, padTopPx, padRightPx, padBottomPx);
        }
    }

    private final class Holder extends RecyclerView.ViewHolder {
        Holder(@NonNull TextView itemView) {
            super(itemView);
            itemView.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION && clickListener != null) {
                    clickListener.onItemClick(position);
                }
            });
            itemView.setOnLongClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position == RecyclerView.NO_POSITION || longClickListener == null) return false;
                longClickListener.onItemLongClick(position);
                return true;
            });
        }
    }
}
