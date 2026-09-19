package com.meridiantrust.sentinel.controller;

import com.meridiantrust.sentinel.model.Edge;
import com.meridiantrust.sentinel.model.GraphData;
import com.meridiantrust.sentinel.model.Node;
import com.meridiantrust.sentinel.model.Transaction;
import com.meridiantrust.sentinel.repository.TransactionRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class NetworkController {

    private final TransactionRepository transactionRepository;

    public NetworkController(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @GetMapping("/network")
    public String network() {
        return "network";
    }

    @GetMapping("/api/v1/network/data")
    @ResponseBody
    public GraphData getNetworkData() {
        List<Transaction> transactions = transactionRepository.findAll();
        Map<String, Node> nodeMap = new HashMap<>();
        List<Edge> edges = new ArrayList<>();

        for (Transaction tx : transactions) {
            String fromId = tx.accountId();
            String toId = tx.counterpartyId();
            
            if (fromId == null || toId == null) continue;

            // Ensure nodes exist
            nodeMap.putIfAbsent(fromId, new Node(fromId, "Acc: " + fromId, "account"));
            nodeMap.putIfAbsent(toId, new Node(toId, "Acc: " + toId, "account"));

            // Determine direction based on OUTBOUND/INBOUND
            // If INBOUND, from is counterparty, to is account
            String actualFrom = tx.direction().equalsIgnoreCase("INBOUND") ? toId : fromId;
            String actualTo = tx.direction().equalsIgnoreCase("INBOUND") ? fromId : toId;

            String label = tx.amount() + " " + tx.currency();
            edges.add(new Edge(actualFrom, actualTo, label, tx.amount()));
        }

        return new GraphData(new ArrayList<>(nodeMap.values()), edges);
    }
}
