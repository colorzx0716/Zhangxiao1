package com.bawie.zhangxiao1.presenter;

import android.content.Context;

import com.bawie.zhangxiao1.bean.CartsBean;
import com.bawie.zhangxiao1.model.CartsModel;
import com.bawie.zhangxiao1.view.CartsView;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;

/**
 * Created by 张肖肖 on 2017/10/30.
 */

public class CartsPresenter implements CartsModel.onUser {

    private CartsModel cartsModel;
    private CartsView cartsView;
    private Context context;

    public CartsPresenter(CartsView cartsView, Context context) {
        this.cartsView = cartsView;
        this.context = context;
        cartsModel = new CartsModel();
        cartsModel.setOnUser(this);
    }

    //自己定义一个方法
    public void requestCarts(){
        cartsModel.getCartsData();
    }

    @Override
    public void getCartsSuccess(List<CartsBean.DataBean> data) {
        cartsView.getCartsSuccess(data);

    }

    @Override
    public void getCartsFaliure(String msg) {
        cartsView.getCartsFaliure(msg);

    }

    @Override
    public void onFaliure(Call call, IOException e) {
       cartsView.onFaliure(call,e);
    }
}
