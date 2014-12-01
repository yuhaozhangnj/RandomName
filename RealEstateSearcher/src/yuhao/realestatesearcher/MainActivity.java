package yuhao.realestatesearcher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {
	static InputStream is = null;
	static JSONObject jObj = null;
	static String json = "";
	
	
	public class JSONParser {
		  // constructor
		  public JSONParser() {
		  }
		  public JSONObject getJSONFromUrl() {
		    // Making HTTP request
			  
			EditText boxCity = (EditText) findViewById(R.id.boxCity);
			EditText boxAddress = (EditText) findViewById(R.id.boxAddress);
		    Spinner boxState=(Spinner) findViewById(R.id.boxState);
		    
		    String city = boxCity.getText().toString();
		    String address = boxAddress.getText().toString();
		    String state = boxState.getSelectedItem().toString();
		    city = city.replace(" ", "%20");
		    address = address.replace(" ", "%20");
		    try {
		      // defaultHttpClient
		      DefaultHttpClient httpClient = new DefaultHttpClient();
		      HttpPost httpPost = new HttpPost("http://yuhaoaws-env.elasticbeanstalk.com?streetname=" + address + "&cityname=" + city + "&statename=" + state + "&callback=?");
		      HttpResponse httpResponse = httpClient.execute(httpPost);
		      HttpEntity httpEntity = httpResponse.getEntity();
		      is = httpEntity.getContent();
		    } catch (UnsupportedEncodingException e) {
		      e.printStackTrace();
		    } catch (ClientProtocolException e) {
		      e.printStackTrace();
		    } catch (IOException e) {
		      e.printStackTrace();
		    }
		    try {
		      BufferedReader reader = new BufferedReader(new InputStreamReader(
		          is, "iso-8859-1"), 8);
		      StringBuilder sb = new StringBuilder();
		      String line = null;
		      while ((line = reader.readLine()) != null) {
		        sb.append(line + "\n");
		      }
		      is.close();
		      json = sb.toString();
		      json = json.substring(2, json.length()-1);
		    } catch (Exception e) {
		      Log.e("Buffer Error", "Error converting result " + e.toString());
		    }
		    // try parse the string to a JSON object
		    try {
		      jObj = new JSONObject(json);
		    } catch (JSONException e) {
		      Log.e("JSON Parser", "Error parsing data " + e.toString());
		    }
		    // return JSON String
		    return jObj;
		  }
	}
	
	private class JSONParse extends AsyncTask<String, String, JSONObject> {
	       
	      @Override
	      protected JSONObject doInBackground(String... args) {
	        JSONParser jParser = new JSONParser();
	        // Getting JSON from URL
	        JSONObject json = null;
			json = jParser.getJSONFromUrl();
	        return json;
	      }
	      @Override
	      protected void onPostExecute(JSONObject json) {
	         try {
	            //JSONObject c = json.getJSONArray("citykkk").getJSONObject(0);
	            // Getting JSON Array
	        	if(json.getString("noresult").equals("yes")){
	        		TextView noResultFound = (TextView)findViewById(R.id.break_5);
		        	noResultFound.setText("No exact match found -- Verify that the given address is correct");
	        	}
	        	else{
	        		TextView noResultFound = (TextView)findViewById(R.id.break_5);
		        	noResultFound.setText(" ");
		        	
		        	Intent intent = new Intent(MainActivity.this, ResultActivity.class);
		        	intent.putExtra("streetkkk", json.getString("streetkkk"));
		            intent.putExtra("citykkk", json.getString("citykkk"));
		            intent.putExtra("statekkk", json.getString("statekkk"));
		            intent.putExtra("zipcode", json.getString("zipcode"));
		            intent.putExtra("links", json.getString("links"));
		            intent.putExtra("usecode", json.getString("usecode"));
		            intent.putExtra("lastsoldprice", json.getString("lastsoldprice"));
		            intent.putExtra("lastsolddate", json.getString("lastsolddate"));
		            intent.putExtra("yearbuild", json.getString("yearbuild"));
		            intent.putExtra("lotsizesqft", json.getString("lotsizesqft"));
		            intent.putExtra("amount", json.getString("amount"));
		            intent.putExtra("lastupdated", json.getString("lastupdated"));
		            intent.putExtra("finishedsqft", json.getString("finishedsqft"));
		            intent.putExtra("valuechange", json.getString("valuechange"));
		            intent.putExtra("bathrooms", json.getString("bathrooms"));
		            intent.putExtra("rangelow", json.getString("rangelow"));
		            intent.putExtra("rangehigh", json.getString("rangehigh"));
		            intent.putExtra("bedrooms", json.getString("bedrooms"));
		            intent.putExtra("rent_lastupdated", json.getString("rent_lastupdated"));
		            intent.putExtra("rent_amount", json.getString("rent_amount"));
		            intent.putExtra("taxassessmentyear", json.getString("taxassessmentyear"));
		            intent.putExtra("rent_valuechange", json.getString("rent_valuechange"));
		            intent.putExtra("taxassessment", json.getString("taxassessment"));
		            intent.putExtra("rent_rangelow", json.getString("rent_rangelow"));
		            intent.putExtra("rent_rangehigh", json.getString("rent_rangehigh"));
		            intent.putExtra("valuechangeflag", json.getString("valuechangeflag"));
		            intent.putExtra("rent_valuechangeflag", json.getString("rent_valuechangeflag"));
		            intent.putExtra("chartone", json.getString("chartone"));
		            intent.putExtra("chartfive", json.getString("chartfive"));
		            intent.putExtra("chartten", json.getString("chartten"));
		            intent.putExtra("valuechangexxx", json.getString("valuechangexxx"));
		            startActivity(intent);
	        	}
	        } catch (JSONException e) {
	          e.printStackTrace();
	        }
	      }
	}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Spinner spinner = (Spinner) findViewById(R.id.boxState);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.state_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
    	spinner.setAdapter(adapter);
    	 
    	ImageView zillowimg = (ImageView)findViewById(R.id.zillowImage);
    	zillowimg.setOnClickListener(new View.OnClickListener(){
    	    public void onClick(View v){
    	        Intent intent = new Intent();
    	        intent.setAction(Intent.ACTION_VIEW);
    	        intent.addCategory(Intent.CATEGORY_BROWSABLE);
    	        intent.setData(Uri.parse("http://www.zillow.com"));
    	        startActivity(intent);
    	    }
    	});
    	
    	final EditText boxAddress = (EditText) findViewById(R.id.boxAddress);

    	boxAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {

            	TextView firstCheck = (TextView)findViewById(R.id.break_2);
            	if(boxAddress.getText().toString().equals(""))
            		firstCheck.setText("This field is required");
            	else
            		firstCheck.setText(" ");
                // TODO Auto-generated method stub
            }
        });
    	
    	final EditText boxCity = (EditText) findViewById(R.id.boxCity);

    	boxCity.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {

            	TextView secondCheck = (TextView)findViewById(R.id.break_3);
            	if(boxCity.getText().toString().equals(""))
            		secondCheck.setText("This field is required");
            	else
            		secondCheck.setText(" ");
                // TODO Auto-generated method stub
            }
        });
    	
    	
    	final Spinner boxState=(Spinner) findViewById(R.id.boxState);
    	boxState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
    	    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
    	        TextView thirdCheck = (TextView)findViewById(R.id.break_4);
            	if(!boxState.getSelectedItem().toString().equals("Choose State"))
            		thirdCheck.setText(" ");
    	    }
    	    public void onNothingSelected(AdapterView<?> parent) {
    	    }
    	});
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    public void sendMessage(View view){
    	EditText boxCity = (EditText) findViewById(R.id.boxCity);
    	EditText boxAddress = (EditText) findViewById(R.id.boxAddress);
    	Spinner boxState=(Spinner) findViewById(R.id.boxState);
    	
    	if(boxAddress.getText().toString().equals("") || boxCity.getText().toString().equals("") || boxState.getSelectedItem().toString().equals("Choose State")){
    		TextView firstCheck = (TextView)findViewById(R.id.break_2);
        	if(boxCity.getText().toString().equals(""))
        		firstCheck.setText("This field is required");
    		TextView secondCheck = (TextView)findViewById(R.id.break_3);
        	if(boxCity.getText().toString().equals(""))
        		secondCheck.setText("This field is required");
        	TextView thirdCheck = (TextView)findViewById(R.id.break_4);
        	if(boxState.getSelectedItem().toString().equals("Choose State"))
        		thirdCheck.setText("This field is required");
    	}
    	else{
    		new JSONParse().execute();
    	}
    }
}
