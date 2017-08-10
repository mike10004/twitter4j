package twitter4j;

import org.junit.Test;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import java.io.ByteArrayInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotNull;

public class IDsJSONImplTest {

    @Test
    public void construct() throws Exception {
        Configuration conf = new ConfigurationBuilder().build();
        String json = "{\n" +
                "  \"previous_cursor\": 0,\n" +
                "  \"ids\": [\n" +
                "    657693,\n" +
                "    183709371,\n" +
                "    3191321,\n" +
                "    783214\n" +
                "  ],\n" +
                "  \"previous_cursor_str\": \"0\",\n" +
                "  \"next_cursor\": 0,\n" +
                "  \"next_cursor_str\": \"0\"\n" +
                "}";
        Charset utf8 = Charset.forName("UTF-8");
        /*
         * This simulates what seems to happen with the input stream from an instance
         * of sun.net.www.protocol.http.HttpURLConnection if it's already been fully
         * consumed.
         */
        InputStream content = new FilterInputStream(new ByteArrayInputStream(json.getBytes(utf8))) {

            private boolean consumed = false;

            @Override
            public int read(byte[] b, int off, int len) throws IOException {
                if (consumed) {
                    throw new IOException("Stream closed");
                }
                int n = super.read(b, off, len);
                if (n < len) {
                    consumed = true;
                }
                return n;
            }
        };
        HttpResponse res = new MockHttpResponse(content);
        IDsJSONImpl ids = new IDsJSONImpl(res, conf);
        long[] expectedIds = {657693, 183709371, 3191321, 783214};
        assertArrayEquals("ids", expectedIds, ids.getIDs());
        assertNotNull("json object", ids.getJson());
    }

    private static class MockHttpResponse extends HttpResponse {

        public MockHttpResponse(InputStream stream) {
            this.is = stream;
            statusCode = 200;
        }

        @Override
        public String getResponseHeader(String name) {
            return null;
        }

        @Override
        public Map<String, List<String>> getResponseHeaderFields() {
            return new java.util.HashMap<String, List<String>>();
        }

        @Override
        public void disconnect() throws IOException {

        }
    }
}
