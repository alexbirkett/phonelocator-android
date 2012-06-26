package com.birkettenterprise.phonelocator.controller;

import com.birkettenterprise.phonelocator.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.text.method.DigitsKeyListener;
import android.text.method.PasswordTransformationMethod;
import android.view.WindowManager;
import android.widget.EditText;
import no.birkettconsulting.controllers.Controller;

public class PasscodeController extends Controller implements OnDismissListener {

	private Dialog mDialog;

	public PasscodeController(Activity context) {
		super(context);
	}

	@Override
	protected void onResume() {
		buildDialogIfRequired();
		mDialog.show();
	}

	@Override
	protected void onPause() {
		mDialog.hide();
	}

	private Activity getActivity() {
		return (Activity) mContext;
	}

	private void buildDialogIfRequired() {
		if (mDialog == null) {
			buildDialog();
		}
	}

	private void buildDialog() {

		AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
				.setTitle(mContext.getString(R.string.passcode_dialog_title))
				.setIcon(R.drawable.icon)
				.setPositiveButton(
						mContext.getString(R.string.passcode_dialog_ok),
						mOkClickListener)
				.setNegativeButton(
						mContext.getString(R.string.passcode_dialog_cancel),
						mCancelClickListener);

		EditText input = new EditText(mContext);
		input.setTransformationMethod(new PasswordTransformationMethod());
		input.setKeyListener(new DigitsKeyListener());

		builder.setView(input);

		mDialog = builder.create();
		mDialog.setOnDismissListener(this);
		mDialog.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
	}

	@Override
	public void onDismiss(DialogInterface dialog) {

	}

	private OnClickListener mOkClickListener = new OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {

		}

	};

	private OnClickListener mCancelClickListener = new OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			getActivity().finish();
		}

	};

}
