// Generated code from Butter Knife. Do not modify!
package com.easycore.stromecek.views.activities;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.easycore.stromecek.R;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import java.lang.IllegalStateException;
import java.lang.Override;

public class FullscreenPlayerActivity_ViewBinding<T extends FullscreenPlayerActivity> implements Unbinder {
  protected T target;

  @UiThread
  public FullscreenPlayerActivity_ViewBinding(T target, View source) {
    this.target = target;

    target.playerView = Utils.findRequiredViewAsType(source, R.id.player_view, "field 'playerView'", SimpleExoPlayerView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    T target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");

    target.playerView = null;

    this.target = null;
  }
}
