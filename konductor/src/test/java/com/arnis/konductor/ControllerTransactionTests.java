package com.arnis.konductor;

import android.os.Bundle;

import com.arnis.konductor.changehandler.HorizontalChangeHandler;
import com.arnis.konductor.changehandler.VerticalChangeHandler;
import com.arnis.konductor.util.TestController;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ControllerTransactionTests {

    @Test
    public void testRouterSaveRestore() {
        RouterTransaction transaction = RouterTransaction.with(new TestController())
                .pushChangeHandler(new HorizontalChangeHandler())
                .popChangeHandler(new VerticalChangeHandler())
                .tag("Test Tag");

        Bundle bundle = transaction.saveInstanceState();

        RouterTransaction restoredTransaction = new RouterTransaction(bundle);

        assertEquals(transaction.controller.getClass(), restoredTransaction.controller.getClass());
        assertEquals(transaction.pushChangeHandler().getClass(), restoredTransaction.pushChangeHandler().getClass());
        assertEquals(transaction.popChangeHandler().getClass(), restoredTransaction.popChangeHandler().getClass());
        assertEquals(transaction.tag(), restoredTransaction.tag());
    }

}
