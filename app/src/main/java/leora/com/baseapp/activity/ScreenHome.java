package leora.com.baseapp.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.FrameLayout;

import com.android.volley.Request;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import leora.com.baseapp.Constants;
import leora.com.baseapp.customclass.CustomAppCompatActivity;
import leora.com.baseapp.R;
import leora.com.baseapp.fragments.MaterialDetailFragment;
import leora.com.baseapp.model.DbSQLiteHelper;
import leora.com.baseapp.model.apimodel.ApiResponseSampleModel;
import leora.com.baseapp.model.dbmodel.MaterialModel;
import leora.com.baseapp.network.CustomJsonObjectRequest;
import leora.com.baseapp.network.CustomResponseListener;
import leora.com.baseapp.ormfiles.DbSupport;
import leora.com.baseapp.supportfiles.CommonMethods;
import leora.com.baseapp.utils.ApiUtils;
import leora.com.baseapp.utils.DataUtils;
import leora.com.baseapp.utils.DisplayUtils;

public class ScreenHome extends CustomAppCompatActivity {

    FrameLayout frame_ly;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_home);

        setFragment(MaterialDetailFragment.newInstance());


    }

    /**
     * sample api request
     */
    protected void setFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction =
                fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.frame_ly, fragment);
        fragmentTransaction.commit();
    }

}
