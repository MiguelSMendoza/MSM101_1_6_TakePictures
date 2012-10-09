package es.netrunners;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class TakePicturesActivity extends Activity {
	private static final String PREFS_NAME = "TAKINGPICTURES";
	ImageView image;
	File file;
	Uri outputFileUri;
	int TAKE_PICTURE = 1;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		image = (ImageView) findViewById(R.id.imageView);
	}

	@Override
	protected void onResume() {
		super.onResume();
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}

	public void takePicture(View v) {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		file = getFileName("jpg");
		outputFileUri = Uri.fromFile(file);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
		SharedPreferences settings = getSharedPreferences(PREFS_NAME,
				MODE_PRIVATE);
		Editor editor = settings.edit();
		editor.putString("FILE", file.getAbsolutePath());
		editor.commit(); // IMPORTANTE
		startActivityForResult(intent, TAKE_PICTURE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == TAKE_PICTURE) {
			SharedPreferences settings = getSharedPreferences(PREFS_NAME,
					MODE_PRIVATE);
			final String uri = settings.getString("FILE", "Default Value");
			MediaScannerConnectionClient mediaScannerClient = new MediaScannerConnectionClient() {
				private MediaScannerConnection msc = null;
				{
					msc = new MediaScannerConnection(getApplicationContext(),
							this);
					msc.connect();
				}

				public void onMediaScannerConnected() {
					msc.scanFile(uri, null);
				}

				public void onScanCompleted(String path, Uri uri) {
					msc.disconnect();
				}
			};

			Bitmap bMap = BitmapFactory.decodeFile(uri);
			image.setImageBitmap(bMap);
		}
	}

	private File getFileName(String ext) {
		File mFile = null;
		File path = new File(Environment.getExternalStorageDirectory()
				.getPath());
		try {
			mFile = File.createTempFile("temp", "." + ext, path);
		} catch (IOException e) {
			Toast.makeText(getApplicationContext(), e.getMessage(),
					Toast.LENGTH_LONG).show();
		}
		return mFile;
	}
}