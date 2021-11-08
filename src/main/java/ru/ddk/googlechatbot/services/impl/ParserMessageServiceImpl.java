package ru.ddk.googlechatbot.services.impl;

import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.ddk.googlechatbot.services.ParserMessageService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class ParserMessageServiceImpl implements ParserMessageService {

    private static final Logger logger = LoggerFactory.getLogger(ParserMessageServiceImpl.class.getName());

    @Override
    public Pair<String, HashMap<String, String>> parseMessage(String input) {

        if (Pattern.compile("^/help").matcher(input).matches())
        {
            return Pair.with("help", new HashMap<>(){{ put("value",
                    String.format("%s", "Я умею копировать обьекты, мне можно написать следующие вариации команд: \n" +
                            "1. /path1/path2/cdm/samplesubject/vr=1/dt=2021-06-01 -> /path3/target \n" +
                            "2. /path1/path2/cdm/samplesubject/vr=1/dt=2021-06-01" +
                            " /path4/path5/cdm/samplesubject/vr=2/dt=2021-06-01 -> /path3/target"));}});
        }
        else if(input.contains("->"))
        {
            List<String> rezultConvert = convertToPath(input);
            if (!rezultConvert.isEmpty())
            {
                HashMap<String, String> copyMap = new HashMap<>(){{
                    put("value", String.format("%s", "Бегу копировать"));
                }};
                rezultConvert.forEach(convertPath -> copyMap.put(String.valueOf(copyMap.size()), convertPath));
                return Pair.with("copy", copyMap);
            }

        }

        return Pair.with("error", new HashMap<>(){{ put("value", String.format("Что-то пошло не так и ваше сообщение ('%s') не прошло валидацию, напишите мне еще раз," +
                " через команду /help можно посмотреть, что я умею ", input)); }});
    }

    private List<String> convertToPath(String input)
    {
        List<String> rezult = new ArrayList<>();

        try {
            if (input.contains("->")) {
                input = input.replace("->", " -> ");
                input = input.replaceAll("[\\s]+", " ");
                List<String> splitInput = List.of(input.split("[\\s]"));
                int spPos = splitInput.indexOf("->");
                List<String> sourceFile = splitInput.stream().limit(spPos).collect(Collectors.toList());

                sourceFile
                        .stream().filter(this::isValidPath)
                        .forEach(path ->
                        rezult.add(String.format("%s,%s%s", path, splitInput.get(spPos + 1), path).replace("//", "/"))
                );
            }
        }catch (Exception e)
        {
            logger.error(e.toString());
        }
        return rezult;
    }

    private boolean isValidPath(String path)
    {
        return Pattern.compile("(^\\/[\\/\\w]+(=)).*").matcher(path).matches();
    }
}
