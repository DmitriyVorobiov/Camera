package com.example.camera;

import java.io.File;

import android.app.Activity;
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
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements OnClickListener,
		PickNameDialogFragment.OnCompleteListener {

	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1;
	private static final String TAG = "PickNameDialog";
	private static final String PREPARED_FILE_NAME = "fileName";
	private final String TEMP_FILE_NAME = "temp";
	private String currentPath;
	private File tempPhotoFile;
	private Button photoMakeButton;
	private ImageView photoFrame;
	private Bitmap photo;
	private PickNameDialogFragment dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera);
		initViewComponents();

		if (savedInstanceState != null) {
			currentPath = savedInstanceState.getString(PREPARED_FILE_NAME);
			dialog = (PickNameDialogFragment) getFragmentManager()
					.findFragmentByTag(TAG);
			if (dialog != null)
				dialog.setListener((PickNameDialogFragment.OnCompleteListener) this);
		}

		tempPhotoFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),TEMP_FILE_NAME);

		photo = getTakenPhoto();

		if (photo != null)
			showPhoto();

	}

	private Bitmap getTakenPhoto() {
		if (currentPath != null) {
			File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),currentPath);
			return decodeScaledImage(file);
		}
		return null;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	private void showPhoto() {
		photoFrame.setImageBitmap(photo);
		photoFrame.setVisibility(ImageView.VISIBLE);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
			if (resultCode == Activity.RESULT_OK) {
				photo = decodeScaledImage(tempPhotoFile);
				currentPath = TEMP_FILE_NAME;
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
			} else {
				dialog = new PickNameDialogFragment();
				dialog.setListener((PickNameDialogFragment.OnCompleteListener) this);
				dialog.show(getFragmentManager(), TAG);
			}
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

	@Override
	public void onDialogClick(String newFileName) {
		File preparedFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),newFileName + ".jpg");
		tempPhotoFile.renameTo(preparedFile);
		currentPath = newFileName.concat(".jpg");
		showToast(getString(R.string.saved));
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

	private void initViewComponents() {
		photoMakeButton = (Button) findViewById(R.id.photoButton);
		photoMakeButton.setOnClickListener(this);
		photoFrame = (ImageView) findViewById(R.id.photoFrame);
		photoFrame.setVisibility(ImageView.INVISIBLE);

	}

	private void showToast(String message) {
		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putString(PREPARED_FILE_NAME, currentPath);
	}

}
