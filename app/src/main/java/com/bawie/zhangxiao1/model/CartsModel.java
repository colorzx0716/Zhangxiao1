package com.bawie.zhangxiao1.model;

import com.bawie.zhangxiao1.Api;
import com.bawie.zhangxiao1.bean.CartsBean;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by 张肖肖 on 2017/10/30.
 */

public class CartsModel {

    public void getCartsData(){
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder builder = new FormBody.Builder();
        FormBody build = builder.build();
        Request request = new Request.Builder().url(Api.Carts_Api).post(build).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                onUser.onFaliure(call,e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if(response!= null && response.isSuccessful()){
                    final String result;
                    try {
                        result = response.body().string();
                        System.out.println("请求的result = " + result);
                        Gson gson = new Gson();
                        CartsBean cartsBean = gson.fromJson(result, CartsBean.class);
                        String code = cartsBean.getCode();
                        List<CartsBean.DataBean> data = cartsBean.getData();
                        String msg = cartsBean.getMsg();

                        if(code.equals("0")){
                            //成功
                            onUser.getCartsSuccess(data);

                        }else{
                            //失败
                            onUser.getCartsFaliure(msg);

                        }
                    }catch (Exception e){

                    }
                }
            }
        });

    }

    //接口
    private onUser onUser;

    public void setOnUser(onUser onUser) {
        this.onUser = onUser;
    }

    public interface onUser{
        void getCartsSuccess(List<CartsBean.DataBean> data);//成功
        void getCartsFaliure(String msg);//失败
        void onFaliure(Call call,IOException e);

    }
}
