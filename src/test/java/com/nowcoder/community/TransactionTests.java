package com.nowcoder.community;

import com.nowcoder.community.service.AService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class TransactionTests {

    @Autowired
    private AService aService;

    @Test
    public void testSave1(){
        Object obj=aService.save1();
        System.out.println(obj);
    }

    @Test
    public void testSave2(){
        Object obj=aService.save2();
        System.out.println(obj);
    }
}
