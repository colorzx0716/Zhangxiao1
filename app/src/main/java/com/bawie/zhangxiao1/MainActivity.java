package com.bawie.zhangxiao1;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bawie.zhangxiao1.adapter.CartAdapter;
import com.bawie.zhangxiao1.bean.CartsBean;
import com.bawie.zhangxiao1.presenter.CartsPresenter;
import com.bawie.zhangxiao1.view.CartsView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class MainActivity extends AppCompatActivity implements CartsView, View.OnClickListener, CartAdapter.RefreshPriceInterface {

    private double totalPrice = 0.00;
    private int totalCount = 0;

    private List<HashMap<String,String>> goodsList;
    private ListView listView;
    private CheckBox cb_check_all;
    private TextView tv_total_price,tv_delete,tv_go_to_pay;
    private CartAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CartsPresenter presenter = new CartsPresenter(MainActivity.this,this);
        presenter.requestCarts();

    }

    @Override
    public void getCartsSuccess(List<CartsBean.DataBean> data) {
        System.out.println("data的长度 = " + data.size());
        goodsList=new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            HashMap<String,String> map=new HashMap<>();

            List<CartsBean.DataBean.ListBean> list = data.get(i).getList();
            for (int j = 0; j < list.size(); j++) {

                String[] split = list.get(j).getImages().split("\\|");

                map.put("id", String.valueOf(list.get(j).getPid()));
                map.put("name",list.get(j).getTitle());
                map.put("price", String.valueOf(list.get(j).getPrice()));
                map.put("count", String.valueOf(list.get(j).getNum()));
                //商家名字
                map.put("sellerName",data.get(i).getSellerName());

                //图片
                map.put("img",split[0]);
                goodsList.add(map);

            }
        }
        initView();
    }

    private void initView() {
        //列表
        listView = (ListView) findViewById(R.id.listview);
        //全选
        cb_check_all = (CheckBox) findViewById(R.id.all_chekbox);

        //结算总价
        tv_total_price = (TextView) findViewById(R.id.tv_total_price);
        //删除
        tv_delete = (TextView) findViewById(R.id.tv_delete);
        //结算
        tv_go_to_pay = (TextView) findViewById(R.id.tv_go_to_pay);

        //点击
        tv_go_to_pay.setOnClickListener(this);//结算
        tv_delete.setOnClickListener(this);
        cb_check_all.setOnClickListener(this);

        adapter = new CartAdapter(this,goodsList);
        adapter.setRefreshPriceInterface(this);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void getCartsFaliure(String msg) {

    }

    @Override
    public void onFaliure(Call call, IOException e) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.all_chekbox://全选
                AllTheSelected();
                break;
            case R.id.tv_go_to_pay://点击结算

                if(totalCount<=0){
                    Toast.makeText(this,"请选择要付款的商品~",Toast.LENGTH_SHORT).show();
                    return;
                }
                //下面写付款的流程
                Toast.makeText(this,"钱就是另一回事了~",Toast.LENGTH_SHORT).show();
                break;
            case R.id.tv_delete:
                if(totalCount<=0){
                    Toast.makeText(this,"请选择要删除的商品~",Toast.LENGTH_SHORT).show();
                    return;
                }
                //删除
                checkDelete(adapter.getPitchOnMap());
                break;
        }
    }

    private void checkDelete(Map<String, Integer> map) {
        List<HashMap<String,String>> waitDeleteList=new ArrayList<>();
        Map<String,Integer> waitDeleteMap =new HashMap<>();
        for(int i=0;i<goodsList.size();i++){
            if(map.get(goodsList.get(i).get("id"))==1){
                waitDeleteList.add(goodsList.get(i));
                waitDeleteMap.put(goodsList.get(i).get("id"),map.get(goodsList.get(i).get("id")));
            }
        }
        goodsList.removeAll(waitDeleteList);
        map.remove(waitDeleteMap);
        priceControl(map);
        adapter.notifyDataSetChanged();
    }

    //全选
    private void AllTheSelected() {

        Map<String,Integer> map=adapter.getPitchOnMap();
        boolean isCheck=false;
        boolean isUnCheck=false;
        Iterator iter = map.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            if(Integer.valueOf(entry.getValue().toString())==1)isCheck=true;
            else isUnCheck=true;
        }
        if(isCheck==true&&isUnCheck==false){//已经全选,做反选
            for(int i=0;i<goodsList.size();i++){
                map.put(goodsList.get(i).get("id"),0);
            }
            cb_check_all.setChecked(false);
        }else if(isCheck==true && isUnCheck==true){//部分选择,做全选
            for(int i=0;i<goodsList.size();i++){
                map.put(goodsList.get(i).get("id"),1);
            }
            cb_check_all.setChecked(true);
        }else if(isCheck==false && isUnCheck==true){//一个没选,做全选
            for(int i=0;i<goodsList.size();i++){
                map.put(goodsList.get(i).get("id"),1);
            }
            cb_check_all.setChecked(true);
        }
        priceControl(map);
        adapter.setPitchOnMap(map);
        adapter.notifyDataSetChanged();


    }

    @Override
    public void refreshPrice(Map<String, Integer> pitchOnMap) {
        priceControl(pitchOnMap);
    }

    //控制价格展示
    private void priceControl(Map<String, Integer> pitchOnMap) {
        totalCount = 0;
        totalPrice = 0.00;
        for(int i=0;i<goodsList.size();i++){
            if(pitchOnMap.get(goodsList.get(i).get("id"))==1){
                totalCount=totalCount+Integer.valueOf(goodsList.get(i).get("count"));
                double goodsPrice=Integer.valueOf(goodsList.get(i).get("count"))*Double.valueOf(goodsList.get(i).get("price"));
                totalPrice=totalPrice+goodsPrice;
            }
        }
        tv_total_price.setText("￥ "+totalPrice);
        tv_go_to_pay.setText("付款("+totalCount+")");
    }
}
