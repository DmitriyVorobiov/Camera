package com.example.camera;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

public class MainActivity extends ActionBarActivity {

	CameraFragment cameraFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if (savedInstanceState == null) {
			cameraFragment = new CameraFragment();
			getFragmentManager().beginTransaction()
					.add(R.id.fragmentContainer, cameraFragment).commit();
			return;			
		}
	}

}
