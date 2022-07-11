package com.freelancerk.util;

import com.freelancerk.domain.AligoKakaoMessageTemplate;

import java.util.Map;

public class FrkMessageBuilder {

    public static String buildMessage(AligoKakaoMessageTemplate.Code code, String message, Map<String, Object> variables) {

        switch (code) {
            case TA_3172:
            case TA_3192:
            case TA_3198:
                return String.format(message, variables.get("clientName"));
            case TA_3173:
            case TA_3193:
                return String.format(message, variables.get("freelancerName"), variables.get("freelancerName"));
            case TA_3174:
            case TA_3175:
            case TA_3194:
            case TA_3195:
            case TA_3196:
            case TA_3197:
            case TA_3200:
            case TB_0735:
            case TB_1311:
            case TB_1312:
                return message;
            case TA_3177:
            case TA_3178:
            case TA_3190:
            case TA_3191:
            case TB_3000:
            case TB_3001:
                return String.format(message, variables.get("freelancerName"));
            case TA_3202:
            case TA_3203:
            case TA_3204:
            case TA_3205:
                return String.format(message, variables.get("projectName"), variables.get("freelancerName"));
            case TA_3180:
            case TA_3181:
                return String.format(message, variables.get("clientName"), variables.get("projectName"), variables.get("freelancerName"));
            case TA_3182:
                return String.format(message, variables.get("clientName"), variables.get("projectName"));
            case TA_3183:
            case TA_3189:
                return String.format(message, variables.get("freelancerName"), variables.get("projectName"), variables.get("cancelDividend"));
            case TA_3184:
            case TA_3185:
            case TA_3187:
            case TA_3893:
                return String.format(message, variables.get("postingStartAt"), variables.get("clientName"), variables.get("projectName"), variables.get("postingEndAt"), variables.get("numberOfApplicants"));
            case TA_3186:
            case TA_3188:
                return String.format(message, variables.get("postingStartAt"), variables.get("clientName"), variables.get("projectName"), variables.get("depositMoney"), variables.get("totalPrize"));
            case TB_1398:
                return String.format(message, variables.get("postingStartAt"), variables.get("clientName"), variables.get("projectName"));
            case TA_3199:
                return String.format(message, variables.get("clientName"), variables.get("freelancerName"));
            case TA_3201:
                return String.format(message, variables.get("projectName"));
            case TA_3206:
                return String.format(message, variables.get("freelancerName"), variables.get("pickMeUpName"));
            case TA_3207:
                return String.format(message, variables.get("endAt"), variables.get("freelancerName"), variables.get("pickMeUpName"));
            case TA_3208:
                return String.format(message, variables.get("endAt"), variables.get("freelancerName"), variables.get("pickMeUpName"), variables.get("optionName"));
            case TA_3176:
                return String.format(message, variables.get("freelancerName"), variables.get("projectName"));
            case TA_9935:
                return String.format(message, variables.get("code"));
        }
        return null;
    }
}
