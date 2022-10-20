package com.treulieb.worktimetool;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ViewManager {

    private Map<View, Consumer<View>> onChangeListener;
    private Map<View, Runnable> onShowListener;
    private Map<View, View> parents;

    private View currentView;

    public ViewManager() {
        this.onChangeListener = new HashMap<>();
        this.onShowListener = new HashMap<>();
        this.parents = new HashMap<>();
    }

    public Map<View, Consumer<View>> getOnChangeListener() {
        return onChangeListener;
    }

    public void addView(View view, Runnable onShowListener, Consumer<View> onChangeListener) {
        addView(view, null, onShowListener, onChangeListener);
    }

    public void addView(@NonNull View view, @Nullable View parent, Runnable onShowListener, Consumer<View> onChangeListener) {
        this.onChangeListener.put(view, onChangeListener == null ? x -> {} : onChangeListener);
        this.onShowListener.put(view, onShowListener == null ? () -> {} : onShowListener);
        this.parents.put(view, parent);
    }

    public void openView(View toShow) {
        if(currentView == null){
            this.onShowListener.get(toShow).run();
            toShow.setVisibility(View.VISIBLE);
            currentView = toShow;
            return;
        }

        this.onShowListener.get(toShow).run();
        this.onChangeListener.get(currentView).accept(toShow);

        currentView.setVisibility(View.GONE);
        toShow.setVisibility(View.VISIBLE);

        currentView = toShow;
    }

    public View getCurrentParent() {
        if(this.currentView == null)
            return null;

        return this.parents.get(this.currentView);
    }

    public View getParent(View view) {
        return this.parents.get(view);
    }

    public View getCurrentView() {
        return currentView;
    }
}
