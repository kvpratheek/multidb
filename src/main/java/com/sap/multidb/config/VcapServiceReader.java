package com.sap.multidb.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.xs.env.Credentials;
import com.sap.xs.env.Service;
import com.sap.xs.env.VcapServices;

public class VcapServiceReader {

    private VcapServices vcapServices;
    final ObjectMapper objectMapper = new ObjectMapper();

    public VcapServiceReader() {
        if (System.getenv().containsKey("VCAP_SERVICES")) {
            vcapServices = VcapServices.fromEnvironment();
        } else {
            vcapServices = VcapServices.fromSystemProperty();
        }
    }

    public Object getAttribute(final String serviceFilter, final String attribute) {
        final Service service = vcapServices.findService(serviceFilter, null, null);
        if (service != null) {
            final Credentials credentials = service.getCredentials();
            return credentials.get(attribute);
        }
        return null;
    }

    public void check( final String name){
        
    }

}
