package com.example.camera;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class CameraFragment extends Fragment implements OnClickListener {

	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1;
	private final String TEMP_FILE_NAME = "temp";
	private File tempPhotoFile;
	private Button photoMakeButton;
	private ImageView photoFrame;
	private Bitmap photo;
	private String fileName;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setRetainInstance(true);
		setHasOptionsMenu(true);

		tempPhotoFile = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				TEMP_FILE_NAME);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater
				.inflate(R.layout.fragment_camera, container, false);

		initViewComponents(view);

		return view;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
			if (resultCode == Activity.RESULT_OK) {
				showPhoto();
			} else if (resultCode == Activity.RESULT_CANCELED) {
				showToast(getString(R.string.cancelled));
			}
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
		super.onCreateOptionsMenu(menu, menuInflater);
		menuInflater.inflate(R.menu.main, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case (R.id.action_save):
			if (photo == null) {
				showToast(getString(R.string.nophoto));
			} else
				showSaveDialog();
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onClick(View v) {
		takePhoto();
	}

	protected void takePhoto() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempPhotoFile));
		startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
	}

	private void showSaveDialog() {
		AlertDialog.Builder builder = new Builder(getActivity());
		builder.setTitle(R.string.pick_name_dialog);
		final EditText fileNameEditText = new EditText(getActivity());
		builder.setView(fileNameEditText);
		builder.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						fileName = fileNameEditText.getText().toString();
						File preparedFile = new File(
								Environment
										.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
								fileName + ".jpg");
						tempPhotoFile.renameTo(preparedFile);
						showToast(getString(R.string.saved));
						dialog.cancel();
					}
				});
		builder.create().show();
	}

	private void decodeScaledImage() {
		DisplayMetrics metrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay()
				.getMetrics(metrics);
		// Get the dimensions of the View
		int targetW = metrics.widthPixels; 
		int targetH = metrics.heightPixels;
		// Get the dimensions of the bitmap
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(tempPhotoFile.getAbsolutePath(), bmOptions);
		int photoW = bmOptions.outWidth;
		int photoH = bmOptions.outHeight;
		// Determine how much to scale down the image
		int scaleFactor = Math.min(photoW / targetW, photoH / targetH);
		// Decode the image file into a Bitmap sized to fill the View
		bmOptions.inJustDecodeBounds = false;
		bmOptions.inSampleSize = scaleFactor;
		bmOptions.inPurgeable = true;
		photo = BitmapFactory.decodeFile(tempPhotoFile.getAbsolutePath(),
				bmOptions);
	}

	private void initViewComponents(View view) {
		photoMakeButton = (Button) view.findViewById(R.id.photoButton);
		photoMakeButton.setOnClickListener(this);
		photoFrame = (ImageView) view.findViewById(R.id.photoFrame);
		photoFrame.setVisibility(ImageView.INVISIBLE);
		if (photo != null) {
			showPhoto();
		}
	}

	private void showToast(String message) {
		Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
	}

	private void showPhoto() {
		decodeScaledImage();
		photoFrame.setImageBitmap(photo);
		photoFrame.setVisibility(ImageView.VISIBLE);
	}
}
