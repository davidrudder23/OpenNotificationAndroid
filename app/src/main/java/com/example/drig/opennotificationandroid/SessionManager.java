package com.example.drig.opennotificationandroid;

import org.apache.http.client.HttpClient;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.DefaultedHttpContext;
import org.apache.http.protocol.HttpContext;

import java.net.CookiePolicy;

/**
 * Created by drig on 12/18/14.
 */
public class SessionManager {
    private static HttpClient httpClient;
    public static HttpClient getHttpClient() {
        if (httpClient == null) {
            httpClient = new DefaultHttpClient();
            //httpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.ACCEPT_ALL.toString());
        }
        return httpClient;
    }

}

