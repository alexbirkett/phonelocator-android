package com.birkettenterprise.phonelocator.controller;

import com.birkett.controllers.Controller;
import com.birkettenterprise.phonelocator.R;
import com.birkettenterprise.phonelocator.settings.SettingsHelper;
import com.birkettenterprise.phonelocator.utility.StringUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.preference.PreferenceManager;
import android.text.method.DigitsKeyListener;
import android.text.method.PasswordTransformationMethod;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

public class PasscodeController extends Controller implements OnDismissListener {

	private Dialog mDialog;
	private EditText mPasscodeEditText;
	private boolean mCorrectPasswordEntered;
	private boolean mOkClicked;
    private Context mContext;
	
	public PasscodeController(Activity context) {
		super();
		mContext = context;
	}

	@Override
	protected void onResume() {
		
		boolean passcodeEnabled = isPasscodeNonNummAndEnabled();
		if (passcodeEnabled) {
			buildDialogIfNotAlreadyExists();
			showDialog();
		}

	}

	private void showDialog() {
		mDialog.show();
		mOkClicked = false;
		mCorrectPasswordEntered = false;
		mPasscodeEditText.setText("");
	}
	
	@Override
	protected void onPause() {
		hideDialogIfExists();
	}

	private Activity getActivity() {
		return (Activity) mContext;
	}

	private void buildDialogIfNotAlreadyExists() {
		if (mDialog == null) {
			buildDialog();
		}
	}
	
	private void hideDialogIfExists() {
		if (mDialog != null) {
			mDialog.hide();
		}
	}
	
	private boolean isPasscodeNonNummAndEnabled() {
		return SettingsHelper.isPasscodeEnabled() && !StringUtil.isNullOrWhiteSpace(getPasscode());
	}

	private String getPasscode() {
		return SettingsHelper.getPasscode();
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

		mPasscodeEditText = new EditText(mContext);
		mPasscodeEditText.setTransformationMethod(new PasswordTransformationMethod());
		mPasscodeEditText.setKeyListener(new DigitsKeyListener());

		builder.setView(mPasscodeEditText);

		mDialog = builder.create();
		mDialog.setOnDismissListener(this);
		mDialog.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		if (mOkClicked) {
			if (!mCorrectPasswordEntered) {
				showIncorrectToast();
				showDialog();
			}
		} else {
			getActivity().finish();
		}

	}

	private void showIncorrectToast() {
		Toast toast = Toast.makeText(mContext, R.string.incorrect_passcode, Toast.LENGTH_LONG);
		toast.show();
	}
	
	private OnClickListener mOkClickListener = new OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			String passcode = getPasscode();
			mCorrectPasswordEntered = mPasscodeEditText.getText().toString().equals(passcode);
			mOkClicked = true;
		}

	};

	private OnClickListener mCancelClickListener = new OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
		
		}

	};

}
