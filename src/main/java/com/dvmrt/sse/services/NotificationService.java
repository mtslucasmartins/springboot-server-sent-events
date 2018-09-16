/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dvmrt.sse.services;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@EnableScheduling
public class NotificationService {

    private static HashMap<String, SseEmitter> userEmitters = new HashMap<>();

    private static List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    final DateFormat DATE_FORMATTER = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a");

    //<editor-fold defaultstate="collapsed" desc="register">    
    public void register(final SseEmitter emitter) {

        emitter.onCompletion(() -> {
            removeEmitter(emitter);
        });

        emitter.onTimeout(() -> {
            removeEmitter(emitter);
        });

        emitter.onError((T) -> {
            removeEmitter(emitter);
        });

        emitters.add(emitter);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="unregister">
    public void removeEmitter(final SseEmitter emitter) {
        emitters.remove(emitter);
    }
    //</editor-fold>

    public void appendEmitterByUserId(final String userId, final SseEmitter emitter) {
        emitter.onCompletion(() -> {
            removeEmitterByUserId(userId);
        });

        emitter.onTimeout(() -> {
            removeEmitterByUserId(userId);
        });

        emitter.onError((T) -> {
            removeEmitterByUserId(userId);
        });

        userEmitters.put(userId, emitter);
    }

    public void removeEmitterByUserId(final String userId) {
        userEmitters.remove(userId);
    }

    @Async
    @Scheduled(fixedRate = 5000)
    public void doNotify() throws IOException {
        List deadEmitters = new ArrayList<>();
        emitters.forEach(emitter -> {
            try {
                emitter.send(SseEmitter.event()
                        .data(DATE_FORMATTER.format(new Date()) + " : " + UUID.randomUUID().toString()));
            } catch (Exception e) {
                deadEmitters.add(emitter);
            }
        });
        emitters.removeAll(deadEmitters);
    }

    //<editor-fold defaultstate="collapsed" desc="notify">    
    public String notify(String userId) throws IOException {
        if (userEmitters.containsKey(userId)) {
            try {
                SseEmitter emitter = userEmitters.get(userId);
                emitter.send(SseEmitter.event()
                        .data(DATE_FORMATTER.format(new Date()) + " : " + UUID.randomUUID().toString()));

                return "Message sent.";
            } catch (Exception e) {
                userEmitters.remove(userId);
                return "Couldn't find user.";
            }
        }
        return "Couldn't find user.";
    }
    //</editor-fold>

}
