package org.wso2.carbon.apimgt.impl.notifier.events;

import java.util.HashSet;
import java.util.Set;

public class DeployAPIInGatewayEvent extends Event {

    private int apiId;
    private String uuid;
    private String name;
    private String version;
    private String provider;
    private String apiType;
    private Set<String> gatewayLabels;
    private Set<APIEvent> associatedApis;
    private String context;

    public DeployAPIInGatewayEvent(String eventId, long timestamp, String type, String tenantDomain, int apiId,
                                   String uuid, Set<String> gatewayLabels, String name, String version, String provider,
                                   String apiType, String context, Set<APIEvent> associatedApis) {
        this.uuid = uuid;
        this.eventId = eventId;
        this.timeStamp = timestamp;
        this.type = type;
        this.apiId = apiId;
        this.tenantDomain = tenantDomain;
        this.name = name;
        this.version = version;
        this.provider = provider;
        this.gatewayLabels = gatewayLabels;
        this.name = name;
        this.context = context;
        this.provider = provider;
        this.apiType = apiType;
        this.associatedApis = associatedApis;
    }

    /**
     *
     * @param eventId
     * @param timestamp
     * @param type
     * @param tenantDomain
     * @param apiId
     * @param gatewayLabels
     * @param name
     * @param version
     * @param provider
     * @param apiType
     * @param context
     */
    public DeployAPIInGatewayEvent(String eventId, long timestamp, String type, String tenantDomain, int apiId,
                                   String uuid, Set<String> gatewayLabels, String name, String version, String provider,
                                   String apiType, String context) {
        this.uuid = uuid;
        this.eventId = eventId;
        this.timeStamp = timestamp;
        this.type = type;
        this.apiId = apiId;
        this.tenantDomain = tenantDomain;
        this.name = name;
        this.version = version;
        this.provider = provider;
        this.gatewayLabels = gatewayLabels;
        this.name = name;
        this.version = version;
        this.provider = provider;
        this.apiType = apiType;
        this.context = context;
        this.associatedApis = new HashSet<>();
    }

    public Set<String> getGatewayLabels() {

        return gatewayLabels;
    }

    public void setGatewayLabels(Set<String> gatewayLabels) {

        this.gatewayLabels = gatewayLabels;
    }

    public int getApiId() {

        return apiId;
    }

    public void setApiId(int apiId) {

        this.apiId = apiId;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }


    public String getProvider() {

        return provider;
    }

    public void setProvider(String provider) {

        this.provider = provider;
    }

    public String getApiType() {

        return apiType;
    }

    public void setApiType(String apiType) {

        this.apiType = apiType;
    }

    public Set<APIEvent> getAssociatedApis() {

        return associatedApis;
    }

    public void setAssociatedApis(Set<APIEvent> associatedApis) {

        this.associatedApis = associatedApis;
    }
    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getUuid() {

        return uuid;
    }

    public void setUuid(String uuid) {

        this.uuid = uuid;
    }
}
