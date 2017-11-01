package com.bawie.zhangxiao1.view;

import com.bawie.zhangxiao1.bean.CartsBean;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;

/**
 * Created by 张肖肖 on 2017/10/30.
 */

public interface CartsView {

    void getCartsSuccess(List<CartsBean.DataBean> data);//成功
    void getCartsFaliure(String msg);//失败
    void onFaliure(Call call, IOException e);

}
