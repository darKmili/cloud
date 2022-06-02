package com.cloud.encrypting_cloud_storage.service.impl;

import com.cloud.encrypting_cloud_storage.service.BlockService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.URL;
import java.util.ArrayList;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CephFileBlockServiceImplTest {
    @Autowired
    @Qualifier("cephFileBlockService")
    BlockService cephFileBlockService;

    @Test
    void uploadBlock() throws Exception {
        cephFileBlockService.uploadBlock(null);
        assert cephFileBlockService!=null;
    }
    @Test
    void testJson(){
        ArrayList<URL> urls = new ArrayList<>();
        urls.add(new URL("dwada"))
    }
}