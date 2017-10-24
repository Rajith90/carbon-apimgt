package org.wso2.carbon.apimgt.gateway.throttling;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;


public class ThrottleDataHolderTest {

    @Test
    public void addThrottleDataFromMap() throws Exception {
        Map<String,Long> map = new HashMap<>();
        map.put("/api/1.0.0",System.currentTimeMillis());
        ThrottleDataHolder throttleDataHolder = new ThrottleDataHolder();
        throttleDataHolder.addThrottleDataFromMap(map);
        throttleDataHolder.removeThrottleData("/api/1.0.0");
    }


    @Test
    public void removeThrottledAPIKey() throws Exception {
        ThrottleDataHolder throttleDataHolder = new ThrottleDataHolder();
        throttleDataHolder.addThrottledAPIKey("/api/1.0.0",System.currentTimeMillis());
        throttleDataHolder.removeThrottledAPIKey("/api/1.0.0");
    }


    @Test
    public void addBlockingCondition() throws Exception {
        ThrottleDataHolder throttleDataHolder = new ThrottleDataHolder();
        throttleDataHolder.addAPIBlockingCondition("/api1/1.0.0","enabled");
        throttleDataHolder.removeAPIBlockingCondition("/api1/1.0.0");
        throttleDataHolder.addApplicationBlockingCondition("admin:DefaultApplication","enabled");
        throttleDataHolder.removeApplicationBlockingCondition("admin:DefaultApplication");
        throttleDataHolder.addUserBlockingCondition("user1","enabled");
        throttleDataHolder.removeUserBlockingCondition("user1");
        throttleDataHolder.setBlockingConditionsPresent(true);
        throttleDataHolder.setKeyTemplatesPresent(true);
    }

    @Test
    public void addApplicationBlockingCondition() throws Exception {
    }

    @Test
    public void addUserBlockingCondition() throws Exception {
    }

    @Test
    public void addIplockingCondition() throws Exception {
    }

    @Test
    public void addUserBlockingConditionsFromMap() throws Exception {
    }

    @Test
    public void addIplockingConditionsFromMap() throws Exception {
    }

    @Test
    public void addAPIBlockingConditionsFromMap() throws Exception {
    }

    @Test
    public void addApplicationBlockingConditionsFromMap() throws Exception {
    }

    @Test
    public void removeAPIBlockingCondition() throws Exception {
    }

    @Test
    public void removeApplicationBlockingCondition() throws Exception {
    }

    @Test
    public void removeUserBlockingCondition() throws Exception {
    }

    @Test
    public void removeIpBlockingCondition() throws Exception {
    }

    @Test
    public void addKeyTemplate() throws Exception {
    }

    @Test
    public void addKeyTemplateFromMap() throws Exception {
    }

    @Test
    public void removeKeyTemplate() throws Exception {
    }

    @Test
    public void getKeyTemplateMap() throws Exception {
    }

    @Test
    public void isThrottled() throws Exception {
    }

    @Test
    public void getThrottleNextAccessTimestamp() throws Exception {
    }

    @Test
    public void isBlockingConditionsPresent() throws Exception {
    }

    @Test
    public void setBlockingConditionsPresent() throws Exception {
    }

    @Test
    public void isKeyTemplatesPresent() throws Exception {
    }

    @Test
    public void setKeyTemplatesPresent() throws Exception {
    }

}