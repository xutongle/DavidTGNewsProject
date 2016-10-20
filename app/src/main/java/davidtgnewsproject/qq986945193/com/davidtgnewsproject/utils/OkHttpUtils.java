package davidtgnewsproject.qq986945193.com.davidtgnewsproject.utils;import android.os.Handler;import android.os.Looper;import com.google.gson.Gson;import org.json.JSONException;import org.json.JSONObject;import java.io.IOException;import java.util.Map;import java.util.concurrent.TimeUnit;import davidtgnewsproject.qq986945193.com.davidtgnewsproject.callback.OkHttpBaseCallback;import davidtgnewsproject.qq986945193.com.davidtgnewsproject.callback.OnGetByteArrayListener;import davidtgnewsproject.qq986945193.com.davidtgnewsproject.callback.OnGetJsonObjectListener;import davidtgnewsproject.qq986945193.com.davidtgnewsproject.callback.OnGetOkhttpStringListener;import davidtgnewsproject.qq986945193.com.davidtgnewsproject.enums.HttpMethodType;import okhttp3.Call;import okhttp3.Callback;import okhttp3.FormBody;import okhttp3.MediaType;import okhttp3.OkHttpClient;import okhttp3.Request;import okhttp3.RequestBody;import okhttp3.Response;/** * @author ：程序员小冰 * @新浪微博 ：http://weibo.com/mcxiaobing * @GitHub: https://github.com/QQ986945193 * @CSDN博客: http://blog.csdn.net/qq_21376985 * @交流Qq ：986945193 * <p/> * OkHttp使用单利进行封装 */public class OkHttpUtils {    private static OkHttpClient client;    private volatile static OkHttpUtils mOkHttputils;    private final String TAG = OkHttpUtils.class.getSimpleName();//获得类名    private static Handler handler;    //提交json数据    private static final MediaType JSON = MediaType.parse("application/json;charset=utf-8");    //提交字符串    private static final MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("text/x-markdown;charset=utf-8");    private static Gson gson;    private OkHttpUtils() {        client = new OkHttpClient().newBuilder().readTimeout(20, TimeUnit.SECONDS)                .connectTimeout(10, TimeUnit.SECONDS)                .writeTimeout(20, TimeUnit.SECONDS).build();        handler = new Handler(Looper.getMainLooper());        gson = new Gson();    }    //采用单例模式获取对象    public static OkHttpUtils getInstance() {        OkHttpUtils instance = null;        if (mOkHttputils == null) {            synchronized (OkHttpUtils.class) {                if (instance == null) {                    instance = new OkHttpUtils();                    mOkHttputils = instance;                }            }        }        return instance;    }    /**     * 请求指定的url返回的结果是json字符串     *     * @param url     * @param callBack     */    public void asyncJsonStringByURL(String url, final OnGetOkhttpStringListener callBack) {        final Request request = new Request.Builder().url(url).build();        client.newCall(request).enqueue(new Callback() {            @Override            public void onFailure(Call call, IOException e) {                e.printStackTrace();            }            @Override            public void onResponse(Call call, Response response) throws IOException {                if (response != null && response.isSuccessful()) {                    onSuccessJsonStringMethod(response.body().string(), callBack);                }            }        });    }    /**     * 请求返回的是jsonOject对象     *     * @param url     * @param callBack     */    public void asyncJsonObjectByURL(String url, final OnGetJsonObjectListener callBack) {        final Request request = new Request.Builder().url(url).build();        client.newCall(request).enqueue(new Callback() {            @Override            public void onFailure(Call call, IOException e) {                e.printStackTrace();            }            @Override            public void onResponse(Call call, Response response) throws IOException {                if (response != null && response.isSuccessful()) {                    onSuccessJsonObjectMethod(response.body().string(), callBack);                }            }        });    }    /**     * 请求返回的是byte字节数组     *     * @param url     * @param callBack     */    public void asyncGetByteByURL(String url, final OnGetByteArrayListener callBack) {        final Request request = new Request.Builder().url(url).build();        client.newCall(request).enqueue(new Callback() {            @Override            public void onFailure(Call call, IOException e) {                e.printStackTrace();            }            @Override            public void onResponse(Call call, Response response) throws IOException {                if (response != null && response.isSuccessful()) {                    onSuccessByteMethod(response.body().bytes(), callBack);                }            }        });    }    /**     * 表单提交  post提交     *     * @param url     * @param params     * @param callBack     */    public void sendComplexForm(String url, Map<String, String> params, final OnGetJsonObjectListener callBack) {        RequestBody request_body = builderFormData(params);//表单对象，包含以input开始的对象，以html表单为主        Request request = new Request.Builder().url(url).post(request_body).build();//采用post方式提交        client.newCall(request).enqueue(new Callback() {            @Override            public void onFailure(Call call, IOException e) {                e.printStackTrace();            }            @Override            public void onResponse(Call call, Response response) throws IOException {                if (response != null && response.isSuccessful()) {                    onSuccessJsonObjectMethod(response.body().string(), callBack);                }            }        });    }    /**     * 向服务器提交String请求     *     * @param url     * @param content     * @param callBack     */    public void sendStringByPostMethod(String url, String content, final OnGetJsonObjectListener callBack) {        Request request = new Request.Builder().url(url).post(RequestBody.create(MEDIA_TYPE_MARKDOWN, content)).build();        client.newCall(request).enqueue(new Callback() {            @Override            public void onFailure(Call call, IOException e) {                e.printStackTrace();            }            @Override            public void onResponse(Call call, Response response) throws IOException {                if (response != null && response.isSuccessful()) {                    onSuccessJsonObjectMethod(response.body().string(), callBack);                }            }        });    }    /**     * 请求返回的结果是json字符串     *     * @param jsonValue     * @param callBack     */    private void onSuccessJsonStringMethod(final String jsonValue, final OnGetOkhttpStringListener callBack) {        handler.post(new Runnable() {            @Override            public void run() {                if (callBack != null) {                    try {                        callBack.onResponse(jsonValue);                    } catch (Exception e) {                        e.printStackTrace();                    }                }            }        });    }    /**     * 请求返回的是byte[] 数组     *     * @param data     * @param callBack     */    private void onSuccessByteMethod(final byte[] data, final OnGetByteArrayListener callBack) {        handler.post(new Runnable() {            @Override            public void run() {                if (callBack != null) {                    callBack.onResponse(data);                }            }        });    }    /**     * 返回响应的结果是json对象     *     * @param jsonValue     * @param callBack     */    private void onSuccessJsonObjectMethod(final String jsonValue, final OnGetJsonObjectListener callBack) {        handler.post(new Runnable() {            @Override            public void run() {                if (callBack != null) {                    try {                        callBack.onResponse(new JSONObject(jsonValue));                    } catch (JSONException e) {                        e.printStackTrace();                    }                }            }        });    }    /**     * 利用Get请求得到Json对象     *     * @param url     * @param callback     */    public <T> void get(String url, Class<T> clazz, OkHttpBaseCallback callback) {        Request request = buildGetRequest(url);        request(request, clazz, callback);    }    /**     * 利用POST请求得到Json对象     *     * @param url     * @param callback     */    public <T> void post(String url, Map<String, String> param, Class<T> clazz, OkHttpBaseCallback callback) {        Request request = buildPostRequest(url, param);        request(request, clazz, callback);    }    public <T> void request(final Request request, final Class<T> clazz, final OkHttpBaseCallback callback) {        callback.onBeforeRequest(request);        client.newCall(request).enqueue(new Callback() {            @Override            public void onFailure(Call call, IOException e) {                callbackFailure(callback, request, e);            }            @Override            public void onResponse(Call call, Response response) throws IOException {                callback.onResponse(response);                callbackResponse(callback, response);                if (response.isSuccessful()) {                    String resultStr = response.body().string();                    LogUtil.E("okhttp_result " + resultStr);                    if (callback.mType == String.class) {                        callbackSuccess(callback, response, resultStr);                    } else {                        try {//                            Object obj = gson.fromJson(resultStr, clazz);                            Object obj = gson.fromJson(resultStr, callback.mType);                            callbackSuccess(callback, response, obj);                        } catch (com.google.gson.JsonParseException e) { // Json解析的错误                            callback.onError(response, response.code(), e);                        }                    }                } else {                    callbackError(callback, response, null);                }            }        });    }    private void callbackSuccess(final OkHttpBaseCallback callback, final Response response, final Object obj) {        handler.post(new Runnable() {            @Override            public void run() {                callback.onSuccess(response, obj);            }        });    }    private void callbackError(final OkHttpBaseCallback callback, final Response response, final Exception e) {        handler.post(new Runnable() {            @Override            public void run() {                callback.onError(response, response.code(), e);            }        });    }    private void callbackFailure(final OkHttpBaseCallback callback, final Request request, final IOException e) {        handler.post(new Runnable() {            @Override            public void run() {                callback.onFailure(request, e);            }        });    }    private void callbackResponse(final OkHttpBaseCallback callback, final Response response) {        handler.post(new Runnable() {            @Override            public void run() {                callback.onResponse(response);            }        });    }    private Request buildPostRequest(String url, Map<String, String> params) {        return buildRequest(url, HttpMethodType.POST, params);    }    private Request buildGetRequest(String url) {        return buildRequest(url, HttpMethodType.GET, null);    }    private Request buildRequest(String url, HttpMethodType methodType, Map<String, String> params) {        Request.Builder builder = new Request.Builder()                .url(url);        if (methodType == HttpMethodType.POST) {            RequestBody body = builderFormData(params);            builder.post(body);        } else if (methodType == HttpMethodType.GET) {            builder.get();        }        return builder.build();    }    private RequestBody builderFormData(Map<String, String> params) {        FormBody.Builder form_builder = new FormBody.Builder();//表单对象，包含以input开始的对象，以html表单为主        if (params != null && !params.isEmpty()) {            for (Map.Entry<String, String> entry : params.entrySet()) {                if (entry.getKey() == null || entry.getValue() == null) {                    //以防出现个别参数为空,报空指针闪退                } else {                    form_builder.add(entry.getKey(), entry.getValue());                }            }        }        return form_builder.build();    }    /**     * 同步请求，在android开发中不常用，因为会阻塞UI线程     *     * @param url     * @return     */    public String syncGetByURL(String url) {        //构建一个request请求        Request request = new Request.Builder().url(url).build();        Response response = null;        try {            response = client.newCall(request).execute();//同步请求数据            if (response.isSuccessful()) {                return response.body().string();            }        } catch (Exception e) {            e.printStackTrace();        }        return null;    }}