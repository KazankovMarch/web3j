package org.web3j.protocol.scenarios;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.web3j.EVMTest;
import org.web3j.NodeType;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.utils.Convert;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;


/**
 * Sign transaction on Geth side using unlocked account without (retrieving private keys)
 */
@EVMTest(type = NodeType.GETH)
public class SignAndSendTransactionIT extends Scenario {

    @BeforeAll
    public static void beforeAll(Web3j web3j) {
        Scenario.web3j = web3j;
    }

    @Test
    public void testTransferEther() throws Exception {

        BigInteger nonce = getNonce(ALICE.getAddress());
        BigInteger value = Convert.toWei("0.5", Convert.Unit.ETHER).toBigInteger();
        Transaction transaction = new Transaction(ALICE.getAddress(), nonce, GAS_PRICE, GAS_LIMIT, BOB.getAddress(),
                value, null, 2018L, null, null);

        org.web3j.protocol.core.methods.response.Transaction signedTransaction =
                web3j.ethSignTransaction(transaction).send().getSignedTransactionData();
        EthSendTransaction ethSendTransaction =
                web3j.ethSendRawTransaction(signedTransaction.getRaw())
                        .sendAsync()
                        .get();

        String transactionHash = ethSendTransaction.getTransactionHash();

        assertFalse(transactionHash.isEmpty());

        TransactionReceipt transactionReceipt = waitForTransactionReceipt(transactionHash);

        assertEquals(transactionReceipt.getTransactionHash(), (transactionHash));
    }

}
