package com.yao.rentingmap;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yao
 */

public class CardGalleryView extends RecyclerView {

    //CardGalleryView适配器
    private Adapter<?> wrapperAdapter;

    //当前item的索引
    private int currentIndex = 0;

    //改变选中的监听器集合
    private List<OnPageChangedListener> mOnPageChangedListeners;

    //缩放的系数
    private int scaleMultiple = 10;

    public CardGalleryView(Context context) {
        super(context);
        init();
    }

    public CardGalleryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CardGalleryView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    @Override
    public void setAdapter(RecyclerView.Adapter adapter) {
        wrapperAdapter = transformCardGalleryAdapter(adapter);
        super.setAdapter(wrapperAdapter);
        initPosition();
    }

    @Override
    public void swapAdapter(RecyclerView.Adapter adapter, boolean removeAndRecycleExistingViews) {
        wrapperAdapter = transformCardGalleryAdapter(adapter);
        super.swapAdapter(wrapperAdapter, removeAndRecycleExistingViews);
        initPosition();
    }

    @Override
    public RecyclerView.Adapter getAdapter() {
        if (wrapperAdapter != null) {
            return wrapperAdapter.adapter;
        }
        return null;
    }

    /**
     * 获取包装的Adapter
     *
     * @return
     */
    public Adapter getWrapperAdapter() {
        return wrapperAdapter;
    }

    /**
     * 初始化item位置
     */
    private void initPosition() {
        scrollToPosition(getMiddlePosition());
        new Thread() {
            @Override
            public void run() {
                while (getChildAt(0) == null) ;
                post(new Runnable() {
                    @Override
                    public void run() {
                        alignView(0);
                    }
                });
            }
        }.start();
    }

    /**
     * 转换为CardGallery.Adapter
     *
     * @param adapter
     * @return
     */
    protected Adapter transformCardGalleryAdapter(RecyclerView.Adapter adapter) {
        return (adapter instanceof Adapter) ? (Adapter) adapter : new Adapter(this, adapter);

    }


    private void init() {
        setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));//设置横向滚动
        setHorizontalScrollBarEnabled(false);//隐藏滚动条

        //添加滚动监听
        addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == SCROLL_STATE_IDLE) {//停止滚动
                    int newIndex = getItem4ScreenXMin();//获取最靠近中线的Item索引
                    if (mOnPageChangedListeners != null) {//触发监听器
                        for (OnPageChangedListener onPageChangedListener : mOnPageChangedListeners) {
                            if (onPageChangedListener != null) {
                                onPageChangedListener.OnPageChanged(getActualCurrentIndex(), getChildAt(newIndex).getId());
                            }
                        }
                    }
                    alignView(newIndex);//Item对齐
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int screenX = getView4ScreenX(CardGalleryView.this);
                for (int i = 0; i < getChildCount(); i++) {
                    View view = getChildAt(i);
                    int height = view.getHeight();
                    int width = view.getWidth();
                    int difference = Math.abs(screenX - getView4ScreenX(view));//item与recyclerView的距离
                    int padding = difference / scaleMultiple;
                    view.setPadding(padding, (int) (padding * (float) height / width), padding, (int) (padding * (float) height / width));
                    view.getLayoutParams().height = height;//保持item高度不变
                    view.requestLayout();
                    //快速滚动时可能没有difference==0的情况，所以这里用difference <= 100来触发选中Item
                    if (difference <= 100) {
                        if (mOnPageChangedListeners != null) {//触发监听器
                            for (OnPageChangedListener onPageChangedListener : mOnPageChangedListeners) {
                                if (onPageChangedListener != null) {
                                    onPageChangedListener.OnPageChanged(getActualCurrentIndex(), view.getId());
                                }
                            }
                        }
                        currentIndex = i;
                    }
                }
            }
        });
    }

    public int getScaleMultiple() {
        return scaleMultiple;
    }

    public void setScaleMultiple(int scaleMultiple) {
        this.scaleMultiple = scaleMultiple;
    }

    /**
     * 根据索引居中item
     *
     * @param newIndex
     */
    private void alignView(int newIndex) {
        currentIndex = newIndex;
        alignView(getChildAt(newIndex));
    }

    /**
     * 居中view
     *
     * @param view
     */
    private void alignView(View view) {
        int dx = getView4ScreenX(CardGalleryView.this) - getView4ScreenX(view);
        smoothScrollBy(-dx, 0);
    }

    //获取当前item真实索引
    public int getActualCurrentIndex() {
        return getChildAt(currentIndex).getId();
    }

    /**
     * 获取真实的item数
     *
     * @return
     */
    private int getActualItemCountFromAdapter() {
        return (getWrapperAdapter()).getActualItemCount();
    }

    private int getMiddlePosition() {
        int middlePosition = Integer.MAX_VALUE / 2;
        final int actualItemCount = getActualItemCountFromAdapter();
        if (actualItemCount > 0 && middlePosition % actualItemCount != 0) {
            middlePosition = middlePosition - middlePosition % actualItemCount;
        }
        return middlePosition;
    }

    /**
     * 获取距离RecyclerView中线最接近item的索引
     *
     * @return
     */
    private int getItem4ScreenXMin() {
        int screenX = getView4ScreenX(this);
        int min = 10086;
        int item = 0;
        for (int i = 0; i < getChildCount(); i++) {
            int difference = screenX - getView4ScreenX(getChildAt(i));
            if (Math.abs(min) > Math.abs(difference)) {
                min = difference;
                item = i;
            }
        }
        return item;
    }

    /**
     * 获取view中线相对于屏幕的x坐标
     *
     * @param view
     * @return
     */
    private int getView4ScreenX(View view) {
        int w = view.getWidth();
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        return w / 2 + location[0];
    }


    public static class Adapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> implements View.OnClickListener {

        RecyclerView.Adapter<VH> adapter;
        CardGalleryView cardGalleryView;
        View.OnClickListener onClickListener;//当前选中item的点击监听器
        int itemWidth;//item的宽度

        public Adapter(final CardGalleryView cardGalleryView, RecyclerView.Adapter adapter) {
            this.adapter = adapter;
            this.cardGalleryView = cardGalleryView;

            //默认item宽度为RecyclerView的2/3
            cardGalleryView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    if (itemWidth == 0 && cardGalleryView.getWidth() != 0) {
                        itemWidth = (int) (cardGalleryView.getWidth() / 3f * 2);
                        cardGalleryView.removeOnLayoutChangeListener(this);
                        notifyDataSetChanged();
                    }
                }
            });
        }

        public int getItemWidth() {
            return itemWidth;
        }

        public void setItemWidth(int itemWidth) {
            this.itemWidth = itemWidth;
        }

        public View.OnClickListener getOnClickListener() {
            return onClickListener;
        }

        public Adapter<VH> setOnClickListener(View.OnClickListener onClickListener) {
            this.onClickListener = onClickListener;
            return this;
        }

        @Override
        public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            VH vh = adapter.onCreateViewHolder(parent, viewType);
            vh.itemView.setOnClickListener(this);
            return vh;
        }

        @Override
        public void onBindViewHolder(VH holder, int position) {
            adapter.onBindViewHolder(holder, getActualPosition(position));
            ViewGroup.LayoutParams lp;
            int width = itemWidth == 0 ? ViewGroup.LayoutParams.MATCH_PARENT : itemWidth;
            if (holder.itemView.getLayoutParams() == null) {
                lp = new ViewGroup.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT);
            } else {
                lp = holder.itemView.getLayoutParams();
                lp.width = width;
                lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            }
            holder.itemView.setLayoutParams(lp);
            holder.itemView.setId(getActualPosition(position));
        }

        @Override
        public int getItemCount() {
            return Integer.MAX_VALUE;
        }

        public int getActualItemCount() {
            return adapter.getItemCount();
        }

        public int getActualPosition(int position) {
            int actualPosition = position;
            if (position >= getActualItemCount()) {
                actualPosition = position % getActualItemCount();
            }
            return actualPosition;
        }

        public int getActualItemViewType(int position) {
            return adapter.getItemViewType(position);
        }

        public long getActualItemId(int position) {
            return adapter.getItemId(position);
        }

        @Override
        public int getItemViewType(int position) {
            return adapter.getItemViewType(getActualPosition(position));
        }

        @Override
        public long getItemId(int position) {
            return adapter.getItemId(getActualPosition(position));
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == cardGalleryView.getActualCurrentIndex()) {
                if (onClickListener != null) onClickListener.onClick(v);
            } else {
                cardGalleryView.alignView(v);
            }
        }
    }

    public interface OnPageChangedListener {
        void OnPageChanged(int oldPosition, int newPosition);
    }

    public void addOnPageChangedListener(OnPageChangedListener listener) {
        if (mOnPageChangedListeners == null) {
            mOnPageChangedListeners = new ArrayList<>();
        }
        mOnPageChangedListeners.add(listener);
    }
}
