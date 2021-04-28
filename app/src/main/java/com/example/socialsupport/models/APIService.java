package com.example.socialsupport.models;

import com.example.socialsupport.notification.MyResponse;
import com.example.socialsupport.notification.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            { "Content-Type:application/json",
            "Authorization:Key=AAAAzT6s_AE:APA91bFXhyHQT4m1oTmrNV9s_rmOzrCLchI-" +
                    "dB62jtSwkYXJ88gzs_XraIeI0MWq1k3vJFG67o84MuMsDzCmqSsX" +
                    "6htC6Y3AV76MbU3IM8xlprBk5PLx9xBWJl4u54PaaJuZC0PykXwQ"
            }
    )
    @POST("fcm/send")
    Call<MyResponse>sendNotification(@Body Sender body);

}
