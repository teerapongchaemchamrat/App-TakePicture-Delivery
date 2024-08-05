package com.example.btc_delivery_v2;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface RetrofitAPI {
    @Multipart
    @POST("delivery/uploads/v2")
    Call<ResponseBody> uploadPicture(
            @Part MultipartBody.Part file1,
            @Part MultipartBody.Part file2,
            @Part MultipartBody.Part file3,
            @Part("co_num") RequestBody co_num,
            @Part("co_line") RequestBody co_line,
            @Part("qty") RequestBody qty);
}
