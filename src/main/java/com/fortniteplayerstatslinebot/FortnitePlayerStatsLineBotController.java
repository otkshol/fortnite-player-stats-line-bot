package com.fortniteplayerstatslinebot;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fortniteplayerstatslinebot.models.api.FortnitePlayerStats;
import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.*;
import com.linecorp.bot.model.response.BotApiResponse;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.DataInput;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import static java.util.Collections.singletonList;

@Slf4j
@LineMessageHandler
public class FortnitePlayerStatsLineBotController {

    @Autowired
    private LineMessagingClient lineMessagingClient;

    @Autowired
    RestTemplate restTemplate;

    @Value("${trn-api-key}")
    private String TRN_API_KEY;

    @EventMapping
    public void handleTextMessageEvent(MessageEvent<TextMessageContent> event) throws Exception {

        // TODO LINEのユーザーIDを取得して一般用とプライベート用の振る舞いを分岐させる（外に対象ID一覧を置いておいて、Streamを使用して絞り込む。）
        // 一般用から着手
        TextMessageContent message = event.getMessage();
        handleTextContent(event.getReplyToken(), event, message);
    }

    private void handleTextContent(String replyToken, Event event, TextMessageContent content)
            throws Exception {
        final String text = content.getText();

        log.info("Got text message from replyToken:{}: text:{} emojis:{}", replyToken, text, content.getEmojis());
        lineMessagingClient.getBotInfo();

        // TODO Streamを使ったリファクタリング検討(if分岐が多すぎる...)
        if(isVictoryRoyalAsked(text)){
            String accountName = getAccountName(text);
            FortnitePlayerStats fortnitePlayerStats = executeFortnitetrackerApi(accountName);
            if (Objects.isNull(fortnitePlayerStats.lifeTimeStats)){
                this.replyText(replyToken, "指定のアカウントが見つかりませんでした。例を参考にもう一度入力をお願いします。\n\n例: (account名)のビクロイ数を教えて");
            }

            if (isSoloStatsAsked(text)){
                String totalSoloVicroyNumber =  fortnitePlayerStats.stats.p2.top1.value;
                this.replyText(replyToken, accountName + "さんのソロビクロイ数は\n" + totalSoloVicroyNumber + "回です。");
            } else if (isDuoStatsAsked(text)){
                String totalDuoVicroyNumber =  fortnitePlayerStats.stats.p10.top1.value;
                this.replyText(replyToken, accountName + "さんのデュオビクロイ数は\n" + totalDuoVicroyNumber + "回です。");
            } else if (isTrioStatsAsked(text)){
                String totalTrioVicroyNumber =  fortnitePlayerStats.stats.trios.top1.value;
                this.replyText(replyToken, accountName + "さんのトリオビクロイ数は\n" + totalTrioVicroyNumber + "回です。");
            } else if (isSquadStatsAsked(text)){
                String totalSquadVicroyNumber =  fortnitePlayerStats.stats.p9.top1.value;
                this.replyText(replyToken, accountName + "さんのスクワッドビクロイ数は\n" + totalSquadVicroyNumber + "回です。");
            } else {
                String totalVicroyNumber = fortnitePlayerStats.lifeTimeStats.get(8).get("value");
                this.replyText(replyToken, accountName + "さんのビクロイ数は\n" + totalVicroyNumber + "回です。");
            }
        }
        if(isKillRateAsked(text)){
            String accountName = getAccountName(text);
            FortnitePlayerStats fortnitePlayerStats = executeFortnitetrackerApi(accountName);
            if (Objects.isNull(fortnitePlayerStats.lifeTimeStats)){
                this.replyText(replyToken, "指定のアカウントが見つかりませんでした。例を参考にもう一度入力をお願いします。\n\n例: (account名)のキルレートを教えて");
            }
            if (isSoloStatsAsked(text)){
                String totalSoloKillRate =  fortnitePlayerStats.stats.p2.kd.value;
                this.replyText(replyToken, accountName + "さんのソロキルレートは\n" + totalSoloKillRate + "回です。");
            } else if (isDuoStatsAsked(text)){
                String totalDuoKillRate =  fortnitePlayerStats.stats.p10.kd.value;
                this.replyText(replyToken, accountName + "さんのデュオキルレートは\n" + totalDuoKillRate + "回です。");
            } else if (isTrioStatsAsked(text)){
                String totalTrioKillRate =  fortnitePlayerStats.stats.trios.kd.value;
                this.replyText(replyToken, accountName + "さんのトリオキルレートは\n" + totalTrioKillRate + "回です。");
            } else if (isSquadStatsAsked(text)){
                String totalDuoKillRate =  fortnitePlayerStats.stats.p10.kd.value;
                this.replyText(replyToken, accountName + "さんのデュオキルレートは\n" + totalDuoKillRate + "回です。");

            } else {
                String totalKillRate = fortnitePlayerStats.lifeTimeStats.get(11).get("value");
                this.replyText(replyToken, accountName + "さんのキルレートは\n" + totalKillRate + "です。");
            }
        }
    }

    private FortnitePlayerStats executeFortnitetrackerApi(String accountName) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("TRN-Api-Key",TRN_API_KEY);
        HttpEntity<FortnitePlayerStats> entity = new HttpEntity<>(headers);
        ResponseEntity<String> responseJson = restTemplate.exchange("https://api.fortnitetracker.com/v1/profile/all/" + accountName, HttpMethod.GET, entity, String.class);
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(responseJson.getBody(), FortnitePlayerStats.class);
    }

    private String getAccountName(String text) {
        if (text.contains("の")){
            return text.split("の")[0];
        } else if(text.contains(" ")){
            return text.split(" ")[0];
        } else if (text.contains("　")){
            return text.split("　")[0];
        } else if (text.contains("ビクロイ数")) {
         return text.split("ビクロイ数")[0];
        } else if (text.contains("キルレ")) {
            return text.split("キルレ")[0];
        } else throw new RuntimeException();
    }

    private boolean isVictoryRoyalAsked(String text) {
        return text.contains("ビクロイ数");
    }

    private boolean isKillRateAsked(String text) {
        return text.contains("キルレ");
    }

    private boolean isSoloStatsAsked(String text) {
        return text.contains("ソロ") || text.contains("そろ") || text.contains("solo");
    }

    private boolean isDuoStatsAsked(String text) {
        return text.contains("デュオ") || text.contains("でゅお") || text.contains("duo");
    }

    private boolean isTrioStatsAsked(String text) {
        return text.contains("トリオ") || text.contains("とりお") || text.contains("trio");
    }

    private boolean isSquadStatsAsked(String text) {
        return text.contains("スクワッド") || text.contains("すくわっど") || text.contains("squad");
    }

    private void replyText(@NonNull String replyToken, @NonNull String message) {
        if (replyToken.isEmpty()) {
            throw new IllegalArgumentException("replyToken must not be empty");
        }
        try {
            BotApiResponse apiResponse = lineMessagingClient
                    .replyMessage(new ReplyMessage(replyToken, new TextMessage(message), false))
                    .get();
            log.info("Sent messages: {}", apiResponse);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
