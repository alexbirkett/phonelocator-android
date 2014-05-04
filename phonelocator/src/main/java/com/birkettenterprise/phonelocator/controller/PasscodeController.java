/**
 *
 *  Copyright 2011-2014 Birkett Enterprise Ltd
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */


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
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.text.method.DigitsKeyListener;
import android.text.method.PasswordTransformationMethod;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

public class PasscodeController extends Controller implements OnDismissListener {

	private Dialog dialog;
	private EditText passcodeEditText;
	private boolean correctPasswordEntered;
	private boolean okClicked;
    private Context context;
	
	public PasscodeController(Activity context) {
		super();
		this.context = context;
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
		dialog.show();
		okClicked = false;
		correctPasswordEntered = false;
		passcodeEditText.setText("");
	}
	
	@Override
	protected void onPause() {
		hideDialogIfExists();
	}

	private Activity getActivity() {
		return (Activity) context;
	}

	private void buildDialogIfNotAlreadyExists() {
		if (dialog == null) {
			buildDialog();
		}
	}
	
	private void hideDialogIfExists() {
		if (dialog != null) {
			dialog.hide();
		}
	}
	
	private boolean isPasscodeNonNummAndEnabled() {
		return SettingsHelper.isPasscodeEnabled() && !StringUtil.isNullOrWhiteSpace(getPasscode());
	}

	private String getPasscode() {
		return SettingsHelper.getPasscode();
	}
	
	private void buildDialog() {

		AlertDialog.Builder builder = new AlertDialog.Builder(context)
				.setTitle(context.getString(R.string.passcode_dialog_title))
				.setIcon(R.drawable.icon)
				.setPositiveButton(
                        context.getString(R.string.passcode_dialog_ok),
                        mOkClickListener)
				.setNegativeButton(
                        context.getString(R.string.passcode_dialog_cancel),
                        mCancelClickListener);

		passcodeEditText = new EditText(context);
		passcodeEditText.setTransformationMethod(new PasswordTransformationMethod());
		passcodeEditText.setKeyListener(new DigitsKeyListener());

		builder.setView(passcodeEditText);

		dialog = builder.create();
		dialog.setOnDismissListener(this);
		dialog.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		if (okClicked) {
			if (!correctPasswordEntered) {
				showIncorrectToast();
				showDialog();
			}
		} else {
			getActivity().finish();
		}

	}

	private void showIncorrectToast() {
		Toast toast = Toast.makeText(context, R.string.incorrect_passcode, Toast.LENGTH_LONG);
		toast.show();
	}
	
	private OnClickListener mOkClickListener = new OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			String passcode = getPasscode();
			correctPasswordEntered = passcodeEditText.getText().toString().equals(passcode);
			okClicked = true;
		}

	};

	private OnClickListener mCancelClickListener = new OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
		
		}

	};

}
