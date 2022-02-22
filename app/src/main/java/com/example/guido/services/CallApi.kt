import com.example.guido.models.Note
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST


object ApiClient {
    private const val BASE_URL: String = "http://192.168.182.96:5000"

    private val gson : Gson by lazy {
        GsonBuilder().setLenient().create()
    }

    private val httpClient : OkHttpClient by lazy {
        OkHttpClient.Builder().build()
    }

    private val retrofit : Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    val apiService : CallApi by lazy{
        retrofit.create(CallApi::class.java)
    }
}

interface CallApi {

    @GET("/test")
    suspend fun getApi(): Response<Note>;

    @POST("/process")
    suspend fun getNote(@Body array: com.example.guido.models.Body) : Response<Note>;
}