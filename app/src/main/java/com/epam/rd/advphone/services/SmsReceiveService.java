package com.epam.rd.advphone.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.IBinder;
import android.provider.Telephony;
import android.telephony.SmsMessage;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.epam.rd.advphone.ContactDaoInjection;
import com.epam.rd.advphone.R;
import com.epam.rd.advphone.SmsDaoInjection;
import com.epam.rd.advphone.database.ContactsDao;
import com.epam.rd.advphone.database.SmsDao;
import com.epam.rd.advphone.models.Sms;
import com.epam.rd.advphone.views.SmsActivity;

import java.util.concurrent.Executors;

import static com.epam.rd.advphone.Constants.CONTACT_NAME;
import static com.epam.rd.advphone.Constants.CONTACT_NUMBER;

public class SmsReceiveService extends Service {
    private SmsDao smsDao;
    private ContactsDao contactsDao;
    private NotificationManagerCompat notificationManager;
    private static final int BEGIN_INDEX = 3;
    private static final String NOTIFICATION_CHANNEL_ID = "ADVPHONE_CHANEL_ID";
    private static final String NOTIFICATION_CHANNEL_NAME = "ADVPHONE_CHANEL";

    @Override
    public void onCreate() {
        super.onCreate();
        smsDao = SmsDaoInjection.provideSmsDao(this);
        contactsDao = ContactDaoInjection.provideContactsDao(this);
        notificationManager = NotificationManagerCompat.from(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Executors.newSingleThreadExecutor().execute(() -> {
            Sms sms;
            for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                Sms.Builder smsBuilder = new Sms.Builder()
                        .setRecipientNumber(smsMessage.getOriginatingAddress())
                        .setMessageText(smsMessage.getMessageBody())
                        .setRecipientName(contactsDao.getContactNameByNumber("%" + smsMessage.getOriginatingAddress()))
                        .setTime(System.currentTimeMillis())
                        .setRecipient(true);

                sms = smsBuilder.build();
                smsDao.insertSms(sms);

                Intent openSmsActivityIntent = new Intent(this, SmsActivity.class);
                openSmsActivityIntent.putExtra(CONTACT_NUMBER, sms.getRecipientNumber());
                openSmsActivityIntent.putExtra(CONTACT_NAME, sms.getRecipientName());
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, openSmsActivityIntent, 0);

                //setup notification
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel(
                            NOTIFICATION_CHANNEL_ID,
                            NOTIFICATION_CHANNEL_NAME,
                            NotificationManager.IMPORTANCE_HIGH);

                    channel.enableLights(true);
                    channel.setLightColor(Color.RED);
                    channel.enableVibration(true);
                    notificationManager.createNotificationChannel(channel);
                }

                //content title for notification
                String recipientNumber = sms.getRecipientNumber();
                String title = sms.getRecipientName() != null ? sms.getRecipientName() : recipientNumber;

                //create single notification
                Notification newMessageNotification = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                        .setSmallIcon(R.drawable.message_processing)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_email_grey_48dp))
                        .setContentTitle(title)
                        .setContentText(sms.getMessageText())
                        .setWhen(sms.getTime())
                        .setTicker(getString(R.string.incoming_message_ticker))
                        .setAutoCancel(true)
                        .setGroup(recipientNumber)
                        .setContentIntent(pendingIntent)
                        .setPriority(NotificationManagerCompat.IMPORTANCE_HIGH)
                        .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                        .setLights(Color.RED, 1000, 1000)
                        .setVibrate(new long[]{1000})
                        .build();

                //create group of notification
                Notification summaryNotification =
                        new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                                .setContentTitle(title)
                                //set content text to support devices running API level < 24
                                .setContentText(getString(R.string.new_messages))
                                .setSmallIcon(R.drawable.message_processing)
                                //build summary info into InboxStyle template
                                .setStyle(new NotificationCompat.InboxStyle()
                                        .addLine(sms.getMessageText())
                                        .setBigContentTitle(getString(R.string.new_messages))
                                        .setSummaryText(title))
                                //specify which group this notification belongs to
                                .setGroup(recipientNumber)
                                //set this notification as the summary for the group
                                .setGroupSummary(true)
                                .setContentIntent(pendingIntent)
                                .setPriority(NotificationManagerCompat.IMPORTANCE_HIGH)
                                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                                .setAutoCancel(true)
                                .build();

                notificationManager.notify((int) (System.currentTimeMillis() / 1000), newMessageNotification);
                notificationManager.notify(Integer.parseInt(recipientNumber.substring(BEGIN_INDEX)), summaryNotification);
            }
        });

        stopSelf();

        return super.onStartCommand(intent, flags, startId);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
