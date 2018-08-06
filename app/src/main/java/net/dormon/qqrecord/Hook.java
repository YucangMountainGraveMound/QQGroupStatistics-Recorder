package net.dormon.qqrecord;

import android.content.Context;

import com.google.gson.GsonBuilder;

import net.dormon.qqrecord.bean.Msg;
import net.dormon.qqrecord.bean.Pic;

import java.lang.reflect.Field;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.PUT;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.getObjectField;
import static net.dormon.qqrecord.Utils.log;

public class Hook {

    private Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://192.168.123.134:4000/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    public void hook(final ClassLoader loader) {
        try {
            hookQQMessageFacade(loader);
        } catch (Throwable t) {
            XposedBridge.log(t);
        }
    }

    private void hookQQMessageFacade(final ClassLoader loader) {

        findAndHookMethod(
                Variants.MessageHandlerUtils,
                loader,
                "a",
                Variants.QQAppInterface,
                Variants.MessageRecord,
                Boolean.TYPE,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {

                        log("Hook MessageHandlerUtils|Params[1]:", param.args[1].toString());

                        // 是否是群消息
                        int isTroop = XposedHelpers.getIntField(param.args[1], "istroop");
                        log("Hook MessageHandlerUtils|isTroop:", isTroop + "");
                        if (isTroop == 0) {
                            return;
                        }

                        Msg message = new Msg();
                        message.className = param.args[1].getClass().getSimpleName();
                        message.selfUin = getObjectField(param.args[1], "selfuin").toString();
                        message.friendUin = getObjectField(param.args[1], "frienduin").toString();
                        message.senderUin = getObjectField(param.args[1], "senderuin").toString();
                        message.time = getObjectField(param.args[1], "time").toString();
                        message.uniSeq = getObjectField(param.args[1], "uniseq").toString();

                        message.message = "";
                        if (message.className.equals("MessageForText") || message.className.equals("MessageForMixedMsg")) {
                            message.message = getObjectField(param.args[1], "msg").toString();
                        }


                        RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new GsonBuilder().create().toJson(message));

                        MessageService msgService = retrofit.create(MessageService.class);
                        Call<ResponseBody> call = msgService.createRecord(requestBody);
                        call.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {

                            }
                        });
                    }
                }
        );


        findAndHookMethod(
                Variants.MessageForPic,
                loader,
                "getPicDownloadInfo",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);

                        Pic pic = new Pic();
                        pic.picUrl = getObjectField(param.getResult(), "j").toString();
                        pic.uniSeq = getFieldValue(param.getResult(), "a").toString();

                        RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new GsonBuilder().create().toJson(pic));

                        PicService picService = retrofit.create(PicService.class);
                        Call<ResponseBody> call = picService.createRecordPic(requestBody);
                        call.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {

                            }
                        });

                    }
                }
        );
    }

    public interface MessageService {
        @PUT("record/message")
        Call<ResponseBody> createRecord(@Body RequestBody message);
    }

    public interface PicService {
        @PUT("record/pic")
        Call<ResponseBody> createRecordPic(@Body RequestBody pic);
    }

    private static Field getDeclaredField(Object object, String fieldName) {
        Field[] field = null;
        Class<?> clazz = object.getClass();
        for (; clazz != Object.class; clazz = clazz.getSuperclass()) {
            try {
                field = clazz.getDeclaredFields();
                for (Field f : field) {
                    if (f.getName().equals("a") && f.getType() == long.class) {
                        return f;
                    }
                }
            } catch (Exception e) {
                // should keep this, or it will failed to get
            }
        }
        return null;
    }

    private static Object getFieldValue(Object object, String fieldName) {

        Field field = getDeclaredField(object, fieldName);
        if (field != null) {
            field.setAccessible(true);
        }
        try {
            if (field != null) {
                return field.getLong(object);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}