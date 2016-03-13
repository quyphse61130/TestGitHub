package com.app.projectcapstone.utils;

import com.app.projectcapstone.constant.Constant;
import com.app.projectcapstone.constant.RequestConstant;
import com.app.projectcapstone.listener.RequestApiListener;
import com.app.projectcapstone.task.RequestTask;

import java.util.HashMap;
import java.util.Map;



public class RequestUtils {

    private static RequestUtils instance;

    public static RequestUtils getInstance() {
        if (instance == null) {
            instance = new RequestUtils();
        }
        return instance;
    }

    public void sendEmgData(RequestApiListener requestApiListener, String myoSignalContent) {
        Map<String, String> params = new HashMap<>();
        new RequestTask(requestApiListener, params, myoSignalContent).execute(RequestConstant.URL_REQUEST_EMG_DATA, Constant.POST_METHOD);
    }

    public void sendEmgDataTrain(RequestApiListener requestApiListener, String leftData, String rightData, String meaning) {
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.PARAM_L_DATA,leftData);
        params.put(RequestConstant.PARAM_R_DATA, rightData);
        params.put(RequestConstant.PARAM_MEANING,meaning);
        new RequestTask(requestApiListener, params, null).execute(RequestConstant.URL_REQUEST_EMG_DATA_TRAIN, Constant.GET_METHOD);
    }

    public void sendDataChanged(RequestApiListener requestApiListener, String editResult, String position) {
        Map<String, String> params = new HashMap<>();
        params.put("editResult",editResult);
        params.put("position",position);
        new RequestTask(requestApiListener, params, null).execute(RequestConstant.URL_REQUEST_EDIT_RESULT, Constant.POST_METHOD);
    }
    //chua dung toi
    public void sendEditData(RequestApiListener requestApiListener, String editData) {
        Map<String, String> params = new HashMap<>();
        new RequestTask(requestApiListener, params, editData).execute(RequestConstant.URL_REQUEST_EDIT_RESULT, Constant.POST_METHOD);
    }
    public void sendLoginData(RequestApiListener requestApiListener, String username, String password) {
        Map<String, String> params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);
        new RequestTask(requestApiListener, params, null).execute(RequestConstant.URL_REQUEST_LOGIN, Constant.GET_METHOD);
    }
}

