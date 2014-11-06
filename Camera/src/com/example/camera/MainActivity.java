package com.example.camera;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements OnClickListener,
		PickNameDialogFragment.OnCompleteListener {

	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1;
	private final String TEMP_FILE_NAME = "temp";
	private File tempPhotoFile;
	private Uri imageUri;
	private Button photoMakeButton;
	private ImageView photoFrame;
	private Bitmap photo;
	private String fileName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera);
		initComponents();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	private Bitmap decodeScaledImage(File file) {
		DisplayMetrics metrics = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		int targetW = metrics.widthPixels;
		int targetH = metrics.heightPixels;
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(file.getAbsolutePath(), bmOptions);
		int photoW = bmOptions.outWidth;
		int photoH = bmOptions.outHeight;
		int scaleFactor = Math.min(photoW / targetW, photoH / targetH);
		bmOptions.inJustDecodeBounds = false;
		bmOptions.inSampleSize = scaleFactor;
		bmOptions.inPurgeable = true;
		return BitmapFactory.decodeFile(file.getAbsolutePath(), bmOptions);
	}

	private void initComponents() {
		photoMakeButton = (Button) findViewById(R.id.photoButton);
		photoMakeButton.setOnClickListener(this);
		photoFrame = (ImageView) findViewById(R.id.photoFrame);
		tempPhotoFile = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				TEMP_FILE_NAME);
		imageUri = Uri.fromFile(tempPhotoFile);
		photoFrame.setVisibility(ImageView.INVISIBLE);
		photo = decodeScaledImage(tempPhotoFile);
		if (photo != null) {
			showPhoto();
		}
	}

	private void showPhoto() {
		photo = decodeScaledImage(tempPhotoFile);
		photoFrame.setImageBitmap(photo);
		photoFrame.setVisibility(ImageView.VISIBLE);
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
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case (R.id.action_save):
			if (photo == null) {
				showToast(getString(R.string.nophoto));
			} else
				// showSaveDialog();
				// Exception here
				new PickNameDialogFragment(this).show(
						getFragmentManager(), "tag");
		PickNameDialogFragment = new PickNameDialogFragment(this);
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
		intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
	}

	private void showSaveDialog() {
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle(R.string.pick_name_dialog);
		final EditText fileNameEditText = new EditText(this);
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

	private void showToast(String message) {
		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
	}

	@Override
	public void onDialogClick(String newFileName) {
		fileName = newFileName;
		File preparedFile = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				fileName + ".jpg");
		tempPhotoFile.renameTo(preparedFile);
		showToast(getString(R.string.saved));
	}
}
