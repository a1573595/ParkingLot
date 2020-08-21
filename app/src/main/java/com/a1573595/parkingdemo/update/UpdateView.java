package com.a1573595.parkingdemo.update;

import com.a1573595.parkingdemo.BaseView;

public interface UpdateView extends BaseView {
    void updateFinished();

    void updateFailed(String msg);
}
