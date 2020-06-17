/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package abi38_0_0.host.exp.exponent.modules.api.components.datetimepicker;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnClickListener;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.text.format.DateFormat;

import java.util.Locale;

@SuppressWarnings("ValidFragment")
public class RNTimePickerDialogFragment extends DialogFragment {
  private TimePickerDialog instance;

  @Nullable
  private OnTimeSetListener mOnTimeSetListener;
  @Nullable
  private OnDismissListener mOnDismissListener;
  @Nullable
  private static OnClickListener mOnNeutralButtonActionListener;

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    final Bundle args = getArguments();
    instance = createDialog(args, getActivity(), mOnTimeSetListener);
    return instance;
  }

  public void update(Bundle args) {
    final RNDate date = new RNDate(args);
    instance.updateTime(date.hour(), date.minute());
  }

  static TimePickerDialog getDialog(
          Bundle args,
          Context activityContext,
          @Nullable OnTimeSetListener onTimeSetListener) {

    final RNDate date = new RNDate(args);
    final int hour = date.hour();
    final int minute = date.minute();
    boolean is24hour = DateFormat.is24HourFormat(activityContext);

    RNTimePickerDisplay display = RNTimePickerDisplay.DEFAULT;
    if (args != null && args.getString(RNConstants.ARG_DISPLAY, null) != null) {
      display = RNTimePickerDisplay.valueOf(args.getString(RNConstants.ARG_DISPLAY).toUpperCase(Locale.US));
    }

    if (args != null) {
      is24hour = args.getBoolean(RNConstants.ARG_IS24HOUR, DateFormat.is24HourFormat(activityContext));
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      switch (display) {
        case CLOCK:
        case SPINNER:
          String resourceName = display == RNTimePickerDisplay.CLOCK
                  ? "ClockTimePickerDialog"
                  : "SpinnerTimePickerDialog";
          return new RNDismissableTimePickerDialog(
                  activityContext,
                  activityContext.getResources().getIdentifier(
                          resourceName,
                          "style",
                          activityContext.getPackageName()
                  ),
                  onTimeSetListener,
                  hour,
                  minute,
                  is24hour,
                  display
          );
      }
    }
    return new RNDismissableTimePickerDialog(
            activityContext,
            onTimeSetListener,
            hour,
            minute,
            is24hour,
            display
    );
  }

  static TimePickerDialog createDialog(
          Bundle args, Context activityContext,
          @Nullable OnTimeSetListener onTimeSetListener) {

    TimePickerDialog dialog = getDialog(args, activityContext, onTimeSetListener);

    if (args != null && args.containsKey(RNConstants.ARG_NEUTRAL_BUTTON_LABEL)) {
      dialog.setButton(DialogInterface.BUTTON_NEUTRAL, args.getString(RNConstants.ARG_NEUTRAL_BUTTON_LABEL), mOnNeutralButtonActionListener);
    }
    return dialog;
  }

  @Override
  public void onDismiss(DialogInterface dialog) {
    super.onDismiss(dialog);
    if (mOnDismissListener != null) {
      mOnDismissListener.onDismiss(dialog);
    }
  }

  public void setOnDismissListener(@Nullable OnDismissListener onDismissListener) {
    mOnDismissListener = onDismissListener;
  }

  public void setOnTimeSetListener(@Nullable OnTimeSetListener onTimeSetListener) {
    mOnTimeSetListener = onTimeSetListener;
  }

  /*package*/ void setOnNeutralButtonActionListener(@Nullable OnClickListener onNeutralButtonActionListener) {
    mOnNeutralButtonActionListener = onNeutralButtonActionListener;
  }
}
