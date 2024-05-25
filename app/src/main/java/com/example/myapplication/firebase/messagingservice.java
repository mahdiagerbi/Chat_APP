package com.example.myapplication.firebase;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.myapplication.R;
import com.example.myapplication.activities.chatActivity;
import com.example.myapplication.model.user;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.example.myapplication.utilities.constantes;

import java.util.Random;

public class messagingservice extends FirebaseMessagingService {
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);

    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        user user=new user();
        user.id=message.getData().get(constantes.KEY_USER_ID);
        user.name=message.getData().get(constantes.KEY_NAME);
        user.token=message.getData().get(constantes.KEY_FCM_TOKEN);

        int notificationid =new Random().nextInt();
        String channelid="chat_message";

        Intent intent=new Intent(this, chatActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(constantes.KEY_USER,user);
        PendingIntent pendingIntent=PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Builder builder=new NotificationCompat.Builder(this,channelid);
        builder.setSmallIcon(R.drawable.notification);
        builder.setContentTitle(user.name);
        builder.setContentText(message.getData().get(constantes.KEY_MESSAGE));
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(
                message.getData().get(constantes.KEY_MESSAGE)
        ));
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            CharSequence channelname="chat message";
            String channeldescription="this notification";
            int importance= NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel =new NotificationChannel(channelid,channelname,importance);
            channel.setDescription(channeldescription);
            NotificationManager notificationManager=getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationManagerCompat notificationManagerCompat=NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(notificationid,builder.build());

    }
}
