package com.app.projectcapstone.task;

import android.os.AsyncTask;
import android.util.Log;

import com.app.projectcapstone.constant.Constant;
import com.app.projectcapstone.listener.RequestApiListener;
import com.app.projectcapstone.manager.NetworkManager;
import com.app.projectcapstone.response.Response;

import java.util.Map;


public class RequestTask extends AsyncTask<String, Void, Response> {
    private RequestApiListener requestListener;
    private Map<String, String> params;
    private String jsonDataContent;

    /**
     *
     * @param requestListener co 2 event bat dau va goi xong.
     * @param params list params
     * @param jsonDataContent body request.
     */
    public RequestTask(RequestApiListener requestListener, Map<String, String> params, String jsonDataContent) {
        this.requestListener = requestListener;
        this.params = params;
        this.jsonDataContent = jsonDataContent;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        requestListener.onPrepareRequest();
    }

    @Override
    protected Response doInBackground(String... params) {
        String url = params[0];
        String method = params[1];
        NetworkManager executor = new NetworkManager();
        if (Constant.GET_METHOD.equalsIgnoreCase(method)) {
            return executor.executeGetRequest(url, this.params, jsonDataContent);
        } else {
            return executor.executePostRequest(url, this.params, jsonDataContent);
        }
    }

    @Override
    protected void onPostExecute(Response response) {
        super.onPostExecute(response);
        requestListener.onRequestDone(response);
    }

    @Override
    protected void onCancelled() {
        Log.w("onCancelled", "onCancelled 1");
        super.onCancelled();
        requestListener = new RequestApiListener() {
            @Override
            public void onPrepareRequest() {

            }

            @Override
            public void onRequestDone(Response response) {

            }
        };
    }

    @Override
    protected void onCancelled(Response response) {
        Log.w("onCancelled", "onCancelled 2");
        super.onCancelled(response);
        requestListener = new RequestApiListener() {
            @Override
            public void onPrepareRequest() {

            }

            @Override
            public void onRequestDone(Response response) {

            }
        };
    }
}

