package com.cloud.encrypting_cloud_storage.service.impl;

import com.cloud.encrypting_cloud_storage.models.po.FileBlockPo;
import com.cloud.encrypting_cloud_storage.service.BlockService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
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
}