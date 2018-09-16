package com.dvmrt.sse.controllers;

import com.dvmrt.sse.services.NotificationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    NotificationService service;

    @RequestMapping(value = "/stream", produces = "text/event-stream")
    //<editor-fold defaultstate="collapsed" desc="register">    
    public SseEmitter register() {
        SseEmitter emitter = new SseEmitter();

        service.register(emitter);

        return emitter;
    }
    //</editor-fold>

    @RequestMapping(value = "/notify")
    //<editor-fold defaultstate="collapsed" desc="notify">    
    public ResponseEntity doNotify() {
        try {
            service.doNotify();
        } catch (Exception ex0) {
            return ResponseEntity.ok("err");
        }

        return ResponseEntity.ok("ok");
    }
    //</editor-fold>

    @RequestMapping(value = "/{userId}/stream", produces = "text/event-stream")
    //<editor-fold defaultstate="collapsed" desc="register">    
    public SseEmitter register(@PathVariable("userId") String userId) {
        SseEmitter emitter = new SseEmitter();

        service.appendEmitterByUserId(userId, emitter);

        return emitter;
    }
    //</editor-fold>

    @RequestMapping(value = "/{userId}/notify")
    //<editor-fold defaultstate="collapsed" desc="notify">    
    public ResponseEntity notify(@PathVariable("userId") String userId) {
        try {
            return ResponseEntity.ok(service.notify(userId));
        } catch (Exception ex0) {
            return ResponseEntity.ok("err");
        }
    }
    //</editor-fold>

}
