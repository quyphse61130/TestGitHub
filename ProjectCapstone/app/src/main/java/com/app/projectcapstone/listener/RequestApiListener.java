package com.app.projectcapstone.listener;


import com.app.projectcapstone.response.Response;

public interface RequestApiListener extends RequestListener {
    void onRequestDone(Response response);
}
