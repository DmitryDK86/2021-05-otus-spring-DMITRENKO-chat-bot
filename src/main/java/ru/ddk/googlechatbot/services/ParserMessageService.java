package ru.ddk.googlechatbot.services;

import org.javatuples.Pair;

import java.util.HashMap;

public interface ParserMessageService {
    Pair<String, HashMap<String, String>> parseMessage(String input);
}
