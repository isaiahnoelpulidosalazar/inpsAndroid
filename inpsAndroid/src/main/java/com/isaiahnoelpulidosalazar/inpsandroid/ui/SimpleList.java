package com.isaiahnoelpulidosalazar.inpsandroid.ui;

import android.content.Context;
import android.content.res.TypedArray;
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
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * A vertical list of string items backed by an encapsulated {@link RecyclerView} and
 * {@link ListAdapter} with {@link DiffUtil}, supporting click, long-click, and an optional
 * modern checkbox mode ({@link #setCheckboxesEnabled(boolean)} / {@code app:inpsCheckboxesEnabled})
 * with toggleable bounce animations ({@link #setBounceAnimationEnabled(boolean)}).
 *
 * <pre>{@code
 * SimpleList list = findViewById(R.id.list);
 * list.setCheckboxesEnabled(true);
 * list.setBounceAnimationEnabled(true);
 * list.addItem("Apple");
 * list.setOnItemCheckedChangeListener((pos, item, checked) -> updateSelection());
 * }</pre>
 */
public class SimpleList extends FrameLayout {

    private static final int VIEW_TYPE_TEXT = 0;
    private static final int VIEW_TYPE_CHECKBOX = 1;

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
    private final Set<Integer> checkedPositions = new LinkedHashSet<>();
    private final RecyclerView recyclerView;
    private final ItemAdapter adapter;
    private OnItemClickListener clickListener;
    private OnItemLongClickListener longClickListener;
    private OnItemCheckedChangeListener checkedChangeListener;

    private boolean checkboxesEnabled;
    private boolean bounceAnimationEnabled = true;
    private RoundedCheckBox.Style checkStyle = RoundedCheckBox.Style.CARD;
    private float checkboxCornerRadiusDp = 12f;
    private boolean hasCustomCornerRadius;
    private boolean customPaddingSet;
    private int padLeftPx, padTopPx, padRightPx, padBottomPx;

    public SimpleList(@NonNull Context context) {
        super(context);
        recyclerView = new RecyclerView(context);
        adapter = new ItemAdapter();
        init(context, null, 0);
    }

    public SimpleList(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        recyclerView = new RecyclerView(context, attrs);
        adapter = new ItemAdapter();
        init(context, attrs, 0);
    }

    public SimpleList(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        recyclerView = new RecyclerView(context, attrs, defStyle);
        adapter = new ItemAdapter();
        init(context, attrs, defStyle);
    }

    private void init(Context context, @Nullable AttributeSet attrs, int defStyle) {
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SimpleList, defStyle, 0);
            try {
                checkboxesEnabled = a.getBoolean(R.styleable.SimpleList_inpsCheckboxesEnabled, false);
                bounceAnimationEnabled = a.getBoolean(R.styleable.SimpleList_inpsBounceAnimation, true);
                RoundedCheckBox.Style[] styles = RoundedCheckBox.Style.values();
                int idx = a.getInt(R.styleable.SimpleList_inpsCheckStyle, RoundedCheckBox.Style.CARD.ordinal());
                checkStyle = styles[Math.max(0, Math.min(idx, styles.length - 1))];
                if (a.hasValue(R.styleable.SimpleList_inpsCornerRadius)) {
                    hasCustomCornerRadius = true;
                    float px = a.getDimension(R.styleable.SimpleList_inpsCornerRadius,
                            RoundedViewHelper.dp(context, 12f));
                    checkboxCornerRadiusDp = px / context.getResources().getDisplayMetrics().density;
                }
            } finally {
                a.recycle();
            }
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        addView(recyclerView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }

    /** Listeners are read at click time, so they can be changed without rebuilding anything. */
    public void setOnItemClickListener(@Nullable OnItemClickListener listener) { this.clickListener = listener; }

    public void setOnItemLongClickListener(@Nullable OnItemLongClickListener listener) { this.longClickListener = listener; }

    public void setOnItemCheckedChangeListener(@Nullable OnItemCheckedChangeListener listener) {
        this.checkedChangeListener = listener;
    }

    /** Toggles between classic {@link TextView} rows and modern {@link RoundedCheckBox} rows. */
    public void setCheckboxesEnabled(boolean enabled) {
        if (this.checkboxesEnabled != enabled) {
            this.checkboxesEnabled = enabled;
            adapter.notifyDataSetChanged();
        }
    }

    public boolean isCheckboxesEnabled() {
        return checkboxesEnabled;
    }

    /** Enables or disables the checkbox bounce animation across all rows. */
    public void setBounceAnimationEnabled(boolean enabled) {
        if (this.bounceAnimationEnabled != enabled) {
            this.bounceAnimationEnabled = enabled;
            if (checkboxesEnabled) {
                adapter.notifyDataSetChanged();
            }
        }
    }

    public boolean isBounceAnimationEnabled() {
        return bounceAnimationEnabled;
    }

    /** Configures the visual style used by {@link RoundedCheckBox} rows when checkboxes are enabled. */
    public void setCheckStyle(@NonNull RoundedCheckBox.Style style) {
        this.checkStyle = Objects.requireNonNull(style, "style");
        if (checkboxesEnabled) {
            adapter.notifyDataSetChanged();
        }
    }

    public RoundedCheckBox.Style getCheckStyle() {
        return checkStyle;
    }

    /** Sets corner radius in dp for checkbox rows. */
    public void setCheckboxCornerRadiusDp(float dp) {
        this.hasCustomCornerRadius = true;
        this.checkboxCornerRadiusDp = dp;
        if (checkboxesEnabled) {
            adapter.notifyDataSetChanged();
        }
    }

    public void addItem(String item) {
        items.add(item);
        submitSnapshot();
    }

    public void addItems(Collection<String> newItems) {
        if (newItems.isEmpty()) return;
        items.addAll(newItems);
        submitSnapshot();
    }

    /**
     * Replaces all items, snapshotting {@code newItems} before clearing so
     * {@code setItems(getItems())} is safe. Checked states matching item text are preserved.
     */
    public void setItems(Collection<String> newItems) {
        Set<String> previouslyChecked = new HashSet<>();
        for (int idx : checkedPositions) {
            if (idx >= 0 && idx < items.size()) {
                previouslyChecked.add(items.get(idx));
            }
        }

        List<String> snapshot = new ArrayList<>(newItems);
        items.clear();
        items.addAll(snapshot);

        checkedPositions.clear();
        for (int i = 0; i < snapshot.size(); i++) {
            if (previouslyChecked.contains(snapshot.get(i))) {
                checkedPositions.add(i);
            }
        }
        adapter.submitList(snapshot);
    }

    /** @throws IndexOutOfBoundsException if position is out of range */
    public void removeItem(int position) {
        items.remove(position);
        Set<Integer> shifted = new LinkedHashSet<>();
        for (int pos : checkedPositions) {
            if (pos < position) {
                shifted.add(pos);
            } else if (pos > position) {
                shifted.add(pos - 1);
            }
        }
        checkedPositions.clear();
        checkedPositions.addAll(shifted);
        submitSnapshot();
    }

    public void clear() {
        items.clear();
        checkedPositions.clear();
        submitSnapshot();
    }

    /** @return a read-only live view of the items; mutate through {@link #addItem}, {@link #removeItem}, etc. */
    public List<String> getItems() {
        return Collections.unmodifiableList(items);
    }

    /** Sets checked state for {@code position} without triggering the bounce animation. */
    public void setChecked(int position, boolean checked) {
        if (position < 0 || position >= items.size()) {
            throw new IndexOutOfBoundsException("Index: " + position + ", Size: " + items.size());
        }
        boolean changed = checked ? checkedPositions.add(position) : checkedPositions.remove(position);
        if (changed && checkboxesEnabled) {
            adapter.notifyItemChanged(position);
        }
    }

    /** @return {@code true} if the row at {@code position} is currently checked. */
    public boolean isChecked(int position) {
        return checkedPositions.contains(position);
    }

    /** Checks every item whose text is in {@code selectedItems} and unchecks all others. */
    public void setCheckedItems(Collection<String> selectedItems) {
        Set<String> targets = selectedItems == null ? Collections.emptySet() : new HashSet<>(selectedItems);
        checkedPositions.clear();
        for (int i = 0; i < items.size(); i++) {
            if (targets.contains(items.get(i))) {
                checkedPositions.add(i);
            }
        }
        if (checkboxesEnabled) {
            adapter.notifyDataSetChanged();
        }
    }

    /** @return unmodifiable list of currently checked item strings in list order. */
    public List<String> getCheckedItems() {
        List<String> result = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            if (checkedPositions.contains(i)) {
                result.add(items.get(i));
            }
        }
        return Collections.unmodifiableList(result);
    }

    /** @return unmodifiable list of checked row indices in ascending order. */
    public List<Integer> getCheckedPositions() {
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            if (checkedPositions.contains(i)) {
                result.add(i);
            }
        }
        return Collections.unmodifiableList(result);
    }

    /** Checks all items currently in the list. */
    public void checkAll() {
        for (int i = 0; i < items.size(); i++) {
            checkedPositions.add(i);
        }
        if (checkboxesEnabled) {
            adapter.notifyDataSetChanged();
        }
    }

    /** Unchecks all items currently in the list. */
    public void clearChecked() {
        if (checkedPositions.isEmpty()) return;
        checkedPositions.clear();
        if (checkboxesEnabled) {
            adapter.notifyDataSetChanged();
        }
    }

    /** Uniform item padding in dp. */
    public void setItemPadding(int dp) {
        setItemPadding(dp, dp, dp, dp);
    }

    /** Per-side item padding in dp. Applies to existing rows immediately. */
    public void setItemPadding(int left, int top, int right, int bottom) {
        Context c = getContext();
        customPaddingSet = true;
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

    public interface OnItemCheckedChangeListener {
        void onItemCheckedChanged(int position, String item, boolean isChecked);
    }

    private final class ItemAdapter extends ListAdapter<String, RecyclerView.ViewHolder> {
        ItemAdapter() {
            super(new AsyncDifferConfig.Builder<>(DIFF_CALLBACK)
                    .setBackgroundThreadExecutor(Runnable::run)
                    .build());
        }

        @Override
        public int getItemViewType(int position) {
            return checkboxesEnabled ? VIEW_TYPE_CHECKBOX : VIEW_TYPE_TEXT;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            if (viewType == VIEW_TYPE_CHECKBOX) {
                RoundedCheckBox checkBox = (RoundedCheckBox) inflater.inflate(
                        R.layout.inps_simple_list_checkbox_item, parent, false);
                return new CheckboxHolder(checkBox);
            }
            TextView row = (TextView) inflater.inflate(R.layout.inps_simple_list_item, parent, false);
            return new TextHolder(row);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            String value = getItem(position);
            if (holder instanceof CheckboxHolder) {
                CheckboxHolder cbHolder = (CheckboxHolder) holder;
                cbHolder.bind(value, checkedPositions.contains(position));
            } else {
                TextView row = (TextView) holder.itemView;
                row.setText(value);
                row.setPadding(padLeftPx, padTopPx, padRightPx, padBottomPx);
            }
        }
    }

    private final class TextHolder extends RecyclerView.ViewHolder {
        TextHolder(@NonNull TextView itemView) {
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

    private final class CheckboxHolder extends RecyclerView.ViewHolder {
        private final RoundedCheckBox checkBox;
        private boolean binding;

        CheckboxHolder(@NonNull RoundedCheckBox checkBox) {
            super(checkBox);
            this.checkBox = checkBox;
            checkBox.setOnCheckedChangeListener((cb, isChecked) -> {
                if (binding) return;
                int position = getBindingAdapterPosition();
                if (position == RecyclerView.NO_POSITION) return;
                if (isChecked) {
                    checkedPositions.add(position);
                } else {
                    checkedPositions.remove(position);
                }
                if (checkedChangeListener != null && position < items.size()) {
                    checkedChangeListener.onItemCheckedChanged(position, items.get(position), isChecked);
                }
                if (clickListener != null) {
                    clickListener.onItemClick(position);
                }
            });
            checkBox.setOnLongClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position == RecyclerView.NO_POSITION || longClickListener == null) return false;
                longClickListener.onItemLongClick(position);
                return true;
            });
        }

        void bind(String value, boolean isChecked) {
            binding = true;
            try {
                checkBox.setStyle(checkStyle);
                if (hasCustomCornerRadius) {
                    checkBox.setCornerRadiusDp(checkboxCornerRadiusDp);
                }
                checkBox.setBounceAnimationEnabled(bounceAnimationEnabled);
                checkBox.setText(value);
                checkBox.setChecked(isChecked, false);
                if (customPaddingSet) {
                    checkBox.setPadding(padLeftPx, padTopPx, padRightPx, padBottomPx);
                }
            } finally {
                binding = false;
            }
        }
    }
}
