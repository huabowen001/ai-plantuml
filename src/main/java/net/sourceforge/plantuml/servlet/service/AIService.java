package net.sourceforge.plantuml.servlet.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.ContentType;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * AI服务类，用于将自然语言转换为PlantUML代码.
 */
public final class AIService {

    private static String apiUrl;
    private static String apiKey;
    private static String model;
    private static final Gson GSON = new Gson();

    static {
        try {
            Properties props = new Properties();
            InputStream is = AIService.class.getClassLoader()
                .getResourceAsStream("config.properties");
            if (is != null) {
                props.load(is);
                is.close();
            }

            apiUrl = props.getProperty("ai.api.url", "https://api.openai.com/v1/chat/completions");
            apiKey = props.getProperty("ai.api.key");
            model = props.getProperty("ai.model", "gpt-3.5-turbo");
        } catch (IOException e) {
            throw new RuntimeException("Failed to load AI configuration", e);
        }
    }

    /**
     * Private constructor to hide the implicit public one.
     */
    private AIService() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * 将自然语言描述转换为PlantUML代码.
     *
     * @param description 用户输入的自然语言描述
     * @return PlantUML代码
     * @throws IOException IO异常
     */
    public static String convertToPlantUML(String description) throws IOException {
        String systemPrompt = "你是一个PlantUML专家。用户会给你一段描述，请将其转换为PlantUML代码。"
                + "只返回PlantUML代码，不要有任何其他解释。代码必须以@startuml开始，以@enduml结束。"
                + "支持的图表类型包括：序列图(sequence)、类图(class)、活动图(activity)、用例图(usecase)、"
                + "组件图(component)、状态图(state)、对象图(object)、部署图(deployment)、时序图(timing)等。";

        String userPrompt = "请将以下描述转换为PlantUML代码：\n\n" + description;

        return callAI(systemPrompt, userPrompt);
    }

    /**
     * 优化已有的PlantUML代码.
     *
     * @param plantumlCode 现有的PlantUML代码
     * @param instruction 优化指令
     * @return 优化后的PlantUML代码
     * @throws IOException IO异常
     */
    public static String optimizePlantUML(String plantumlCode, String instruction) throws IOException {
        String systemPrompt = "你是一个PlantUML专家。用户会给你一段PlantUML代码和优化指令，"
                + "请根据指令优化代码。只返回优化后的PlantUML代码，不要有任何其他解释。";

        String userPrompt = "现有PlantUML代码：\n" + plantumlCode
                + "\n\n优化指令：" + instruction
                + "\n\n请返回优化后的PlantUML代码。";

        return callAI(systemPrompt, userPrompt);
    }

    /**
     * 调用AI API.
     *
     * @param systemPrompt 系统提示词
     * @param userPrompt 用户提示词
     * @return AI生成的响应
     * @throws IOException IO异常
     */
    private static String callAI(String systemPrompt, String userPrompt) throws IOException {
        if (apiKey == null || apiKey.isEmpty() || "your-api-key-here".equals(apiKey)) {
            // 如果没有配置API Key，返回一个示例PlantUML代码
            return generateSamplePlantUML(userPrompt);
        }

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(apiUrl);
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("Authorization", "Bearer " + apiKey);

            // 构建请求体
            JsonObject requestBody = new JsonObject();
            requestBody.addProperty("model", model);

            JsonArray messages = new JsonArray();
            JsonObject systemMessage = new JsonObject();
            systemMessage.addProperty("role", "system");
            systemMessage.addProperty("content", systemPrompt);
            messages.add(systemMessage);

            JsonObject userMessage = new JsonObject();
            userMessage.addProperty("role", "user");
            userMessage.addProperty("content", userPrompt);
            messages.add(userMessage);

            requestBody.add("messages", messages);
            requestBody.addProperty("temperature", 0.7);
            requestBody.addProperty("max_tokens", 2000);

            // 使用UTF-8编码创建请求实体
            StringEntity entity = new StringEntity(
                GSON.toJson(requestBody),
                ContentType.APPLICATION_JSON.withCharset(StandardCharsets.UTF_8)
            );
            httpPost.setEntity(entity);

            // 发送请求
            try (CloseableHttpResponse httpResponse = httpClient.execute(httpPost)) {
                String responseBody;
                try {
                    // 使用UTF-8编码读取响应
                    responseBody = EntityUtils.toString(httpResponse.getEntity(), StandardCharsets.UTF_8);
                } catch (org.apache.hc.core5.http.ParseException e) {
                    throw new IOException("Failed to parse response", e);
                }
                JsonObject responseJson = GSON.fromJson(responseBody, JsonObject.class);

                if (responseJson.has("choices") && responseJson.get("choices").getAsJsonArray().size() > 0) {
                    JsonObject firstChoice = responseJson.get("choices")
                        .getAsJsonArray().get(0).getAsJsonObject();
                    String content = firstChoice.get("message")
                        .getAsJsonObject().get("content").getAsString();

                    // 提取PlantUML代码
                    return extractPlantUMLCode(content);
                } else if (responseJson.has("error")) {
                    String errorMsg = responseJson.get("error")
                        .getAsJsonObject().get("message").getAsString();
                    throw new IOException("AI API Error: " + errorMsg);
                }
            }
        }

        throw new IOException("Failed to get response from AI");
    }

    /**
     * 从AI响应中提取PlantUML代码.
     *
     * @param content AI返回的内容
     * @return PlantUML代码
     */
    private static String extractPlantUMLCode(String content) {
        // 如果内容包含代码块标记，提取其中的内容
        if (content.contains("```plantuml")) {
            int start = content.indexOf("```plantuml") + 11;
            int end = content.indexOf("```", start);
            if (end > start) {
                return content.substring(start, end).trim();
            }
        } else if (content.contains("```")) {
            int start = content.indexOf("```") + 3;
            int end = content.indexOf("```", start);
            if (end > start) {
                return content.substring(start, end).trim();
            }
        }

        // 如果已经包含@startuml，直接返回
        if (content.contains("@startuml")) {
            return content.trim();
        }

        // 否则尝试添加标记
        return content.trim();
    }

    /**
     * 生成示例PlantUML代码（当没有配置API Key时使用）.
     *
     * @param userPrompt 用户提示
     * @return 示例PlantUML代码
     */
    private static String generateSamplePlantUML(String userPrompt) {
        // 根据关键词判断图表类型
        String prompt = userPrompt.toLowerCase();

        if (prompt.contains("序列图") || prompt.contains("sequence") || prompt.contains("交互")) {
            return "@startuml\n"
                + "actor 用户\n"
                + "participant \"前端\" as Frontend\n"
                + "participant \"后端\" as Backend\n"
                + "database \"数据库\" as DB\n\n"
                + "用户 -> Frontend: 发起请求\n"
                + "Frontend -> Backend: API调用\n"
                + "Backend -> DB: 查询数据\n"
                + "DB --> Backend: 返回结果\n"
                + "Backend --> Frontend: 返回响应\n"
                + "Frontend --> 用户: 显示结果\n"
                + "@enduml";
        } else if (prompt.contains("类图") || prompt.contains("class")) {
            return "@startuml\n"
                + "class User {\n"
                + "  - id: Long\n"
                + "  - username: String\n"
                + "  - email: String\n"
                + "  + login()\n"
                + "  + logout()\n"
                + "}\n\n"
                + "class Order {\n"
                + "  - id: Long\n"
                + "  - userId: Long\n"
                + "  - amount: Double\n"
                + "  + create()\n"
                + "  + cancel()\n"
                + "}\n\n"
                + "User \"1\" -- \"*\" Order\n"
                + "@enduml";
        } else if (prompt.contains("活动图") || prompt.contains("activity") || prompt.contains("流程")) {
            return "@startuml\n"
                + "start\n"
                + ":用户输入信息;\n"
                + "if (验证通过?) then (是)\n"
                + "  :处理数据;\n"
                + "  :保存到数据库;\n"
                + "  :返回成功;\n"
                + "else (否)\n"
                + "  :返回错误信息;\n"
                + "endif\n"
                + "stop\n"
                + "@enduml";
        } else {
            return "@startuml\n"
                + "actor 用户\n"
                + "participant 系统\n\n"
                + "用户 -> 系统: 请求\n"
                + "系统 --> 用户: 响应\n"
                + "@enduml";
        }
    }
}

