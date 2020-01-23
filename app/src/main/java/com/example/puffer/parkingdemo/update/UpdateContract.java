package com.example.puffer.parkingdemo.update;

interface UpdateContract {
    interface View {
        void updateFinished();

        void updateFailed(String msg);
    }

    interface Presenter {
        void downloadDataSet();
    }
}
