package com.example.camera;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.EditText;

public class PickNameDialogFragment extends DialogFragment {

	private OnCompleteListener pickNameListener;

	public static interface OnCompleteListener {
		public abstract void onDialogClick(String fileName);
	}
//
//	public PickNameDialogFragment(OnCompleteListener listener) {
//		pickNameListener = listener;
//	}

	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new Builder(getActivity());
		builder.setTitle(R.string.pick_name_dialog);
		final EditText fileNameEditText = new EditText(getActivity());
		builder.setView(fileNameEditText);
		builder.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String fileName = fileNameEditText.getText().toString();
						pickNameListener.onDialogClick(fileName);
					}
				});
		// inflate view ?
		return builder.create();
	}
}
