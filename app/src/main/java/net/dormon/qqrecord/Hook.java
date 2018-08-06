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

    protected void hookQQMessageFacade(final ClassLoader loader) {

//        findAndHookMethod("android.app.Application", loader, "attach", Context.class, new XC_MethodHook() {
//            @Override
//            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                super.afterHookedMethod(param);
//                Context applicationContext = (Context) param.args[0];
//                if (appContext == null) {
//                    appContext = applicationContext;
//                }
//            }
//        });

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


//                        saveData(message.uniSeq + "_m", new GsonBuilder().create().toJson(message));

//                        mContext = AndroidAppHelper.currentApplication();
//                        Intent intent = new Intent(Variants.ACTION_ON_RECEIVE_MESSAGE);
//                        intent.putExtra("class_name", className);
//                        intent.putExtra("self_uin", selfUin);
//                        intent.putExtra("friend_uin", friendUin);
//                        intent.putExtra("sender_uin", senderUin);
//                        intent.putExtra("time", time);
//                        intent.putExtra("uni_seq", uniseq);
//                        intent.putExtra("message", message);
//                        intent.setPackage(Utils.PACKAGE_NAME);
//                        getRecorderContext().sendBroadcast(intent);

                        switch (message.className) {
                            /*
                            纯文字消息(表情也为文字)
                            classname:MessageForText
                            selfUin:
                            friendUin:
                            senderUin:
                            shmsgseq:125
                            uid:-1374381655
                            time:1533367411
                            isRead:false
                            isSend:0
                            extraFlag:0
                            sendFailCode:0
                            istroop:1
                            msgType:-1000
                            msg:d|6
                            bubbleid:0
                            subBubbleId:0
                            uniseq:6585762880688416650
                            isMultiMsg:false
                            msgseq:1533367411
                            */
                            case "MessageForText": {

                            }
                            /*
                            纯图片消息
                            classname:MessageForPic
                            selfUin:
                            friendUin:
                            senderUin:
                            shmsgseq:126
                            uid:72057595762728087
                            time:1533367574
                            isRead:false
                            isSend:0
                            extraFlag:0
                            sendFailCode:0
                            istroop:1
                            msgType:-2000
                            msg:0|0
                            bubbleid:0
                            subBubbleId:0
                            uniseq:6585763580476490674
                            isMultiMsg:false
                            msgseq:1533367574
                            */
                            case "MessageForPic": {

                            }
                            /*
                            既有文字又有图片消息
                            classname:MessageForMixedMsg
                            selfUin:
                            friendUin:
                            senderUin:
                            shmsgseq:127
                            uid:72057595464531977
                            time:1533367716
                            isRead:false
                            isSend:0
                            extraFlag:0
                            sendFailCode:0
                            istroop:1
                            msgType:-1035
                            msg:[|9
                            bubbleid:0
                            subBubbleId:0
                            uniseq:6585764189599240436
                            isMultiMsg:false
                            msgseq:1533367716
                             */
                            case "MessageForMixedMsg": {

                            }
                            /*
                            红包消息
                            classname:MessageForQQWalletMsg
                            selfUin:
                            friendUin:
                            senderUin:
                            shmsgseq:128
                            uid:72057595574688094
                            time:1533367848
                            isRead:false
                            isSend:0
                            extraFlag:0
                            sendFailCode:0
                            istroop:1
                            msgType:-2025
                            msg:[|10
                            bubbleid:0
                            subBubbleId:0
                            uniseq:6585764761280290993
                            isMultiMsg:false
                            msgseq:1533367848
                             */
                            case "MessageForQQWalletMsg": {

                            }
                            /*
                            语音消息
                            classname:MessageForPtt
                            selfUin:
                            friendUin:
                            senderUin:
                            shmsgseq:129
                            uid:-714161954
                            time:1533367924
                            isRead:false
                            isSend:0
                            extraFlag:0
                            sendFailCode:0
                            istroop:1
                            msgType:-2002
                            msg:0|0
                            bubbleid:0
                            subBubbleId:0
                            uniseq:6585765087336940496
                            isMultiMsg:false
                            msgseq:1533367924
                            */
                            case "MessageForPtt": {

                            }
                            /*
                            文件消息
                            classname:MessageForTroopFile
                            selfUin:
                            friendUin:
                            senderUin:
                            shmsgseq:130
                            uid:72057596079519733
                            time:1533368002
                            isRead:false
                            isSend:0
                            extraFlag:0
                            sendFailCode:0
                            istroop:1
                            msgType:-2017
                            msg:[|5
                            bubbleid:0
                            subBubbleId:0
                            uniseq:6585765417954057438
                            isMultiMsg:false
                            msgseq:1533368002
                             */
                            case "MessageForTroopFile": {

                            }
                            /*
                            日程消息
                            classname:MessageForDingdongSchedule
                            selfUin:
                            friendUin:
                            senderUin:
                            shmsgseq:5710
                            uid:72057595374392973
                            time:1533368059
                            isRead:false
                            isSend:0
                            extraFlag:0
                            sendFailCode:0
                            istroop:0
                            msgType:-5010
                            msg:[|7
                            bubbleid:0
                            subBubbleId:0
                            uniseq:6585765663832006516
                            isMultiMsg:false
                            msgseq:0
                             */
                            case "MessageForDingdongSchedule": {

                            }
                            /*
                            视频消息
                             */
                            case "MessageForShortVideo": {

                            }
                        }

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
//                        mContext = AndroidAppHelper.currentApplication();
//                        Intent intent = new Intent(Variants.ACTION_ON_RECEIVE_PIC);
//                        intent.putExtra("pic_url", pic.picUrl);
//                        intent.putExtra("uni_seq", pic.uniSeq);
//                        log("dormon:packagename|", Utils.PACKAGE_NAME);
//                        intent.setPackage(Utils.PACKAGE_NAME);
//                        getRecorderContext().sendBroadcast(intent);
//                        Utils.saveData(pic.uniSeq + "_pic", new GsonBuilder().create().toJson(pic));

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

    public static Field getDeclaredField(Object object, String fieldName) {
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
            }
        }
        return null;
    }

    public static Object getFieldValue(Object object, String fieldName) {

        Field field = getDeclaredField(object, fieldName);
        field.setAccessible(true);
        try {
            return field.getLong(object);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}