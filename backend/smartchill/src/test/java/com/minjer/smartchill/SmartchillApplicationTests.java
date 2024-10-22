package com.minjer.smartchill;

import com.minjer.smartchill.entity.dto.Transaction;
import com.minjer.smartchill.mapper.TransactionMapper;
import com.minjer.smartchill.utils.ImageUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;

@SpringBootTest
class SmartchillApplicationTests {
    @Autowired
    private ImageUtil imageUtil;

    @Autowired
    private TransactionMapper transactionMapper;
    @Test
    void testTransactionMapper() {
        ArrayList<Transaction> drink = transactionMapper.getDrinkOnSaleByFridgeAndOrder(null, true);
        System.out.println(drink);

        System.out.println("------------------------------------------------------");
        System.out.println(transactionMapper.getDrinkOnSale());
    }

}
