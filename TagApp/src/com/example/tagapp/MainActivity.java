package com.example.tagapp;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.example.tagbincam.R;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.provider.MediaStore;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity implements View.OnClickListener{
	
	Button ib, b, misub;
	Uri imageUri, oldUri;
	private ProgressDialog pDialog;
	public static final String PREFS_NAME = "tagAppfile";
	ImageView iv;
	ContentValues values;
	int hvalue;
	EditText mitext;
	final static int camData = 0;
	final static int RESULT_LOAD_IMAGE = 1;
	Intent i, id;
	Bitmap bmp;
	String encoded,tag_id, imageurl, stored_id;
	
	public String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        //this.managedQuery(contentUri, proj, null, null, null);
        Cursor cursor = getContentResolver().query(contentUri,proj, null, null, null);
        cursor.moveToFirst();
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        String news = cursor.getString(column_index);
        cursor.close();
        return news;
    }


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		/*
		PowerManager powerManager = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
		WakeLock wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Lock");
		wakeLock.acquire();
		*/
		initialize();
		InputStream is = getResources().openRawResource(R.drawable.ic_launcher);
		bmp = BitmapFactory.decodeStream(is);
	}
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		stored_id = settings.getString("ID", "None");
		imageUri = Uri.parse(settings.getString("uri", "Not Defined"));
		Log.d(Constants.LOG, "Resuming "+ imageUri);
	}


	private void initialize() {
		iv = (ImageView) findViewById (R.id.iView1);
		ib = (Button) findViewById (R.id.iButton1);
		b = (Button) findViewById (R.id.bttn1);
		misub = (Button) findViewById (R.id.bttnMI1);
		mitext = (EditText) findViewById(R.id.MItext1);
		b.setOnClickListener(this);
		ib.setOnClickListener(this);
		misub.setOnClickListener(this);
	}
	public static Camera getCameraInstance(){
	    Camera c = null;
	    try {
	        c = Camera.open(); // attempt to get a Camera instance
	    }
	    catch (Exception e){
	        // Camera is not available (in use or does not exist)
	    }
	    return c; // returns null if camera is unavailable
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.iButton1:
			long timeStamp = System.currentTimeMillis();
			values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "New Picture"+timeStamp);
            values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
            imageUri = getContentResolver().insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            Log.d(Constants.LOG, "ImageUri before " + imageUri);
            SharedPreferences uri = getSharedPreferences(
					PREFS_NAME , 0);
			SharedPreferences.Editor edite = uri.edit();
			edite.putString("uri", imageUri.toString());
			edite.commit();

			i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
			i.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
			startActivityForResult(i, camData);
			break;
		case R.id.bttn1:
			id = new Intent(
					Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);					 
					startActivityForResult(id, RESULT_LOAD_IMAGE);			
			break;
		case R.id.bttnMI1:
			tag_id = mitext.getText().toString();
			
			if (tag_id.length() >= 10) {
				new DoSampleHttpPostRequest().execute();
				mitext.setVisibility(View.INVISIBLE);
				SharedPreferences settings = getSharedPreferences(
						PREFS_NAME, 0);
				SharedPreferences.Editor editor = settings.edit();
				editor.putString("ID", tag_id);
				editor.commit();
			}
			else if (tag_id.length() <10 && stored_id != "None")
			{
				tag_id = stored_id;
				new DoSampleHttpPostRequest().execute();
				mitext.setVisibility(View.INVISIBLE);
				misub.setVisibility(View.INVISIBLE);
			}
			break;
			
		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == camData && resultCode == RESULT_OK) {
			Log.d(Constants.LOG, "Uri of Image " + imageUri);
			//Bundle extras = data.getExtras();
			/*
			if (data.hasExtra(MediaStore.EXTRA_OUTPUT))
			{
				Log.d(Constants.LOG, "OUTPUT Received");
			}
			*/
			//bmp = (Bitmap) extras.get("data");
			if(stored_id != "None")
			{
				mitext.setHint(stored_id);				
			}
			misub.setVisibility(View.VISIBLE);
			mitext.setVisibility(View.VISIBLE);
			
			//try {
			if(null == imageUri)
			{
				SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
				imageUri = Uri.parse(settings.getString("uri", "Not Defined"));
				Log.d(Constants.LOG, "IF wala "+ imageUri);
			}
				imageurl = getRealPathFromURI(imageUri);
				BitmapFactory.Options opt = new BitmapFactory.Options();
	            opt.inSampleSize = 4;
	            bmp = BitmapFactory.decodeFile(imageurl, opt);
				//bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);				
				//iv.setImageBitmap(bmp);
	        /*    
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			*/
			ByteArrayOutputStream baos = new ByteArrayOutputStream();  
			bmp.compress(Bitmap.CompressFormat.PNG, 100, baos); //bmp is the bitmap object
			hvalue = bmp.getHeight();
			byte[] b = baos.toByteArray();
			encoded = Base64.encodeToString(b, Base64.DEFAULT);
			iv.setImageBitmap(bmp);
		}
		else if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK) {
			Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
 
            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
 
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            BitmapFactory.Options opt = new BitmapFactory.Options();
            opt.inSampleSize = 4;
            bmp = BitmapFactory.decodeFile(picturePath, opt);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();  
            bmp.compress(Bitmap.CompressFormat.PNG, 100, baos); //bmp is the bitmap object   
            byte[] b = baos.toByteArray();
            encoded = Base64.encodeToString(b, Base64.DEFAULT);
            if(stored_id != "None")
			{
				mitext.setHint(stored_id);				
			}
            misub.setVisibility(View.VISIBLE);
			mitext.setVisibility(View.VISIBLE);
			iv.setImageBitmap(bmp);
		}
		
	}
	
	class DoSampleHttpPostRequest extends AsyncTask<Void, Void, String> {
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(MainActivity.this);
			pDialog.setMessage("Uploading Picture...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

	    @Override
	    protected String doInBackground(Void... params) {
	        BufferedReader in = null;
	        String baseUrl = "http://23.23.209.78/phpHandler/tagbinAPI.php";
	        String d = String.valueOf(hvalue);
	        String tagid = tag_id;//mitext.getText().toString();
	        try {
	            HttpClient httpClient = new DefaultHttpClient();
	            HttpPost request = new HttpPost(baseUrl);

	            List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
	            postParameters.add(new BasicNameValuePair("_TAG_ID", tagid));
	            postParameters.add(new BasicNameValuePair("_ACTION", "_PIC_POST"));
	            //postParameters.add(new BasicNameValuePair("uploadType", "_PIC_UPLOAD"));
	            postParameters.add(new BasicNameValuePair("_IMG_DATA", encoded));
	            postParameters.add(new BasicNameValuePair("_HEIGHT", d));
	            postParameters.add(new BasicNameValuePair("_CLIENT_ID", "4"));
	            UrlEncodedFormEntity form = new UrlEncodedFormEntity(postParameters);
	            request.setEntity(form);
	            //Toast.makeText(MainActivity.this, encoded, Toast.LENGTH_LONG).show();

	            Log.d(Constants.LOG, "making POST request to: " + baseUrl);

	            HttpResponse response = httpClient.execute(request);

	            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
	            StringBuffer sb = new StringBuffer("");
	            String line = "";
	            String NL = System.getProperty("line.separator");
	            while ((line = in.readLine()) != null) {
	                sb.append(line + NL);
	            }
	            in.close();

	            return sb.toString();
	        } catch (Exception e) {
	            return "Exception happened: " + e.getMessage();
	        } finally {
	            if (in != null) {
	                try {
	                    in.close();
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }
	            }
	        }
	    }

	    @Override
	    protected void onPostExecute(String result) {
	    	pDialog.dismiss();
	    	//Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
	    	if(result.contains("Successfully Posted")){
	    		Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
	    		// this refers to a TextView defined as a private field in the parent Activity
	    		//textView.setText(result);
	    		mitext.setText("");
	    		misub.setVisibility(View.INVISIBLE);
				mitext.setVisibility(View.INVISIBLE);
	    	}
	    	else if(result.contains("Error")) {
	    		Toast.makeText(getApplicationContext(), "You May not have given permission to the Tagbin App", Toast.LENGTH_LONG).show();
	    		mitext.setText("");
	    		misub.setVisibility(View.INVISIBLE);
				mitext.setVisibility(View.INVISIBLE);
	    	}
	    	else {
	    		Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
	    		//Toast.makeText(getApplicationContext(), "There's some error!! Check internet connectivity or Moodi no.", Toast.LENGTH_LONG).show();
	    		mitext.setVisibility(View.VISIBLE);
	    		misub.setVisibility(View.VISIBLE);
	    	}
	    		
	    }

	}
	
	

}
