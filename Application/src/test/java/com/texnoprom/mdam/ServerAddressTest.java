package com.texnoprom.mdam;

import com.texnoprom.mdam.utils.ServerAddress;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ServerAddressTest {

    @Test
    public void hostAndPort_becomeTheServerOrigin() {
        assertEquals("http://192.168.1.20:8080/",
                ServerAddress.normalize("http://192.168.1.20:8080"));
    }

    @Test
    public void fullPostAddress_keepsOnlyTheServer() {
        assertEquals("http://192.168.1.20:8080/",
                ServerAddress.normalize("http://192.168.1.20:8080/sotca/post"));
        assertEquals("http://sotca.ddns.net:8080/",
                ServerAddress.normalize("http://sotca.ddns.net:8080/sotca/post/"));
    }

    @Test
    public void missingScheme_usesHttp() {
        assertEquals("http://192.168.1.20:8080/",
                ServerAddress.normalize("192.168.1.20:8080"));
    }

    @Test
    public void blankAddress_isRejected() {
        assertNull(ServerAddress.normalize("  "));
        assertNull(ServerAddress.normalize("http://"));
    }
}
