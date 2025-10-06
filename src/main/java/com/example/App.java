package com.example;

import org.stellar.sdk.KeyPair;
import org.stellar.sdk.Network;
import org.stellar.sdk.SorobanServer;
import org.stellar.sdk.Transaction;
import org.stellar.sdk.TransactionBuilder;
import org.stellar.sdk.TransactionBuilderAccount;
import org.stellar.sdk.xdr.InvokeHostFunctionOp;
import org.stellar.sdk.operations.InvokeHostFunctionOperation;
import org.stellar.sdk.exception.PrepareTransactionException;
import org.stellar.sdk.exception.NetworkException;
import org.stellar.sdk.responses.sorobanrpc.SendTransactionResponse;

public class App {

    public static void main(String[] args) {
        SorobanServer server = new SorobanServer("https://soroban-testnet.stellar.org");
        try {
            // Replace with your source account's public key
            String sourcePublicKey = "GDKOYSOKU4TNHGGR763NOL6VNY52WKJL3XI33TOUKDNF4AZN74TE3XUA";
            // Replace with your source account's secret seed
            String sourceSecretSeed = "SDREE3T3U67AV6HFBS3IJWDMPTWZULHZE7ZHXWIGEYKZUUXC7TVVRYXM";
            KeyPair sourceKeyPair = KeyPair.fromSecretSeed(sourceSecretSeed);

            // Decode the source account's public key to TransactionBuilderAccount
            TransactionBuilderAccount account = server.getAccount(sourceKeyPair.getAccountId());
            
            // Define the contract ID and function to invoke
            String contractId = "CBSJSC5BEGCETRYUHFDNLEX5DN3ITRWGR6BOYAW4UGHNEGV2WS6ICSUK";
            String functionName = "increment";

            InvokeHostFunctionOperation operation =
                InvokeHostFunctionOperation.invokeContractFunctionOperationBuilder(
                  contractId, 
                  functionName, 
                  null
                )
                .build();

            Transaction unpreparedTransaction =
                new TransactionBuilder(account, Network.TESTNET)
                    .setBaseFee(Transaction.MIN_BASE_FEE)
                    .addOperation(operation)
                    .setTimeout(300)
                    .build();

            Transaction transaction;
                try {
                    transaction = server.prepareTransaction(unpreparedTransaction);
                } catch (PrepareTransactionException e) {
                    throw new RuntimeException("Prepare transaction failed", e);
                } catch (NetworkException e) {
                    throw new RuntimeException("Network error", e);
                }

            // Sign the transaction
            transaction.sign(sourceKeyPair);

            // Send the transaction using the SorobanServer
            SendTransactionResponse response = server.sendTransaction(transaction);

            System.out.println("Status: " + response.getStatus());
            System.out.println("Transaction Hash: " + response.getHash());
            System.out.println("Latest Ledger: " + response.getLatestLedger());
            System.out.println("Ledger Close Time: " + response.getLatestLedgerCloseTime());
        } catch (Exception e) {
            System.err.println("An error has occurred:");
            e.printStackTrace();
        }
    }
}




