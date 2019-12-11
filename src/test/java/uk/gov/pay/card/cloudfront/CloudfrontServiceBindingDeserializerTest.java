package uk.gov.pay.card.cloudfront;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class CloudfrontServiceBindingDeserializerTest {
    private ObjectMapper mapper;

    @Before
    public void setup() {
        mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        CloudfrontServiceBindingDeserializer deserializer = new CloudfrontServiceBindingDeserializer();
        module.addDeserializer(CloudfrontServiceBinding.class, deserializer);
        mapper.registerModule(module);
    }

    @Test
    public void testDeserializer() throws Exception {
        String json = "{\"user-provided\": [ {\"credentials\": {\"keyName\": \"name\",\"keyProvider\": \"provider\",\"privateKey\": \"key\" },\"instance_name\": \"cloudfront\" }] }";
        CloudfrontServiceBinding cloudfrontServiceBinding = mapper.readValue(json, CloudfrontServiceBinding.class);
        assertThat(cloudfrontServiceBinding.getKeyName(), is("name"));
        assertThat(cloudfrontServiceBinding.getKeyProvider(), is("provider"));
        assertThat(cloudfrontServiceBinding.getPrivateKey(), is("key"));
    }
}
