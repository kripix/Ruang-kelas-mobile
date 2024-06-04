package com.kripix.dev.ruangkelas.data.api.kelas

import com.kripix.dev.ruangkelas.data.model.IconKelasModel
import com.kripix.dev.ruangkelas.data.model.IconUploadResponse
import com.kripix.dev.ruangkelas.data.model.KelasModel
import com.kripix.dev.ruangkelas.data.model.TambahKelasModel
import com.kripix.dev.ruangkelas.data.model.UserModel
import okhttp3.MultipartBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface KelasApiEndpoint {

    @GET("read.php")
    fun data(
        @Query("userId") userId: Int
    ): Call<KelasModel>

    @GET("get_user.php")
    fun get_user(
        @Query("userId") userId: Int
    ): Call<UserModel>

    @FormUrlEncoded
    @POST("create.php")
    fun create(
        @Field("nama_kelas") kelas: String,
        @Field("nama_grade") grade: String,
        @Field("deskripsi") deskripsi: String,
        @Field("icon_kelas") icon: Int,
        @Field("wali_kelas") user: Int
    ): Call<TambahKelasModel>

    @Multipart
    @POST("create_with_icon.php")
    fun upload_icon(
        @Part file: MultipartBody.Part
    ): Call<IconUploadResponse>

    @GET("get_icon.php")
    fun get_icon(): Call<IconKelasModel>

    @FormUrlEncoded
    @POST("update.php")
    fun update(
        @Field("id") id: Int,
        @Field("nama_kelas") kelas: String,
        @Field("nama_grade") grade: String,
        @Field("deskripsi") deskripsi: String,
        @Field("icon_kelas") icon: Int,
        @Field("wali_kelas") user: Int,
        @Field("kode_kelas") kode: String
    ): Call<TambahKelasModel>

    @FormUrlEncoded
    @POST("delete.php")
    fun delete(
        @Field("id") id: Int
    ): Call<TambahKelasModel>


}
