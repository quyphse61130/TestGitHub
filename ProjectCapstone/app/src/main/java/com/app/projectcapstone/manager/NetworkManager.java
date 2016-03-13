package com.app.projectcapstone.manager;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;

import com.app.projectcapstone.constant.Constant;
import com.app.projectcapstone.constant.ResponseConstant;
import com.app.projectcapstone.response.Response;
import com.app.projectcapstone.utils.Utils;


import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Map;

public class NetworkManager {
    public static final String NET_SUPPORT_WIFI = "WIFI";
    public static final String NET_SUPPORT_MOBILE = "3G";
    public static final String[] NET_SUPPORT = {NetworkManager.NET_SUPPORT_WIFI, NetworkManager.NET_SUPPORT_MOBILE};
    private static final String LOG_TAG = NetworkManager.class.getSimpleName();
    private static final int BUFFER_SIZE_DEFAULT = 4096;
    private static final String CHARSET_DEFAULT = "UTF-8";
    private static final int TIME_OUT = 30000;

    /**
     * This method is used for checking network connected or not.
     *
     * @return true: if connected otherwise false.
     */
    public static boolean isConnected(Context context) {
        NetworkInfo networkInfo = getConnectedNetwork(context);
        return networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
    }

    public static boolean is3GConnected(Context context) {
        final ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
    }

    public static NetworkInfo getConnectedNetwork(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo();
    }

    /**
     * This method to close all open resource, include: connection, Closeable (ex: stream) ....
     *
     * @param httpURLConnection Connection need to close
     * @param closeables        Closeable (ex: stream) need to close
     * @return Is close success status
     */
    public static boolean releaseResourceSafely(HttpURLConnection httpURLConnection, Closeable... closeables) {
        boolean isSuccessReleased = false;
        try {
            for (Closeable closeable : closeables) {
                if (closeable != null) {
                    closeable.close();
                }
            }
            isSuccessReleased = true;
        } catch (IOException e) {
            Log.w(LOG_TAG, "Exception " + e.getMessage());
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
        return isSuccessReleased;
    }

    /**
     * This method is used to execute a POST request to a specified URL
     *
     * @param urlString The specified URL to call POST request to
     * @param params    The parameters to append to the URL
     * @return a response object contain response information from server
     */
    public Response executeRequest(String urlString,
                                   Map<String, String> params,
                                   String jsonDataContent,
                                   String method) {
        HttpURLConnection httpUrlCon = null;
        Response response = new Response(ResponseConstant.STATUS_UNKNOWN_ERROR);
        // Check network open
        if (!isConnected(com.app.projectcapstone.application.Application.getInstance())) {
            // Network disabled
            response.setStatusCode(ResponseConstant.STATUS_REQUEST_NETWORK_DISABLED);
            Log.w(LOG_TAG, "Network disabled");
            return response;
        }
        InputStream is = null;
        try {
            // Make url
            URL url = new URL(makeUrlParams(urlString, params));
            Log.w(LOG_TAG, "Call url... " + url);

            // Open connection
            httpUrlCon = (HttpURLConnection) url.openConnection();
            bindingHttpUrlConnection(jsonDataContent, method, httpUrlCon);

            is = httpUrlCon.getInputStream();
            byte[] data = new byte[BUFFER_SIZE_DEFAULT];
            int length;
            StringBuilder sb = new StringBuilder();
            while ((length = is.read(data)) != -1) {
                sb.append(new String(data, 0, length));
            }
            // Convert to Response
            Log.w(LOG_TAG, "Response string: " + sb.toString());
            //response = new Gson().fromJson(sb.toString(), Response.class);
            response.setStringData(sb.toString());
        } catch (SocketTimeoutException | FileNotFoundException | UnknownHostException e) {
            // Cannot ping to host - timed out
            response.setStatusCode(ResponseConstant.STATUS_REQUEST_TIME_OUT);
            Log.w(LOG_TAG, "Request timeout: " + e);
        } catch (IOException e) {
            Log.w(LOG_TAG, "Unknown exception: " + e);
        } finally {
            // Save to release resource
            releaseResourceSafely(httpUrlCon, is);
        }
        return response;
    }

    private void bindingHttpUrlConnection(String jsonDataContent, String method, HttpURLConnection httpUrlCon) throws IOException {
        httpUrlCon.setConnectTimeout(TIME_OUT);
        httpUrlCon.setReadTimeout(TIME_OUT);
        // Set method
        httpUrlCon.setRequestMethod(method);

        if (!TextUtils.isEmpty(jsonDataContent)) {
            httpUrlCon.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            httpUrlCon.connect();
            processRawAPIData(httpUrlCon, jsonDataContent);
        } else {
            httpUrlCon.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        }
    }

    private void processRawAPIData(HttpURLConnection connection, String jsonDataContent) {
        DataOutputStream dataOutputStream = null;
        try {
            dataOutputStream = new DataOutputStream(connection.getOutputStream());
            dataOutputStream.writeBytes(jsonDataContent);

            dataOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (dataOutputStream != null) {
                try {
                    dataOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * This method is used to execute a POST request to a specified URL
     *
     * @param urlString The specified URL to call POST request to
     * @param params    The parameters to append to the URL
     * @return a response object contain response information from server
     */
    public Response executePostRequest(String urlString, Map<String, String> params, String jsonDataContent) {
        return executeRequest(urlString, params, jsonDataContent, Constant.POST_METHOD);
    }

    /**
     * This method is used to execute a GET request to a specified URL
     *
     * @param urlString The specified URL to call GET request to
     * @param params    The parameters to append to the URL
     * @return a response object contain response information from server
     */
    public Response executeGetRequest(String urlString, Map<String, String> params, String jsonDataContent) {
        return executeRequest(urlString, params, jsonDataContent, Constant.GET_METHOD);
    }

    /**
     * This method to make an URL, along with a list parameters
     *
     * @param urlString specified url
     * @param params    list parameters
     * @return an url which append parameters
     */
    public String makeUrlParams(String urlString, Map<String, String> params) {
        if (!urlString.endsWith("?")) {
            urlString = urlString.concat("?");
        }

        String paramString = Utils.formatParams(params, CHARSET_DEFAULT);
        urlString = urlString.concat(paramString);
        return urlString;
    }
}

