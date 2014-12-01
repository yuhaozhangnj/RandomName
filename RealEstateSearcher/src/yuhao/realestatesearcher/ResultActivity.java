package yuhao.realestatesearcher;



import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher.ViewFactory;

import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;


public class ResultActivity extends FragmentActivity implements ActionBar.TabListener {
	private UiLifecycleHelper uiHelper;
	String postId = "";
	private static final List<String> PERMISSIONS = Arrays.asList("publish_actions");
	private static final String PENDING_PUBLISH_KEY = "pendingPublishReauthorization";
	private boolean pendingPublishReauthorization = false;
	
	Bitmap[] chartbm = new Bitmap[3];
	Drawable[] drawable = new Drawable[3];
	ImageSwitcher imageSwitcher = null;
	TextSwitcher textSwitcher = null;
	Bundle b = null;
	int curIndex;
	TextView addressfullview = null;
	
	String textToShow[]={"Historical Zestimate for the past 1 year","Historical Zestimate for the past 5 year","Historical Zestimate for the past 10 year"};
	String addressFull = null;
	
	Button buttonNext = null;
	Button buttonPrevious = null;

	Animation slide_in_left, slide_out_right;
	
    ViewPager viewPager=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_result);
        
        /*View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);*/

        final ActionBar actionBar=getActionBar();
        if(actionBar != null){
        	actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        	addTabs(actionBar);
        }

        uiHelper = new UiLifecycleHelper(this, null);
        uiHelper.onCreate(savedInstanceState);
        
        b = getIntent().getExtras();
        
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "yuhao.realestatesearcher", 
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
                }
        } catch (NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
        
        ImageDownloader imageDownloaderone = new ImageDownloader();
        imageDownloaderone.execute("chartone");
        ImageDownloader imageDownloaderfive = new ImageDownloader();
        imageDownloaderfive.execute("chartfive");
        ImageDownloader imageDownloaderten = new ImageDownloader();
        imageDownloaderten.execute("chartten");
        
        
        String street=b.getString("streetkkk");
        String city=b.getString("citykkk");
        String statename=b.getString("statekkk");
        String zipcode=b.getString("zipcode");
        
        TextView table_link = (TextView)findViewById(R.id.table_linkid);
        addressfullview = (TextView)findViewById(R.id.addressfull);
        addressFull = street + ", " + city + ", " + statename + "-" + zipcode;
        table_link.setText(addressFull);
        addressfullview.setText(addressFull);
        addressfullview.setVisibility(View.INVISIBLE);
        table_link.setPaintFlags(table_link.getPaintFlags() | 8);
        table_link.setOnClickListener(new View.OnClickListener(){
    	    public void onClick(View v){
    	        Intent intent = new Intent();
    	        intent.setAction(Intent.ACTION_VIEW);
    	        intent.addCategory(Intent.CATEGORY_BROWSABLE);
    	        intent.setData(Uri.parse(b.getString("links")));
    	        startActivity(intent);
    	    }
    	});
        
        TextView row2 = (TextView)findViewById(R.id.row2text);
        row2.setText(b.getString("usecode"));
        TextView row3 = (TextView)findViewById(R.id.row3text);
        row3.setText(b.getString("yearbuild"));
        TextView row4 = (TextView)findViewById(R.id.row4text);
        row4.setText(b.getString("lotsizesqft") + " sq. ft.");
        TextView row5 = (TextView)findViewById(R.id.row5text);
        row5.setText(b.getString("finishedsqft") + " sq. ft.");
        TextView row6 = (TextView)findViewById(R.id.row6text);
        row6.setText(b.getString("bathrooms"));
        TextView row7 = (TextView)findViewById(R.id.row7text);
        row7.setText(b.getString("bedrooms"));
        TextView row8 = (TextView)findViewById(R.id.row8text);
        row8.setText(b.getString("taxassessmentyear"));
        TextView row9 = (TextView)findViewById(R.id.row9text);
        row9.setText("$" + b.getString("taxassessment"));
        TextView row10 = (TextView)findViewById(R.id.row10text);
        row10.setText("$" + b.getString("lastsoldprice"));
        TextView row11 = (TextView)findViewById(R.id.row11text);
        row11.setText(b.getString("lastsolddate"));
        TextView row12label = (TextView)findViewById(R.id.row12label);
        row12label.setText("Zestimate" + '\256' + " Property Estimate as of " + b.getString("lastupdated"));
        TextView row12 = (TextView)findViewById(R.id.row12text);
        row12.setText("$" + b.getString("amount"));
        TextView row13 = (TextView)findViewById(R.id.row13text);
        row13.setText("$" + b.getString("valuechange"));
        ImageView row13img= (ImageView) findViewById(R.id.row13image);
        if(b.getString("valuechangeflag").equals("<img src=\"up_g.gif\" />"))
        	row13img.setImageResource(R.drawable.up_g);
        else
        	row13img.setImageResource(R.drawable.down_r);
        
        TextView row14 = (TextView)findViewById(R.id.row14text);
        row14.setText("$" + b.getString("rangelow") + "-$" + b.getString("rangehigh"));
        TextView row15label = (TextView)findViewById(R.id.row15label);
        row15label.setText("Rent Zestimate" + '\256' + " Valuation as of " + b.getString("rent_lastupdated"));
        TextView row15 = (TextView)findViewById(R.id.row15text);
        row15.setText("$" + b.getString("rent_amount"));
        TextView row16 = (TextView)findViewById(R.id.row16text);
        row16.setText("$" + b.getString("rent_valuechange"));
        ImageView row16img= (ImageView) findViewById(R.id.row16image);
        if(b.getString("rent_valuechangeflag").equals("<img src=\"up_g.gif\" />"))
        	row16img.setImageResource(R.drawable.up_g);
        else
        	row16img.setImageResource(R.drawable.down_r);
        TextView row17 = (TextView)findViewById(R.id.row17text);
        row17.setText("$" + b.getString("rent_rangelow") + "-$" + b.getString("rent_rangehigh"));
        
        TextView footer1 = (TextView)findViewById(R.id.zillow_declare1);
        footer1.setText('\251' + "Zillow, Inc., 2006-2014.");
        TextView footer2 = (TextView)findViewById(R.id.zillow_declare2);
        footer2.setText("Use is subject to ");
        TextView footer3 = (TextView)findViewById(R.id.zillow_declare3);
        footer3.setText("Terms of Use");
        footer3.setPaintFlags(table_link.getPaintFlags() | 8);
        footer3.setOnClickListener(new View.OnClickListener(){
    	    public void onClick(View v){
    	        Intent intent = new Intent();
    	        intent.setAction(Intent.ACTION_VIEW);
    	        intent.addCategory(Intent.CATEGORY_BROWSABLE);
    	        intent.setData(Uri.parse("http://www.zillow.com/corp/Terms.htm"));
    	        startActivity(intent);
    	    }
    	});
        TextView footer4 = (TextView)findViewById(R.id.zillow_declare4);
        footer4.setText("What's a Zestimate?");
        footer4.setPaintFlags(table_link.getPaintFlags() | 8);
        footer4.setOnClickListener(new View.OnClickListener(){
    	    public void onClick(View v){
    	        Intent intent = new Intent();
    	        intent.setAction(Intent.ACTION_VIEW);
    	        intent.addCategory(Intent.CATEGORY_BROWSABLE);
    	        intent.setData(Uri.parse("http://www.zillow.com/zestimate"));
    	        startActivity(intent);
    	    }
    	});
        
        imageSwitcher = (ImageSwitcher) findViewById(R.id.imageswitcherid);

        slide_in_left = AnimationUtils.loadAnimation(this,
          android.R.anim.slide_in_left);
        slide_out_right = AnimationUtils.loadAnimation(this,
          android.R.anim.slide_out_right);

        imageSwitcher.setInAnimation(slide_in_left);
        imageSwitcher.setOutAnimation(slide_out_right);

        imageSwitcher.setFactory(new ViewFactory() {

         @Override
         public View makeView() {

          ImageView imageView = new ImageView(ResultActivity.this);
          imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

          LayoutParams params = new ImageSwitcher.LayoutParams(
            LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

          imageView.setLayoutParams(params);
          return imageView;

         }
        });

   
        imageSwitcher.setVisibility(View.INVISIBLE);
       
        
        textSwitcher = (TextSwitcher) findViewById(R.id.textswitcherid);
        
        textSwitcher.setInAnimation(slide_in_left);
        textSwitcher.setOutAnimation(slide_out_right);
        
        
        textSwitcher.setFactory(new ViewFactory() {
            
            public View makeView() {
                // TODO Auto-generated method stub
                // create new textView and set the properties like clolr, size etc
                TextView myText = new TextView(ResultActivity.this);
                myText.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
                myText.setTextSize(15);
        	    myText.setTypeface(null, Typeface.BOLD);
                return myText;
            }
        });
        
        textSwitcher.setText(textToShow[0]);
	    textSwitcher.setVisibility(View.INVISIBLE);
        
        curIndex = 0;
        
        buttonNext = (Button) findViewById(R.id.button_next);
        buttonNext.setOnClickListener(new OnClickListener() {

        	   @Override
        	   public void onClick(View arg0) {
        	    if (curIndex == drawable.length - 1) {
        	     curIndex = 0;
        	     imageSwitcher.setImageDrawable(drawable[0]);
        	     textSwitcher.setText(textToShow[0]);
        	    } else {
        	    	imageSwitcher.setImageDrawable(drawable[++curIndex]);
        	    	textSwitcher.setText(textToShow[curIndex]);
        	    }
        	   }
        });
        
        buttonPrevious = (Button) findViewById(R.id.button_previous);
        buttonPrevious.setOnClickListener(new OnClickListener() {

        	   @Override
        	   public void onClick(View arg0) {
        	    if (curIndex == 0) {
        	     curIndex = drawable.length - 1;
        	     imageSwitcher.setImageDrawable(drawable[curIndex]);
        	     textSwitcher.setText(textToShow[curIndex]);
        	    } else {
        	    	imageSwitcher.setImageDrawable(drawable[--curIndex]);
        	    	textSwitcher.setText(textToShow[curIndex]);
        	    }
        	   }
        });
        
        buttonNext.setVisibility(View.INVISIBLE);
        buttonPrevious.setVisibility(View.INVISIBLE);
        
        ImageView fbshareimg = (ImageView)findViewById(R.id.fbsharebutton);
    	fbshareimg.setOnClickListener(new View.OnClickListener(){
    	    public void onClick(View v){
    	    	FragmentManager fm = getSupportFragmentManager();
    	    	ArticleFragment alert = new ArticleFragment();
    	    	alert.show(fm, "Alert_Dialog");
    	    }
    	});
        
    }
    
    private void addTabs(ActionBar actionBar)
    {
        ActionBar.Tab tab1=actionBar.newTab();
        tab1.setText("BASIC INFO");
        tab1.setTabListener(this);

        ActionBar.Tab tab2=actionBar.newTab();
        tab2.setText("HISTORICAL ZESTIMATES");
        tab2.setTabListener(this);

        actionBar.addTab(tab1);
        actionBar.addTab(tab2);
    }

	@Override
	public void onTabSelected(Tab tab, android.app.FragmentTransaction ft) {
		// TODO Auto-generated method stub
		TableLayout table_one = (TableLayout)findViewById(R.id.table_layoutid);
        //View footerview = findViewById(R.id.footer);
		if(tab.getText().equals("HISTORICAL ZESTIMATES")){
	        table_one.setVisibility(View.INVISIBLE);
	        //footerview.setVisibility(View.INVISIBLE);
	        if(textSwitcher != null && addressfullview != null && imageSwitcher != null && buttonNext != null && buttonPrevious != null){
	        	imageSwitcher.setVisibility(View.VISIBLE);
	        	buttonNext.setVisibility(View.VISIBLE);
	        	buttonPrevious.setVisibility(View.VISIBLE);
	        	addressfullview.setVisibility(View.VISIBLE);
	        	textSwitcher.setVisibility(View.VISIBLE);
	        }
		}
		else{
	        table_one.setVisibility(View.VISIBLE);
	        //footerview.setVisibility(View.VISIBLE);
	        if(textSwitcher != null && addressfullview != null && imageSwitcher != null && buttonNext != null && buttonPrevious != null){
	        	imageSwitcher.setVisibility(View.INVISIBLE);
	        	buttonNext.setVisibility(View.INVISIBLE);
	        	buttonPrevious.setVisibility(View.INVISIBLE);
	        	addressfullview.setVisibility(View.INVISIBLE);
	        	textSwitcher.setVisibility(View.INVISIBLE);
	        }
		}
	}
	@Override
	public void onTabUnselected(Tab tab, android.app.FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onTabReselected(Tab tab, android.app.FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}
	
	
	private class ImageDownloader extends AsyncTask<String, Integer, Bitmap>{

			String originalstr;
		
		   @Override
		   protected void onPreExecute(){
		     //Setup is done here
		   }

		   @Override
		   protected Bitmap doInBackground(String... params) {
			   originalstr = params[0];
			   HttpURLConnection httpCon = null;
			   try{
		         URL url = new URL(b.getString(originalstr));
		         httpCon = (HttpURLConnection) url.openConnection();

		       if(httpCon.getResponseCode() != 200)
		          throw new Exception("Failed to connect");
		       }
			   catch(Exception e){
				   e.printStackTrace();
			   }

		       InputStream is = null;
		       try {
		    	   is = httpCon.getInputStream();
		       } catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		       }
		       return BitmapFactory.decodeStream(is);
		   }

		   @Override
		   protected void onProgressUpdate(Integer... params){
		      //Update a progress bar here, or ignore it, it's up to you
		   }
		   @Override
		   protected void onPostExecute(Bitmap img){
			   if(originalstr.equals("chartone")){
				   chartbm[0] = img;
				   drawable[0] =new BitmapDrawable(chartbm[0]);
			       imageSwitcher.setImageDrawable(drawable[0]);
			   }
			   else if(originalstr.equals("chartfive")){
				   chartbm[1] = img;
				   drawable[1] =new BitmapDrawable(chartbm[1]);
			   }
			   else{
				   chartbm[2] = img;
				   drawable[2] =new BitmapDrawable(chartbm[2]);
			   }
		   }

		   @Override
		   protected void onCancelled(){
		      // Handle what you want to do if you cancel this task
		   }
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);

	    uiHelper.onActivityResult(requestCode, resultCode, data, new FacebookDialog.Callback() {
	        @Override
	        public void onError(FacebookDialog.PendingCall pendingCall, Exception error, Bundle data) {
	            Toast.makeText(ResultActivity.this.getBaseContext(), "Post Error", Toast.LENGTH_SHORT).show();
	        }

	        @Override
	        public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data) {
	            postId = FacebookDialog.getNativeDialogPostId(data);
	            Toast.makeText(ResultActivity.this.getBaseContext(), "Posted Story, ID:" + postId, Toast.LENGTH_SHORT).show();
	        }
	    });
	}
	
	@Override
	protected void onResume() {
	    super.onResume();
	    uiHelper.onResume();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    uiHelper.onSaveInstanceState(outState);
	}

	@Override
	public void onPause() {
	    super.onPause();
	    uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
	    super.onDestroy();
	    uiHelper.onDestroy();
	}
	
	public class ArticleFragment extends DialogFragment {
		
		
		
		private boolean isSubsetOf(Collection<String> subset, Collection<String> superset) {
			  for (String string : subset) {
			     if (!superset.contains(string)) {
			         return false;
			     }
			  }
			  return true;
		}
		
		@Override
	    public Dialog onCreateDialog(Bundle savedInstanceState) {
	        return new AlertDialog.Builder(getActivity())
	            .setTitle("")
	            .setMessage("Post to Facebook")
	            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	                @Override
	                public void onClick(DialogInterface dialog, int which) {
	                    // do nothing (will close dialog)
	                	Toast.makeText(getActivity().getBaseContext(), "Post Cancelled", Toast.LENGTH_SHORT).show();
	                }
	            })
	            .setPositiveButton("Post Property Details",  new DialogInterface.OnClickListener() {
	                @Override
	                public void onClick(DialogInterface dialog, int which){
	                	/*Session.openActiveSession(getActivity(), true, new Session.StatusCallback() {

	                        // callback when session changes state
	                        @Override
	                        public void call(Session session, SessionState state, Exception exception) {
	                            if (session != null && session.isOpened()) {

	                                // Check for publish permissions
	                                List<String> permissions = session.getPermissions();
	                                if (!isSubsetOf(PERMISSIONS, permissions)) {
	                                    Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(
	                                            getActivity(), PERMISSIONS);
	                                    session.requestNewPublishPermissions(newPermissionsRequest);
	                                    return;
	                                }
	                            }
	                        }
	                	});*/
	                	
	                	//if(checkPermissions()){
	                		publishFeedDialog();
	                	//}
	                	//else{
	                		//requestPermissions();
	                	//}
	                	
	                }
	            })
	            .create();
	    }
		
		public void publishFeedDialog(){
			Session session = Session.getActiveSession();

		    if (session != null){

		        // Check for publish permissions    
		        List<String> permissions = session.getPermissions();
		        if (!isSubsetOf(PERMISSIONS, permissions)) {
		            pendingPublishReauthorization = true;
		            Session.NewPermissionsRequest newPermissionsRequest = new Session
		                    .NewPermissionsRequest(this, PERMISSIONS);
		        session.requestNewPublishPermissions(newPermissionsRequest);
		            return;
		        }
		    }
			
			
			if (FacebookDialog.canPresentShareDialog(getApplicationContext(),
					FacebookDialog.ShareDialogFeature.SHARE_DIALOG)) {
					    // Publish the post using the Share Dialog
					    FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(ResultActivity.this)
					    		.setLink(b.getString("links"))
					    		.setDescription("Property information from zillow.com\nLast Sold Price: $" + b.getString("lastsoldprice") + ", 30 Days Overall Change: $" + b.getString("valuechange"))
					    		.setName(addressFull)
					    		.setCaption("Property information from zillow.com")
					    		.setPicture(b.getString("chartone"))
					    		.build();
					    uiHelper.trackPendingDialogCall(shareDialog.present());

			} else 
			
			{
					    
			/*Bundle params = new Bundle();
			params.putString("app_id", "678978195548950");
		    params.putString("name", addressFull);
		    params.putString("caption", "Property information from zillow.com");
		    params.putString("description", "Last Sold Price: $" + b.getString("lastsoldprice") + ", 30 Days Overall Change: $" + b.getString("valuechange"));
		    params.putString("link", b.getString("links"));
		    params.putString("picture", b.getString("chartone"));

		    WebDialog feedDialog = (
		            new WebDialog.FeedDialogBuilder(getActivity(),
		                    Session.getActiveSession(),
		                    params)).build();
		    feedDialog.show();*/
				
				Toast.makeText(getActivity().getBaseContext(), "Post Cancelled", Toast.LENGTH_SHORT).show();
			}
			
			
			/*Session session = Session.getActiveSession();

		    if (session != null){

		        // Check for publish permissions    
		        List<String> permissions = session.getPermissions();
		        if (!isSubsetOf(PERMISSIONS, permissions)) {
		            pendingPublishReauthorization = true;
		            Session.NewPermissionsRequest newPermissionsRequest = new Session
		                    .NewPermissionsRequest(this, PERMISSIONS);
		        session.requestNewPublishPermissions(newPermissionsRequest);
		            return;
		        }

		        Bundle postParams = new Bundle();
		        postParams.putString("name", addressFull);
		        postParams.putString("caption", "Property information from zillow.com");
		        postParams.putString("description", "Last Sold Price: $" + b.getString("lastsoldprice") + ", 30 Days Overall Change: $" + b.getString("valuechange"));
		        postParams.putString("link", b.getString("links"));
		        postParams.putString("picture", b.getString("chartone"));

		        Request.Callback callback= new Request.Callback() {
		            public void onCompleted(Response response) {
		                JSONObject graphResponse = response
		                                           .getGraphObject()
		                                           .getInnerJSONObject();
		                postId = null;
		                try {
		                    postId = graphResponse.getString("id");
		                } catch (JSONException e) {
		                    e.printStackTrace();
		                }
		                FacebookRequestError error = response.getError();
		                /*if (error != null) {
		                    Toast.makeText(getActivity()
		                         .getApplicationContext(),
		                         error.getErrorMessage(),
		                         Toast.LENGTH_SHORT).show();
		                    } else {
		                        Toast.makeText(getActivity()
		                             .getApplicationContext(), 
		                             postId,
		                             Toast.LENGTH_LONG).show();
		                }*/
		            //}
		        //};

		        /*Request request = new Request(session, "me/feed", postParams, 
		                              HttpMethod.POST, callback);

		        RequestAsyncTask task = new RequestAsyncTask(request);
		        task.execute();
		    }*/
	    
		}
		
	
	}
}

