// Generated by view binder compiler. Do not edit!
package org.hyperskill.photoeditor.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.google.android.material.slider.Slider;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;
import org.hyperskill.photoeditor.R;

public final class ActivityMainBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final Button btnGallery;

  @NonNull
  public final Button btnSave;

  @NonNull
  public final ImageView ivPhoto;

  @NonNull
  public final Slider slBrightness;

  @NonNull
  public final Slider slContrast;

  @NonNull
  public final Slider slGamma;

  @NonNull
  public final Slider slSaturation;

  private ActivityMainBinding(@NonNull ConstraintLayout rootView, @NonNull Button btnGallery,
      @NonNull Button btnSave, @NonNull ImageView ivPhoto, @NonNull Slider slBrightness,
      @NonNull Slider slContrast, @NonNull Slider slGamma, @NonNull Slider slSaturation) {
    this.rootView = rootView;
    this.btnGallery = btnGallery;
    this.btnSave = btnSave;
    this.ivPhoto = ivPhoto;
    this.slBrightness = slBrightness;
    this.slContrast = slContrast;
    this.slGamma = slGamma;
    this.slSaturation = slSaturation;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityMainBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityMainBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_main, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityMainBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.btnGallery;
      Button btnGallery = ViewBindings.findChildViewById(rootView, id);
      if (btnGallery == null) {
        break missingId;
      }

      id = R.id.btnSave;
      Button btnSave = ViewBindings.findChildViewById(rootView, id);
      if (btnSave == null) {
        break missingId;
      }

      id = R.id.ivPhoto;
      ImageView ivPhoto = ViewBindings.findChildViewById(rootView, id);
      if (ivPhoto == null) {
        break missingId;
      }

      id = R.id.slBrightness;
      Slider slBrightness = ViewBindings.findChildViewById(rootView, id);
      if (slBrightness == null) {
        break missingId;
      }

      id = R.id.slContrast;
      Slider slContrast = ViewBindings.findChildViewById(rootView, id);
      if (slContrast == null) {
        break missingId;
      }

      id = R.id.slGamma;
      Slider slGamma = ViewBindings.findChildViewById(rootView, id);
      if (slGamma == null) {
        break missingId;
      }

      id = R.id.slSaturation;
      Slider slSaturation = ViewBindings.findChildViewById(rootView, id);
      if (slSaturation == null) {
        break missingId;
      }

      return new ActivityMainBinding((ConstraintLayout) rootView, btnGallery, btnSave, ivPhoto,
          slBrightness, slContrast, slGamma, slSaturation);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
