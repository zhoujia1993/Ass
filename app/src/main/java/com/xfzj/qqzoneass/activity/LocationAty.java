package com.xfzj.qqzoneass.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.lbssearch.TencentSearch;
import com.tencent.lbssearch.httpresponse.BaseObject;
import com.tencent.lbssearch.httpresponse.HttpResponseListener;
import com.tencent.lbssearch.object.param.SearchParam;
import com.tencent.lbssearch.object.result.SearchResultObject;
import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.map.geolocation.TencentLocationRequest;
import com.umeng.analytics.MobclickAgent;
import com.xfzj.qqzoneass.R;
import com.xfzj.qqzoneass.model.Location;
import com.xfzj.qqzoneass.utils.Constants;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.List;

public class LocationAty extends BaseCommActivity implements HttpResponseListener, SearchView.OnQueryTextListener, AdapterView.OnItemClickListener, View.OnClickListener, TencentLocationListener {

    private RelativeLayout rl;
//    private TextView tvBack;
    private TextView tv;
    private TextView tvAdd;
    private ListView lv;
    private SearchView sv;
    private Toolbar toolbar;
    private ProgressDialog pd;
    private List<Location> lists = new ArrayList<>();
    private ArrayAdapter adapter;
    private TencentLocationManager manager;

    private void assignViews() {
        rl = (RelativeLayout) findViewById(R.id.rl);
//        tvBack = (TextView) findViewById(R.id.tvBack);
        tv = (TextView) findViewById(R.id.tv);
        tvAdd = (TextView) findViewById(R.id.tvAdd);
        lv = (ListView) findViewById(R.id.lv);
        sv = (SearchView) findViewById(R.id.sv);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_aty);
        assignViews();
        sv.setSubmitButtonEnabled(false);
        sv.setOnQueryTextListener(this);
        lists.add(new Location("不显示位置", "", ""));
        lv.setOnItemClickListener(this);
//        tvBack.setOnClickListener(this);
        tvAdd.setOnClickListener(this);
        adapter = new ArrayAdapter(getApplicationContext(), R.layout.location_tv, R.id.tv, lists);

        lv.setAdapter(adapter);
        toolbar.setTitle("位置");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        toolbar.setOnMenuItemClickListener(this);
    }


    @Override
    public void onSuccess(int i, Header[] headers, BaseObject baseObject) {
        if (pd.isShowing()) {
            pd.dismiss();
        }
        sv.clearFocus();
        ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(sv.getWindowToken(), 0);
        
        if (null != baseObject) {
            SearchResultObject object = (SearchResultObject) baseObject;
            //先将之前的搜索结果清空
            if (lists.size() > 0) {
                lists.clear();
                lists.add(new Location("不显示位置", "", ""));
            }
            if (null != object.data) {
                for (SearchResultObject.SearchResultData data : object.data) {

//                    Log.i("aaa", data.address + "&" + data.location.toString() + "&");
                    StringBuilder lat = new StringBuilder();
                    lat.append(data.location.lat + "");
                    StringBuilder lon = new StringBuilder();
                    lon.append(data.location.lng + "");
                    while (lat.length() < 9) {
                        lat.append("0");
                    }
                    while (lon.length() < 10) {
                        lon.append("0");
                    }
                    Location location = new Location(data.address, lat.toString().replace(".", ""), lon.toString().replace(".", ""));
                    lists.add(location);
//                    Log.i("aaa", location.toString());
                }
                adapter.notifyDataSetChanged();
                
            }
        }


    }

    @Override
    public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
        if (pd.isShowing()) {
            pd.dismiss();
        }
        Toast.makeText(getApplicationContext(), "出错了，没找到...", Toast.LENGTH_SHORT);
//        Log.i("aaa", "onFailure" + s);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        try {
            pd = ProgressDialog.show(LocationAty.this, null, "正在查找中...", false, true);
            
            sv.setIconified(true);
            String[] strs = query.split("\\s", 2);
//            Log.i("aaa", strs[0] + "&" + strs[1] + "&");
            TencentSearch search = new TencentSearch(this);
            SearchParam.Region region = new SearchParam.Region().poi(strs[0]);

            SearchParam searchParam = new SearchParam().keyword(strs[1]).boundary(region);
            search.search(searchParam, this);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "输入格式错误,请按照“区域 地点”的格式输入！" , Toast.LENGTH_SHORT).show();
            if (pd.isShowing()) {
                pd.dismiss();
            }
        }
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {


        return false;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Location location = lists.get(position);

        Intent intent = new Intent();
        intent.putExtra(Constants.LOCATION, location);
        setResult(Constants.SELECT_LOCATION, intent);
        //Log.i("aaa", location.toString());
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //定位
            case R.id.tvAdd:
                pd = ProgressDialog.show(LocationAty.this, null, "正在定位中...", false, true);
                lockLocation();

                break;
        }
    }

    /**
     * 定位
     */
    private void lockLocation() {

        TencentLocationRequest request = TencentLocationRequest.create();
        request.setRequestLevel(TencentLocationRequest.REQUEST_LEVEL_NAME);
        request.setAllowCache(true);
        request.setInterval(20000);
        manager = TencentLocationManager.getInstance(getApplicationContext());
        int error = manager.requestLocationUpdates(request, this);
        //Log.i("aaa", "error=" + error);
    }

    @Override
    public void onLocationChanged(TencentLocation tencentLocation, int i, String s) {
        if (pd.isShowing()) {
            pd.dismiss();
        }
        if (TencentLocation.ERROR_OK == i) {
            //定位成功
            //Log.i("aaa", "name=" + tencentLocation.getName() + "address=" + tencentLocation.getAddress() + tencentLocation.getLatitude() + "&" + tencentLocation.getLongitude());
            String lat = tencentLocation.getLatitude() + "";
            String lon = tencentLocation.getLongitude() + "";

            Location location = new Location(tencentLocation.getName()+" "+tencentLocation.getAddress(), lat.replace(".", ""), lon.replace(".", ""));
            lists.add(location);
            adapter.notifyDataSetChanged();
            manager.removeUpdates(this);
        } else {
            //定位失败
            Toast.makeText(getApplicationContext(), "定位失败", Toast.LENGTH_SHORT).show();

        }


    }

    @Override
    public void onStatusUpdate(String s, int i, String s1) {

    }
    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }


    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
