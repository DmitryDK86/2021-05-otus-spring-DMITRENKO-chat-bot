package ru.ddk.googlechatbot.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import ru.ddk.googlechatbot.services.impl.ParserMessageServiceImpl;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Тест парсера входящих сообщений")
public class ParserMessageServiceImplTest {

    @Autowired
    private ParserMessageServiceImpl parserMessageService = Mockito.spy(ParserMessageServiceImpl.class);

    @DisplayName("Валидные сообщений для команды копирования")
    @Test
    public void validInputPatchToCopy()
    {
        String input = "/path1/path2/cdm/samplesubject/vr=1/dt=2021-06-01  \n" +
                " /path1/path2/cdm/samplesubject/vr=2/dt=2021-06-01  \n" +
                " /path1/path2/cdm/weightcubeobjectweightvalue/vr=3/pk=2021-06-01    \n" +
                " /path1/path2/cdm/dailysubjectpropertyvalue/vr=4/rd=2021-06-01  \n" +
                " /path1/path2/cdm/dailysubjectpropertyvalue/vr=5/rd=2021-06-01  \n" +
                " /path1/path2/dds/property/vr=6   ->  /path1/sb/12432/";

        assertThat(parserMessageService.parseMessage(input).getValue1().size()).isEqualTo(7);
    }

    @DisplayName("Невалидные сообщений для команды копирования")
    @Test
    public void notValidInputPatchToCopy()
    {
        String input = " /path1 path2/cdm/samplesubject/vr=1/dt=2021-06-01  \n" +
                    " path1/path2/cdm/samplesubject/vr=2/dt=2021-06-01  \n" +
                    " /path1/path2/cdm/weightcubeobjectweightvalue/vr    \n" +
                    " /path1/path2/cdm/dailysubjectpropertyvalue/vr=4/rd=2021-06-01  \n" +
                    " /path1/path2/cdm/dailysubjectpropertyvalue/vr=5/rd=2021-06-01  \n" +
                    " /path1/path2/dds/property/vr   ->  /path1/sb/12432/";
        assertThat(parserMessageService.parseMessage(input).getValue1().size()).isEqualTo(3);
    }

}