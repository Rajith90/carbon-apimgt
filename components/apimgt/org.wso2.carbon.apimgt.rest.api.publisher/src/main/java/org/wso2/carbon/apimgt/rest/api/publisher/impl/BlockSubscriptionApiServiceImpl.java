package org.wso2.carbon.apimgt.rest.api.publisher.impl;

import org.wso2.carbon.apimgt.rest.api.publisher.*;


import javax.ws.rs.core.Response;

public class BlockSubscriptionApiServiceImpl extends BlockSubscriptionApiService {
    @Override
    public Response blockSubscriptionPost(String subscriptionId,String ifMatch,String ifUnmodifiedSince){
        // do some magic!
        return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }
}
