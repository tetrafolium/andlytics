
package com.github.andlyticsproject.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;
import com.github.andlyticsproject.R;

public class CrashDialog extends Dialog {

  public CrashDialog(final Context context, final int theme) {
    super(context, theme);
  }

  public CrashDialog(final Context context) { super(context); }

  /**
   * Helper class for creating a custom dialog
   */
  public static class CrashDialogBuilder {

    private Context context;
    private String title;
    private String message;
    private String positiveViewText;
    private String negativeViewText;

    private DialogInterface.OnClickListener positiveViewClickListener,
        negativeViewClickListener;
    private int messageStringId;

    public CrashDialogBuilder(final Context context) { this.context = context; }

    /**
     * Set the Dialog message from String
     *
     * @param title
     * @return
     */
    public CrashDialogBuilder setMessage(final String message) {
      this.message = message;
      return this;
    }

    /**
     * Set the Dialog message from resource
     *
     * @param title
     * @return
     */
    public CrashDialogBuilder setMessage(final int message) {
      this.messageStringId = message;
      return this;
    }

    /**
     * Set the Dialog title from resource
     *
     * @param title
     * @return
     */
    public CrashDialogBuilder setTitle(final int title) {
      this.title = (String)context.getText(title);
      return this;
    }

    /**
     * Set the Dialog title from String
     *
     * @param title
     * @return
     */
    public CrashDialogBuilder setTitle(final String title) {
      this.title = title;
      return this;
    }

    /**
     * Set the positive button resource and it's listener
     *
     * @param positiveViewText
     * @param listener
     * @return
     */
    public CrashDialogBuilder
    setPositiveButton(final int positiveViewText,
                      final DialogInterface.OnClickListener listener) {
      this.positiveViewText = (String)context.getText(positiveViewText);
      this.positiveViewClickListener = listener;
      return this;
    }

    /**
     * Set the positive button text and it's listener
     *
     * @param positiveViewText
     * @param listener
     * @return
     */
    public CrashDialogBuilder
    setPositiveButton(final String positiveViewText,
                      final DialogInterface.OnClickListener listener) {
      this.positiveViewText = positiveViewText;
      this.positiveViewClickListener = listener;
      return this;
    }

    /**
     * Set the negative button resource and it's listener
     *
     * @param negativeViewText
     * @param listener
     * @return
     */
    public CrashDialogBuilder
    setNegativeButton(final int negativeViewText,
                      final DialogInterface.OnClickListener listener) {
      this.negativeViewText = (String)context.getText(negativeViewText);
      this.negativeViewClickListener = listener;
      return this;
    }

    /**
     * Set the negative button text and it's listener
     *
     * @param negativeViewText
     * @param listener
     * @return
     */
    public CrashDialogBuilder
    setNegativeButton(final String negativeViewText,
                      final DialogInterface.OnClickListener listener) {
      this.negativeViewText = negativeViewText;
      this.negativeViewClickListener = listener;
      return this;
    }

    /**
     * Create the custom dialog
     */
    @SuppressWarnings("deprecation")
    public CrashDialog create() {

      LayoutInflater inflater = (LayoutInflater)context.getSystemService(
          Context.LAYOUT_INFLATER_SERVICE);
      // instantiate the dialog with the custom Theme
      final CrashDialog dialog = new CrashDialog(context, R.style.Dialog);
      View layout = inflater.inflate(R.layout.crash_dialog, null);

      dialog.addContentView(layout, new LayoutParams(LayoutParams.FILL_PARENT,
                                                     LayoutParams.FILL_PARENT));

      // set the dialog title
      ((TextView)layout.findViewById(R.id.crash_dialog_title)).setText(title);
      // set the confirm button
      if (positiveViewText != null) {
        ((TextView)layout.findViewById(R.id.crash_dialog_positive_button_text))
            .setText(positiveViewText);
        if (positiveViewClickListener != null) {
          ((View)layout.findViewById(R.id.crash_dialog_positive_button))
              .setOnClickListener(new View.OnClickListener() {
                public void onClick(final View v) {
                  positiveViewClickListener.onClick(
                      dialog, DialogInterface.BUTTON_POSITIVE);
                }
              });
        }
      } else {
        // if no confirm button just set the visibility to GONE
        layout.findViewById(R.id.crash_dialog_positive_button)
            .setVisibility(View.GONE);
      }
      // set the cancel button
      if (negativeViewText != null) {
        ((TextView)layout.findViewById(R.id.crash_dialog_negative_button_text))
            .setText(negativeViewText);
        if (negativeViewClickListener != null) {
          ((View)layout.findViewById(R.id.crash_dialog_negative_button))
              .setOnClickListener(new View.OnClickListener() {
                public void onClick(final View v) {
                  negativeViewClickListener.onClick(
                      dialog, DialogInterface.BUTTON_NEGATIVE);
                }
              });
        }
      } else {
        // if no confirm button just set the visibility to GONE
        layout.findViewById(R.id.crash_dialog_negative_button)
            .setVisibility(View.GONE);
      }
      // set the content message
      if (message != null) {
        ((TextView)layout.findViewById(R.id.crash_dialog_text))
            .setText(message);
      } else if (messageStringId > 0) {
        ((TextView)layout.findViewById(R.id.crash_dialog_text))
            .setText(messageStringId);
      }

      dialog.setContentView(layout);

      return dialog;
    }
  }
}
