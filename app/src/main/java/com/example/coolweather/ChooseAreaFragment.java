package com.example.coolweather;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coolweather.db.City;
import com.example.coolweather.db.Country;
import com.example.coolweather.db.Provice;
import com.example.coolweather.util.HttpUtil;
import com.example.coolweather.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static org.litepal.crud.DataSupport.findAll;

/**
 * Created by Administrator on 2017/5/29.
 */

public class ChooseAreaFragment extends Fragment {
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 0;
    public static final int LEVEL_COUNTRY = 0;
    private ProgressDialog progressDialog;
    private TextView textTitle;
    private Button backButton;
    private ListView listView;
    private ArrayAdapter<String > adapter;
    private List<String > dataList = new ArrayList<>();
    private List<Provice> proviceList;
    private List<City> cityList;
    private List<Country> countryList;
    private Provice selectedProvice;
    private  City selectedCity;
    private int currentLevel;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.choose_area,container,false);
        textTitle = (TextView) view.findViewById(R.id.title_text);
        backButton = (Button) view.findViewById(R.id.back_button);
        listView = (ListView) view.findViewById(R.id.list_view);
        adapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);
        Log.d("aaaaaa","aaaaa");
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvice = proviceList.get(position);
                    queryCities();
                }else if (currentLevel == LEVEL_CITY) {
                    selectedCity = cityList.get(position);
                    queryCountries();
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentLevel == LEVEL_COUNTRY){
                    queryCities();
                }else if(currentLevel == LEVEL_CITY){
                    queryProvinces();
                }
            }
        });
        Log.d("aaaaaa","onactivityCreat");
        queryProvinces();
    }

    /*
    * 查询全国所有的省，优先从数据库查询，如果没有就去服务器查询
    * */
    private void  queryProvinces(){
        Log.d("aaaaaa","queryProvinces1111");

        textTitle.setText("china");
        backButton.setVisibility(View.GONE);
        Log.d("aaaaaa","currentLevel=");
        proviceList = DataSupport.findAll(Provice.class);
        Log.d("aaaaaa","currentLevel=" +proviceList.size());
        if(proviceList.size() > 0){
            Log.d("aaaaaa","proviceList.size()=" + proviceList.size());
            dataList.clear();
            for(Provice province:proviceList){
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;


        }else {
            Log.d("aaaaaa","queryFromServer" );

            String address = "http://guolin.tech/api/china";
            queryFromServer(address,"province");
        }

    }

    /*
   * 查询省所有的城市，优先从数据库查询，如果没有就去服务器查询
   * */
    private void queryCities() {
        textTitle .setText(selectedProvice.getProvinceName());
        backButton.setVisibility(View.VISIBLE);
        cityList = DataSupport.where("provinceid = ?",String.valueOf(selectedProvice.getId())).find(City.class);
        if(cityList.size() > 0){
            dataList.clear();
            for(City city : cityList){
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_CITY;
        }else{
            int provinceCode = selectedProvice.getProvinceCode();
            queryFromServer("http://guolin.tech/api/china/"+provinceCode,"city");
        }
    }

    /*
   * 查询市所有的县城，优先从数据库查询，如果没有就去服务器查询
   * */
    private void queryCountries() {
        textTitle.setText(selectedCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        countryList = DataSupport.where("cityid = ?",String.valueOf(selectedCity.getId())).find(Country.class);
        if (countryList.size() > 0) {
            dataList.clear();
            for(Country country:countryList) {
                dataList.add(country.getCountryName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_COUNTRY;
        }else {
            int ProvinceCode = selectedProvice.getProvinceCode();
            int cityCode = selectedCity.getCityCode();
            queryFromServer("http://guolin.tech/api/china/" + ProvinceCode + "/" +cityCode,"country");
        }
    }

    /*
    * 根据传入的地址和类型从服务器查询省市县数据
    * */
    private void queryFromServer(String address, final String type) {
        showProgressDialog();
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void run() {
                        Log.d("aaaaaa","onFailure" );
                        closeProgressDialog();
                        Toast.makeText(getContext(),"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("aaaaaa","onResponse" );
                String responseText = response.body().string();
                boolean result = false;
                if ("province".equals(type)) {
                    result = Utility.handleProvinceResponse(responseText);
                }else if ("city".equals(type)) {
                    result = Utility.handleCityResponse(responseText,selectedProvice.getId());
                }else if ("country".equals(type)) {
                    result = Utility.handleCountryResponse(responseText,selectedCity.getId());
                }
                if(result){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if("province".equals(type)){
                                Log.d("aaaaaa","onResponse + queryProvinces" );

                                queryProvinces();
                            }else if("city".equals(type)){
                                queryCities();
                            }else if("country".equals(type)){
                                queryCountries();
                            }
                        }
                    });
                }
            }
        });

    }

    /*
    显示进度条
    * */
    private void showProgressDialog() {
        if(progressDialog == null){
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载....");
            progressDialog.setCancelable(false);
        }
        progressDialog.show();
    }

    /*
    * 关闭进度条
    * */
    private void closeProgressDialog() {
        if(progressDialog != null){
            progressDialog.dismiss();
        }
    }
}







