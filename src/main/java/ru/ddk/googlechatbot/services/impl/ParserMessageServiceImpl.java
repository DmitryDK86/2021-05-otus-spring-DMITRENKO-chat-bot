package ru.ddk.googlechatbot.services.impl;

import org.javatuples.Pair;
import org.springframework.stereotype.Service;
import ru.ddk.googlechatbot.services.ParserMessageService;

import java.util.HashMap;
import java.util.regex.Pattern;


@Service
public class ParserMessageServiceImpl implements ParserMessageService {
    @Override

    public Pair<String, HashMap<String, String>> parseMessage(String input) {

        if (Pattern.compile("^/help").matcher(input).matches())
        {
            return Pair.with("help", new HashMap<>(){{ put("value",
                    String.format("%s", "Я умею копировать обьекты, мне можно написать следующие вариации команд: " +
                    " C:\\Test\\source\\test.txt -> C:\\Test\\target "));}});
        }
        else if(Pattern.compile("[\\\\\\w\\s\\.:]+->[\\\\\\w\\s\\.:]+").matcher(input).matches())
        {
            return Pair.with("copy", new HashMap<>(){{
                put("value", String.format("%s", "Бегу копировать"));
                put("0", "C:\\Test\\source\\test.txt,C:\\Test\\target");
                put("1", "C:\\Test\\source\\test.txt,C:\\Test\\target1");
                put("2", "C:\\Test\\source\\test.txt,C:\\Test\\target2");
                put("3", "C:\\Test\\source\\test.txt,C:\\Test\\target3");
            }});
        }

        return Pair.with("error", new HashMap<>(){{ put("value", String.format("Что-то пошло не так и ваше сообщение ('%s') не прошло валидацию, напишите мне еще раз," +
                " через команду /help можно посмотреть, что я умею ", input)); }});
    }
}
