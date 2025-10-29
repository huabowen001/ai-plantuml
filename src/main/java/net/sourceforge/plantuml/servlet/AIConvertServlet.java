package net.sourceforge.plantuml.servlet;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.sourceforge.plantuml.servlet.dao.DiagramHistoryDao;
import net.sourceforge.plantuml.servlet.dao.DiagramVersionDao;
import net.sourceforge.plantuml.servlet.model.DiagramHistory;
import net.sourceforge.plantuml.servlet.model.DiagramVersion;
import net.sourceforge.plantuml.servlet.service.AIService;
import net.sourceforge.plantuml.servlet.util.JWTUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * AI文本转PlantUML Servlet.
 */
public class AIConvertServlet extends HttpServlet {

    private final DiagramHistoryDao historyDao = new DiagramHistoryDao();
    private final DiagramVersionDao versionDao = new DiagramVersionDao();
    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // 获取并验证Token
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            JsonObject result = new JsonObject();
            result.addProperty("success", false);
            result.addProperty("message", "未授权访问，请先登录");
            response.getWriter().print(gson.toJson(result));
            return;
        }

        String token = authHeader.substring(7);
        Long userId = JWTUtil.getUserIdFromToken(token);

        if (userId == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            JsonObject result = new JsonObject();
            result.addProperty("success", false);
            result.addProperty("message", "Token无效或已过期");
            response.getWriter().print(gson.toJson(result));
            return;
        }

        String path = request.getServletPath();

        if ("/api/ai/convert".equals(path)) {
            handleConvert(request, response, userId);
        } else if ("/api/ai/optimize".equals(path)) {
            handleOptimize(request, response, userId);
        }
    }

    /**
     * 处理文本转PlantUML.
     */
    private void handleConvert(HttpServletRequest request, HttpServletResponse response, Long userId)
            throws IOException {
        // 设置请求编码为UTF-8
        request.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();
        JsonObject result = new JsonObject();

        try {
            // 读取请求体
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            JsonObject requestData = gson.fromJson(sb.toString(), JsonObject.class);
            String description = requestData.get("description").getAsString();
            String title = requestData.has("title")
                ? requestData.get("title").getAsString() : null;
            boolean saveHistory = requestData.has("saveHistory")
                && requestData.get("saveHistory").getAsBoolean();

            if (description == null || description.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                result.addProperty("success", false);
                result.addProperty("message", "描述不能为空");
                out.print(gson.toJson(result));
                return;
            }

            // 调用AI服务转换
            String plantumlCode = AIService.convertToPlantUML(description);

            // 保存历史记录
            Long historyId = null;
            if (saveHistory) {
                DiagramHistory history = new DiagramHistory(userId, description, plantumlCode);
                String finalTitle = title != null ? title : "未命名图表";
                history.setTitle(finalTitle);
                historyId = historyDao.create(history);

                // 创建初始版本（版本1）
                if (historyId != null) {
                    createVersion(historyId, plantumlCode, finalTitle, "AI生成初始版本", userId);
                }
            }

            result.addProperty("success", true);
            result.addProperty("plantumlCode", plantumlCode);
            if (historyId != null) {
                result.addProperty("historyId", historyId);
            }

            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            result.addProperty("success", false);
            result.addProperty("message", "转换失败：" + e.getMessage());
        }

        out.print(gson.toJson(result));
    }

    /**
     * 处理PlantUML优化.
     */
    private void handleOptimize(HttpServletRequest request, HttpServletResponse response, Long userId)
            throws IOException {
        // 设置请求编码为UTF-8
        request.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();
        JsonObject result = new JsonObject();

        try {
            // 读取请求体
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            JsonObject requestData = gson.fromJson(sb.toString(), JsonObject.class);
            String plantumlCode = requestData.get("plantumlCode").getAsString();
            String instruction = requestData.get("instruction").getAsString();

            if (plantumlCode == null || plantumlCode.trim().isEmpty()
                    || instruction == null || instruction.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                result.addProperty("success", false);
                result.addProperty("message", "PlantUML代码和优化指令不能为空");
                out.print(gson.toJson(result));
                return;
            }

            // 调用AI服务优化
            String optimizedCode = AIService.optimizePlantUML(plantumlCode, instruction);

            result.addProperty("success", true);
            result.addProperty("plantumlCode", optimizedCode);

            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            result.addProperty("success", false);
            result.addProperty("message", "优化失败：" + e.getMessage());
        }

        out.print(gson.toJson(result));
    }

    /**
     * 创建版本（保存历史记录时调用）.
     */
    private void createVersion(Long historyId, String plantumlCode, String title,
            String changeDescription, Long userId) {
        try {
            // 获取当前最新版本号
            Integer latestVersion = versionDao.getLatestVersionNumber(historyId);
            int newVersionNumber = (latestVersion != null ? latestVersion : 0) + 1;

            // 创建新版本
            DiagramVersion version = new DiagramVersion(historyId, newVersionNumber, plantumlCode);

            // 生成版本标题：版本号 + 变更描述
            String versionTitle = "版本 " + newVersionNumber;
            if (changeDescription != null && !changeDescription.isEmpty()) {
                versionTitle += " - " + (changeDescription.length() > 50
                        ? changeDescription.substring(0, 50) + "..."
                        : changeDescription);
            }

            version.setTitle(versionTitle);
            version.setChangeDescription(changeDescription);
            version.setCreatedBy(userId);

            versionDao.create(version);

            // 保持最多20个版本
            versionDao.deleteOldestVersions(historyId, 20);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

