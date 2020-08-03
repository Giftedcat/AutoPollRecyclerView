package com.giftedcat.autopollrecyclerview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.ref.WeakReference;

public class AutoPollRecyclerView extends RecyclerView {

    private static final long TIME_AUTO_POLL = 16;
    AutoPollTask autoPollTask;
    /**
     * 表示是否正在自动轮询
     */
    private boolean running;
    /**
     * 表示是否可以自动轮询
     */
    private boolean canRun;

    private int scroll_num;

    public AutoPollRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        scroll_num = 2;
        autoPollTask = new AutoPollTask(this);
        start();
    }

    private class AutoPollTask implements Runnable {
        private final WeakReference<AutoPollRecyclerView> mReference;

        /**
         * 使用弱引用持有外部类引用->防止内存泄漏
         */
        public AutoPollTask(AutoPollRecyclerView reference) {
            this.mReference = new WeakReference<AutoPollRecyclerView>(reference);
        }

        @Override
        public void run() {
            AutoPollRecyclerView recyclerView = mReference.get();
            if (recyclerView != null && recyclerView.running && recyclerView.canRun) {
                recyclerView.scrollBy(scroll_num, scroll_num);
                recyclerView.postDelayed(recyclerView.autoPollTask, TIME_AUTO_POLL);
            }
        }
    }

    /**
     * 设置滚动页数
     * */
    public void setScrollNum(int scroll_num){
        this.scroll_num = scroll_num;
    }

    /**
     * 开启:如果正在运行,先停止->再开启
     */
    public void start() {
        if (running)
            stop();
        canRun = true;
        running = true;
        postDelayed(autoPollTask, TIME_AUTO_POLL);
    }

    public void stop() {
        running = false;
        removeCallbacks(autoPollTask);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (running)
                    stop();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_OUTSIDE:
                if (canRun)
                    start();
                break;
        }
        return super.onTouchEvent(e);
    }

}
