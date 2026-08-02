package com.zone01.buy01.discovery_server.event;

import com.netflix.appinfo.InstanceInfo;
import org.springframework.cloud.netflix.eureka.server.event.EurekaInstanceCanceledEvent;
import org.springframework.cloud.netflix.eureka.server.event.EurekaInstanceRegisteredEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class EurekaEventListener {

    @EventListener
    public void onInstanceRegistered(EurekaInstanceRegisteredEvent event) {
        InstanceInfo instance = event.getInstanceInfo();
        System.out.printf(
            "[EUREKA] Registered  -> service: %-20s ip: %-15s port: %d%n",
            instance.getAppName(),
            instance.getIPAddr(),
            instance.getPort()
        );
    }

    @EventListener
    public void onInstanceCanceled(EurekaInstanceCanceledEvent event) {
        System.out.printf(
            "[EUREKA] Deregistered -> appName: %-20s instanceId: %s%n",
            event.getAppName(),
            event.getServerId()
        );
    }
}