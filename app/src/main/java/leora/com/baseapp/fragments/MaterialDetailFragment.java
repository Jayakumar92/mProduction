package leora.com.baseapp.fragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import leora.com.baseapp.Constants;
import leora.com.baseapp.R;
import leora.com.baseapp.model.DbSQLiteHelper;
import leora.com.baseapp.model.dbmodel.MaterialModel;
import leora.com.baseapp.network.CustomJsonObjectRequest;
import leora.com.baseapp.network.CustomResponseListener;
import leora.com.baseapp.supportfiles.CommonMethods;
import leora.com.baseapp.utils.ApiUtils;
import leora.com.baseapp.utils.DataUtils;
import leora.com.baseapp.utils.DisplayUtils;

public class MaterialDetailFragment extends android.support.v4.app.Fragment {

    RecyclerView recycler_view;
    DbSQLiteHelper dbSQLiteHelper;
    LinearLayout fab_ly;
    ArrayList<MaterialModel> materialModels = new ArrayList<MaterialModel>();
    ArrayList<MaterialModel> filtered_lists = new ArrayList<MaterialModel>();
    RecyclerView.LayoutManager mLayoutManager;
    EditText search_et;
    boolean is_dialog_showing = false;
    RecyclerViewMaterial recyclerViewMaterial;

    public static MaterialDetailFragment newInstance() {

        final MaterialDetailFragment fragment = new MaterialDetailFragment();
        return fragment;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_material_fagment, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeView(view);
        setupValues();
        setupListeners();
    }

    private void initializeView(View view) {

        recycler_view = view.findViewById(R.id.recycler_view);
        search_et = view.findViewById(R.id.search_et);
        fab_ly = view.findViewById(R.id.fab_ly);

        mLayoutManager = new LinearLayoutManager(getActivity());
        recycler_view.setLayoutManager(mLayoutManager);


    }

    private void setupValues() {
        dbSQLiteHelper = CommonMethods.getDbModel(getActivity());
        proceedSampleRequest();
        recyclerViewMaterial = new RecyclerViewMaterial(materialModels);


    }

    private void setupListeners() {


        search_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                String str = charSequence.toString();

                filtered_lists = new ArrayList<MaterialModel>();

                for (int j = 0; j < materialModels.size(); j++) {

                    if (materialModels.get(j).name.toLowerCase().startsWith(str.toLowerCase())) {

                        filtered_lists.add(materialModels.get(j));

                    }

                }


                recycler_view.setAdapter(new RecyclerViewMaterial(filtered_lists));


            }

            @Override
            public void afterTextChanged(Editable editable) {


            }
        });


        fab_ly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddAlert(getActivity(),"","");
            }
        });

    }


    public class RecyclerViewMaterial extends RecyclerView.Adapter<RecyclerViewHolder> {

        ArrayList<MaterialModel> materialModels_final = new ArrayList<MaterialModel>();


        public RecyclerViewMaterial(ArrayList<MaterialModel> materialModels) {
            this.materialModels_final = materialModels;

        }


        @Override
        public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.material_item, null);
            RecyclerViewHolder rcv = new RecyclerViewHolder(layoutView);
            return rcv;
        }

        @Override
        public void onBindViewHolder(final RecyclerViewHolder holder, final int position) {

            final MaterialModel materialModel = materialModels_final.get(position);

            holder.material_name_tv.setText(materialModel.name);
            holder.material_ref_id_tv.setText(materialModel.ref_id);
            holder.upadte_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showAddAlert(getActivity(),materialModel.name,materialModel.ref_id);
                }
            });


        }

        @Override
        public int getItemCount() {
            return materialModels_final.size();
        }
    }


    class RecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView material_ref_id_tv, material_name_tv,upadte_tv;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            material_ref_id_tv = itemView.findViewById(R.id.material_ref_id_tv);
            material_name_tv = itemView.findViewById(R.id.material_name_tv);
            upadte_tv = itemView.findViewById(R.id.upadte_tv);
        }

    }


    public void proceedSampleRequest() {
        final String url = Constants.URL_SYNC_DATA;

        Map<String, String> params = ApiUtils.getApiRequestDefaultMap();
        params.put("param_key1", "param_val1");

        JSONObject request_obj = DataUtils.convertMapToJsonObj(params);


        new CustomJsonObjectRequest(getActivity(), true, Request.Method.POST, url, request_obj, new CustomResponseListener() {
            @Override
            public void responseSuccess(JSONObject response) {
                Log.e("cameonss", "===" + response);
                try {
                    if (response.getBoolean("status")) {


                        try {
                            {
                                JSONObject jsonObject = response;
                                String server_time = jsonObject.getString("server_time");

                                HashMap<String, String> row_config = new HashMap<String, String>();
                                row_config.put("id", "1");
                                row_config.put("syncbucket_last_sync_time", server_time);

                                dbSQLiteHelper.commonInsertUpdate(Constants.TBL_CONFIG, "id", row_config);

                                try {
                                    JSONArray tables_arr = jsonObject.optJSONArray("tables");
                                    for (int k = 0; k < tables_arr.length(); k++) {

                                        JSONObject each_table_arr = tables_arr.getJSONObject(k);
                                        String table_name = each_table_arr.getString("name");
                                        String uk = each_table_arr.getString("uk");


                                        try {
                                            JSONArray table_value_arr = each_table_arr.optJSONArray("values");

                                            for (int j = 0; j < table_value_arr.length(); j++) {
                                                try {
                                                    JSONObject table_values_each = table_value_arr.getJSONObject(j);

                                                    try {
                                                        HashMap<String, String> each_row = new HashMap<String, String>();
                                                        Iterator<String> iter = table_values_each.keys();
                                                        while (iter.hasNext()) {
                                                            String key = iter.next();
                                                            String value = table_values_each.optString(key);
                                                            each_row.put(key, value);
                                                        }

                                                        each_row.put("last_sync_time", System.currentTimeMillis() + "");
                                                        Log.e("inserrtt", "==" + table_name + "==" + uk + "==" + each_row.toString());
                                                        dbSQLiteHelper.commonInsertUpdate(table_name, uk, each_row);

                                                    } catch (Exception e) {
                                                        Log.e("jobj_err5", "===" + e);
                                                        e.printStackTrace();
                                                    }
                                                } catch (Exception e) {
                                                    Log.e("jobj_err4", "===" + e);
                                                    e.printStackTrace();
                                                }
                                            }


                                            materialModels = dbSQLiteHelper.getMaterialModels();

                                            Log.e("array_size ", materialModels.size() + "==="+ dbSQLiteHelper.getMaterialAuditModels().size());

                                            recycler_view.setAdapter(new RecyclerViewMaterial(materialModels));


                                        } catch (Exception e) {
                                            Log.e("jobj_err3", "===" + e);
                                            e.printStackTrace();
                                        }

                                    }
                                } catch (Exception e) {
                                    Log.e("jobj_err2", "===" + e);
                                    e.printStackTrace();
                                }

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    } else {
                        DisplayUtils.showMessage(getActivity(), "Sync Failed");
                    }

                } catch (Exception e) {
                    e.printStackTrace();

                }
            }

            @Override
            public void responseFailure(JSONObject response) {

            }

            @Override
            public void responseError(String message) {
                Log.e("cameonerr", "===" + message);
            }
        });
    }


    public void addDataRequest(String name, String ref_id) {
        final String url = Constants.URL_ADD_DATA;

        Map<String, String> params = ApiUtils.getApiRequestDefaultMap();
        params.put("ref_id", ref_id);
        params.put("name", name);

        JSONObject request_obj = DataUtils.convertMapToJsonObj(params);


        new CustomJsonObjectRequest(getActivity(), true, Request.Method.POST, url, request_obj, new CustomResponseListener() {
            @Override
            public void responseSuccess(JSONObject response) {
                Log.e("cameonss", "===" + response);
                try {
                    if (response.getBoolean("status")) {

                        proceedSampleRequest();


                    }
                } catch (Exception e) {
                    e.printStackTrace();

                }
            }

            @Override
            public void responseFailure(JSONObject response) {

            }

            @Override
            public void responseError(String message) {
                Log.e("cameonerr", "===" + message);
            }
        });
    }


    public void showAddAlert(Activity activity, String upadte_name, String upadte_id) {
        if (!is_dialog_showing) {
            is_dialog_showing = true;

            {
                final android.support.v7.app.AlertDialog.Builder dialogBuilder = new android.support.v7.app.AlertDialog.Builder(activity);
                LayoutInflater inflater = activity.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.secondary_card_dialog, null);

                final EditText add_ref_id_et = dialogView.findViewById(R.id.add_ref_id_et);
                final EditText add_name_et = dialogView.findViewById(R.id.add_name_et);


                TextView add_tv = dialogView.findViewById(R.id.add_tv);

                add_ref_id_et.setText(upadte_id);
                add_name_et.setText(upadte_name);


                add_tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String name_et = add_name_et.getText().toString().trim();
                        String ref_id_et = add_ref_id_et.getText().toString().trim();


                        if (DataUtils.isStringValueExist(name_et) && DataUtils.isStringValueExist(ref_id_et)) {
                            addDataRequest(name_et, ref_id_et);
                        } else {
                            DisplayUtils.showMessage(getActivity(), "Pls Enter Details");
                        }


                    }
                });


                dialogBuilder.setView(dialogView);

                final android.support.v7.app.AlertDialog alertDialog = dialogBuilder.create();

                alertDialog.getWindow().

                        setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));


                alertDialog.setCancelable(true);

                alertDialog.getWindow().

                        setSoftInputMode(
                                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                alertDialog.show();
                alertDialog.getWindow().

                        setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                alertDialog.setOnKeyListener(new DialogInterface.OnKeyListener()

                {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if (keyCode == KeyEvent.KEYCODE_BACK &&
                                event.getAction() == KeyEvent.ACTION_UP &&
                                !event.isCanceled()) {
                            alertDialog.cancel();
                            is_dialog_showing = false;
                            return true;
                        }
                        return false;
                    }
                });

                alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener()

                {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        is_dialog_showing = false;
                    }
                });
            }

        }
    }
}
