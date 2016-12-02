// Generated code from Butter Knife. Do not modify!
package com.easycore.stromecek.views.activities;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.easycore.stromecek.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class VenueDetailActivity_ViewBinding<T extends VenueDetailActivity> implements Unbinder {
  protected T target;

  @UiThread
  public VenueDetailActivity_ViewBinding(T target, View source) {
    this.target = target;

    target.venuePictureImageView = Utils.findRequiredViewAsType(source, R.id.venuePictureImageView, "field 'venuePictureImageView'", ImageView.class);
    target.toolbar = Utils.findRequiredViewAsType(source, R.id.toolbar, "field 'toolbar'", Toolbar.class);
    target.backdropToolbar = Utils.findRequiredViewAsType(source, R.id.backdrop_toolbar, "field 'backdropToolbar'", CollapsingToolbarLayout.class);
    target.venueNameTextView = Utils.findRequiredViewAsType(source, R.id.venueNameTextView, "field 'venueNameTextView'", TextView.class);
    target.txvDsc = Utils.findRequiredViewAsType(source, R.id.txv_venue_dsc, "field 'txvDsc'", TextView.class);
    target.btnStartVideoStream = Utils.findRequiredViewAsType(source, R.id.btn_start_vide_stream, "field 'btnStartVideoStream'", Button.class);
    target.btnSendDms = Utils.findRequiredViewAsType(source, R.id.btn_send_dms, "field 'btnSendDms'", Button.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    T target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");

    target.venuePictureImageView = null;
    target.toolbar = null;
    target.backdropToolbar = null;
    target.venueNameTextView = null;
    target.txvDsc = null;
    target.btnStartVideoStream = null;
    target.btnSendDms = null;

    this.target = null;
  }
}
