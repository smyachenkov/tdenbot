package com.tden.chatevent;

import com.google.cloud.speech.v1.RecognitionAudio;
import com.google.cloud.speech.v1.RecognitionConfig;
import com.google.cloud.speech.v1.RecognizeResponse;
import com.google.cloud.speech.v1.SpeechRecognitionResult;
import com.google.gson.Gson;
import com.google.protobuf.ByteString;
import com.tden.TDGameBot;
import com.tden.command.encounter.EnterCodeCommand;
import com.tden.encounterengine.ActivityStatus;
import com.tden.encounterengine.EncounterSession;
import com.tden.utilities.Responses;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Voice;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.text.MessageFormat;
import java.util.stream.Stream;

/**
 * Created by Stanislav Myachenkov on 16.05.2017.
 */
@Slf4j
public class VoiceEvent extends ChatEvent {

    private static final int MIN_CODE_LENGTH = 4;
    private static final int MAX_CODE_LENGTH = 20;

    public VoiceEvent(TDGameBot bot, Message message) {
        super(bot, ChatEventType.VOICE, message, true);
    }

    @Override
    public int processEvent() {

        try {

            SendMessage reply = new SendMessage();
            Message msg = this.getMessage();
            Voice voice = msg.getVoice();

            reply.setChatId(message.getChatId());

            if (voice.getDuration() > 120) {
                reply.setText(Responses.VOICE_TOO_LONG.toString());
                reply.enableMarkdown(true);
                reply.setReplyToMessageId(msg.getMessageId());
                this.bot.sendMessage(reply);
            } else {
                String recognizedMessage = getTextFromVoice(message.getVoice()).toLowerCase();
                reply.setText(recognizedMessage);
                reply.setReplyToMessageId(msg.getMessageId());
                this.bot.sendMessage(reply);

                if (!recognizedMessage.equals(Responses.ERROR_IN_VOICE_REC.toString())) {
                    //if we recognized text and the game is active - enter it

                    if (bot.getEncounterSession().getSessionInfo().getActivityStatus() == ActivityStatus.ACTIVE) {

                        String code = recognizedMessage.toLowerCase()
                                                        .replaceAll("\\s+", "")
                                                        .replaceAll("пробел", " ");

                        if ((code.length() >= MIN_CODE_LENGTH && code.length() <= MAX_CODE_LENGTH) || (Stream.of("вот", "кот", "код").anyMatch(recognizedMessage::startsWith))) {

                            if ((Stream.of("вот", "кот", "код").anyMatch(recognizedMessage::startsWith)) && code.length() >= MIN_CODE_LENGTH) {
                                code = code.substring(3, code.length());
                            }

                            //without reply on purpose, too much notifications for one message
                            EnterCodeCommand comm = new EnterCodeCommand(message, bot, code, false);
                            comm.processCommand();
                        }
                    }
                }
            }



        }catch (TelegramApiException e){
            log.error(String.format("Error sending response to voice message [ %s ] from user [ %s ]", message, message.getFrom()));
        }

        return 0;
    }

    /* GSON Entities */

    @Getter
    private class GetFile{
        private Result result;
        private String ok;
    }
    @Getter
    private class Result {
        private String file_size;
        private String file_path;
        private String file_id;
    }

    private String getTextFromVoice(Voice v) {

        String res = "";

        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()){

            String getFileReqStr = MessageFormat.format("https://api.telegram.org/bot{0}/getFile?file_id={1}", bot.getBotToken(), v.getFileId());
            HttpGet getFileReq = new HttpGet(getFileReqStr);
            HttpResponse getFileR = httpClient.execute(getFileReq);

            Gson gson = new Gson();
            GetFile gf = gson.fromJson(EntityUtils.toString(getFileR.getEntity()), GetFile.class);

            String filePath = MessageFormat.format("https://api.telegram.org/file/bot{0}/{1}", bot.getBotToken(), gf.getResult().file_path);
            byte[] bytes = Jsoup.connect(filePath).ignoreContentType(true).execute().bodyAsBytes();
            ByteString byteString = ByteString.copyFrom(bytes);

            // Builds the sync recognize request
            RecognitionConfig config = RecognitionConfig.newBuilder()
                    .setEncoding(RecognitionConfig.AudioEncoding.OGG_OPUS)
                    .setSampleRateHertz(16000)
                    .setLanguageCode("ru-RU")
                    .build();

            RecognitionAudio audio = RecognitionAudio.newBuilder()
                    .setContent(byteString)
                    .build();

            RecognizeResponse response = bot.getSpeechClient().recognize(config, audio);

            SpeechRecognitionResult speechRecognitionResult = response.getResultsList().get(0);
            String transcript = speechRecognitionResult.getAlternativesList().get(0).getTranscript();
            res = transcript;
        } catch (Exception e){
            log.error(String.format("Error converting voice to text in message [ %s ] from user [ %s ]", message, message.getFrom()));
            res = Responses.ERROR_IN_VOICE_REC.toString();
        }

        return res;
    }
}
