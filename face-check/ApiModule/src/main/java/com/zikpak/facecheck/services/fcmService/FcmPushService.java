package com.zikpak.facecheck.services.fcmService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmPushService {


        public void sendToToken(String token, String title, String body){
            if(token == null || token.isBlank()){
                return;
            }

            Message message = Message.builder()
                    .setToken(token)
                    .setNotification(
                            Notification.builder()
                                    .setTitle(title)
                                    .setBody(body)
                                    .build()
                    )
                    .build();

            FirebaseMessaging.getInstance().sendAsync(message)
                    .addListener(() -> log.info("FCM push sent (async)"), Runnable::run);

        }



}
