package com.k.mq.broker.test;

import com.k.mq.broker.util.MMapUtil;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class MMapUtilTest {
    private MMapUtil mmapUtil;
    private String filePath = "/Users/yihang07/code/mq/broker/store/order_topic/00000000";

    @Before
    public void setup() throws IOException {
        mmapUtil = new MMapUtil();
        mmapUtil.loadFileInMMap(filePath, 0, 1 * 1024 * 1024);
    }

    @Test
    public void testReadAndWrite() {
        String s = "this is a test string";
        byte[] content = s.getBytes();
        mmapUtil.writeContent(content);
        byte[] readContent = mmapUtil.readContent(0, content.length);
        System.out.println(new String(readContent));
    }
}
