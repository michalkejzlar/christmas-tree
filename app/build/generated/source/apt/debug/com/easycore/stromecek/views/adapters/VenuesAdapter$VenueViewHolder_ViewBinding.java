// Generated code from Butter Knife. Do not modify!
package com.easycore.stromecek.views.adapters;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.easycore.stromecek.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class VenuesAdapter$VenueViewHolder_ViewBinding<T extends VenuesAdapter.VenueViewHolder> implements Unbinder {
  protected T target;

  @UiThread
  public VenuesAdapter$VenueViewHolder_ViewBinding(T target, View source) {
    this.target = target;

    target.container = Utils.findRequiredViewAsType(source, R.id.venue_container, "field 'container'", ViewGroup.class);
    target.txvName = Utils.findRequiredViewAsType(source, R.id.venue_txv_name, "field 'txvName'", TextView.class);
    target.imvPicture = Utils.findRequiredViewAsType(source, R.id.venue_imv_picture, "field 'imvPicture'", ImageView.class);
    target.txvAddress = Utils.findRequiredViewAsType(source, R.id.venue_txv_address, "field 'txvAddress'", TextView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    T target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");

    target.container = null;
    target.txvName = null;
    target.imvPicture = null;
    target.txvAddress = null;

    this.target = null;
  }
}
