package com.a1573595.parkingdemo;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import java.lang.reflect.ParameterizedType;

public abstract class BaseActivity<P extends BasePresenter> extends AppCompatActivity
        implements BaseView {
    protected P presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        presenter = ViewModelProviders.of(this).get(getTClass());
        presenter.initPresenter(this);
    }

    public Class<P> getTClass() {
        Class<P> tClass = (Class<P>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        return tClass;
    }
}