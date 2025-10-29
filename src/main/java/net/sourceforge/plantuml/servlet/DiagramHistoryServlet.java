package net.sourceforge.plantuml.servlet;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.sourceforge.plantuml.servlet.dao.DiagramHistoryDao;
import net.sourceforge.plantuml.servlet.dao.DiagramVersionDao;
import net.sourceforge.plantuml.servlet.model.DiagramHistory;
import net.sourceforge.plantuml.servlet.model.DiagramVersion;
import net.sourceforge.plantuml.servlet.util.JWTUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * 图表历史记录Servlet.
 */
public class DiagramHistoryServlet extends HttpServlet {

    private final DiagramHistoryDao historyDao = new DiagramHistoryDao();
    private final DiagramVersionDao versionDao = new DiagramVersionDao();
    private final Gson gson = new Gson();

    /**
     * 验证Token并返回用户ID.
     */
    private Long verifyToken(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            JsonObject result = new JsonObject();
            result.addProperty("success", false);
            result.addProperty("message", "未授权访问，请先登录");
            response.getWriter().print(gson.toJson(result));
            return null;
        }

        String token = authHeader.substring(7);
        Long userId = JWTUtil.getUserIdFromToken(token);

        if (userId == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            JsonObject result = new JsonObject();
            result.addProperty("success", false);
            result.addProperty("message", "Token无效或已过期");
            response.getWriter().print(gson.toJson(result));
            return null;
        }

        return userId;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Long userId = verifyToken(request, response);
        if (userId == null) {
            return;
        }

        String pathInfo = request.getPathInfo();

        if (pathInfo == null || "/".equals(pathInfo)) {
            // 获取历史记录列表
            handleList(request, response, userId);
        } else if (pathInfo.startsWith("/favorites")) {
            // 获取收藏列表
            handleFavorites(response, userId);
        } else if (pathInfo.matches("/\\d+/versions/?")) {
            // 获取历史记录的版本列表: /api/history/{id}/versions
            String[] parts = pathInfo.split("/");
            try {
                Long historyId = Long.parseLong(parts[1]);
                handleGetVersions(response, historyId);
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                JsonObject result = new JsonObject();
                result.addProperty("success", false);
                result.addProperty("message", "无效的ID");
                response.getWriter().print(gson.toJson(result));
            }
        } else if (pathInfo.matches("/\\d+/versions/\\d+/?")) {
            // 获取特定版本: /api/history/{id}/versions/{versionId}
            String[] parts = pathInfo.split("/");
            try {
                Long versionId = Long.parseLong(parts[3]);
                handleGetVersion(response, versionId);
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                JsonObject result = new JsonObject();
                result.addProperty("success", false);
                result.addProperty("message", "无效的ID");
                response.getWriter().print(gson.toJson(result));
            }
        } else {
            // 获取单个历史记录
            String idStr = pathInfo.substring(1);
            try {
                Long id = Long.parseLong(idStr);
                handleGetById(response, id);
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                JsonObject result = new JsonObject();
                result.addProperty("success", false);
                result.addProperty("message", "无效的ID");
                response.getWriter().print(gson.toJson(result));
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Long userId = verifyToken(request, response);
        if (userId == null) {
            return;
        }

        handleCreate(request, response, userId);
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Long userId = verifyToken(request, response);
        if (userId == null) {
            return;
        }

        String pathInfo = request.getPathInfo();
        if (pathInfo == null || "/".equals(pathInfo)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            JsonObject result = new JsonObject();
            result.addProperty("success", false);
            result.addProperty("message", "缺少ID参数");
            response.getWriter().print(gson.toJson(result));
            return;
        }

        String[] parts = pathInfo.substring(1).split("/");
        try {
            Long id = Long.parseLong(parts[0]);
            if (parts.length > 1 && "favorite".equals(parts[1])) {
                handleToggleFavorite(request, response, id);
            } else {
                handleUpdate(request, response, id);
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            JsonObject result = new JsonObject();
            result.addProperty("success", false);
            result.addProperty("message", "无效的ID");
            response.getWriter().print(gson.toJson(result));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Long userId = verifyToken(request, response);
        if (userId == null) {
            return;
        }

        String pathInfo = request.getPathInfo();
        if (pathInfo == null || "/".equals(pathInfo)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            JsonObject result = new JsonObject();
            result.addProperty("success", false);
            result.addProperty("message", "缺少ID参数");
            response.getWriter().print(gson.toJson(result));
            return;
        }

        String idStr = pathInfo.substring(1);
        try {
            Long id = Long.parseLong(idStr);
            handleDelete(response, id);
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            JsonObject result = new JsonObject();
            result.addProperty("success", false);
            result.addProperty("message", "无效的ID");
            response.getWriter().print(gson.toJson(result));
        }
    }

    /**
     * 处理获取历史记录列表.
     */
    private void handleList(HttpServletRequest request, HttpServletResponse response, Long userId)
            throws IOException {
        PrintWriter out = response.getWriter();
        JsonObject result = new JsonObject();

        try {
            int page = request.getParameter("page") != null
                ? Integer.parseInt(request.getParameter("page")) : 1;
            int pageSize = request.getParameter("pageSize") != null
                ? Integer.parseInt(request.getParameter("pageSize")) : 20;

            int offset = (page - 1) * pageSize;

            List<DiagramHistory> histories = historyDao.findByUserId(userId, pageSize, offset);
            int total = historyDao.countByUserId(userId);

            JsonArray historyArray = new JsonArray();
            for (DiagramHistory history : histories) {
                historyArray.add(historyToJson(history));
            }

            result.addProperty("success", true);
            result.add("data", historyArray);
            result.addProperty("total", total);
            result.addProperty("page", page);
            result.addProperty("pageSize", pageSize);

            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            result.addProperty("success", false);
            result.addProperty("message", "获取历史记录失败：" + e.getMessage());
        }

        out.print(gson.toJson(result));
    }

    /**
     * 处理获取收藏列表.
     */
    private void handleFavorites(HttpServletResponse response, Long userId) throws IOException {
        PrintWriter out = response.getWriter();
        JsonObject result = new JsonObject();

        try {
            List<DiagramHistory> favorites = historyDao.findFavoritesByUserId(userId);

            JsonArray favoritesArray = new JsonArray();
            for (DiagramHistory history : favorites) {
                favoritesArray.add(historyToJson(history));
            }

            result.addProperty("success", true);
            result.add("data", favoritesArray);

            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            result.addProperty("success", false);
            result.addProperty("message", "获取收藏列表失败：" + e.getMessage());
        }

        out.print(gson.toJson(result));
    }

    /**
     * 处理根据ID获取历史记录.
     */
    private void handleGetById(HttpServletResponse response, Long id) throws IOException {
        PrintWriter out = response.getWriter();
        JsonObject result = new JsonObject();

        try {
            DiagramHistory history = historyDao.findById(id);

            if (history != null) {
                result.addProperty("success", true);
                result.add("data", historyToJson(history));
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                result.addProperty("success", false);
                result.addProperty("message", "历史记录不存在");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            result.addProperty("success", false);
            result.addProperty("message", "获取历史记录失败：" + e.getMessage());
        }

        out.print(gson.toJson(result));
    }

    /**
     * 处理创建历史记录.
     */
    private void handleCreate(HttpServletRequest request, HttpServletResponse response, Long userId)
            throws IOException {
        // 设置请求编码为UTF-8
        request.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();
        JsonObject result = new JsonObject();

        try {
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            JsonObject requestData = gson.fromJson(sb.toString(), JsonObject.class);
            String originalText = requestData.get("originalText").getAsString();
            String plantumlCode = requestData.get("plantumlCode").getAsString();
            String title = requestData.has("title")
                ? requestData.get("title").getAsString() : "未命名图表";

            DiagramHistory history = new DiagramHistory(userId, originalText, plantumlCode);
            history.setTitle(title);

            if (requestData.has("description")) {
                history.setDescription(requestData.get("description").getAsString());
            }
            if (requestData.has("diagramType")) {
                history.setDiagramType(requestData.get("diagramType").getAsString());
            }

            Long historyId = historyDao.create(history);

            if (historyId != null) {
                // 创建初始版本（版本1）
                String initialDescription = requestData.has("description")
                        ? requestData.get("description").getAsString()
                        : "初始版本";
                createVersion(historyId, plantumlCode, title, initialDescription, userId);

                result.addProperty("success", true);
                result.addProperty("id", historyId);
                result.addProperty("message", "保存成功");
                response.setStatus(HttpServletResponse.SC_CREATED);
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                result.addProperty("success", false);
                result.addProperty("message", "保存失败");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            result.addProperty("success", false);
            result.addProperty("message", "保存失败：" + e.getMessage());
        }

        out.print(gson.toJson(result));
    }

    /**
     * 处理更新历史记录.
     */
    private void handleUpdate(HttpServletRequest request, HttpServletResponse response, Long id)
            throws IOException {
        // 设置请求编码为UTF-8
        request.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();
        JsonObject result = new JsonObject();

        try {
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            JsonObject requestData = gson.fromJson(sb.toString(), JsonObject.class);
            DiagramHistory history = historyDao.findById(id);

            if (history == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                result.addProperty("success", false);
                result.addProperty("message", "历史记录不存在");
                out.print(gson.toJson(result));
                return;
            }

            // 保存旧的PlantUML代码用于版本对比
            String oldPlantumlCode = history.getPlantumlCode();
            boolean codeChanged = false;

            if (requestData.has("title")) {
                history.setTitle(requestData.get("title").getAsString());
            }
            if (requestData.has("description")) {
                history.setDescription(requestData.get("description").getAsString());
            }
            if (requestData.has("plantumlCode")) {
                String newCode = requestData.get("plantumlCode").getAsString();
                if (!newCode.equals(oldPlantumlCode)) {
                    history.setPlantumlCode(newCode);
                    codeChanged = true;
                }
            }
            if (requestData.has("diagramType")) {
                history.setDiagramType(requestData.get("diagramType").getAsString());
            }

            boolean success = historyDao.update(history);

            if (success) {
                // 如果代码发生变化，创建新版本
                if (codeChanged) {
                    String changeDesc = requestData.has("changeDescription")
                            ? requestData.get("changeDescription").getAsString()
                            : "更新PlantUML代码";
                    createVersion(id, history.getPlantumlCode(), history.getTitle(),
                            changeDesc, history.getUserId());
                }

                result.addProperty("success", true);
                result.addProperty("message", "更新成功");
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                result.addProperty("success", false);
                result.addProperty("message", "更新失败");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            result.addProperty("success", false);
            result.addProperty("message", "更新失败：" + e.getMessage());
        }

        out.print(gson.toJson(result));
    }

    /**
     * 处理切换收藏状态.
     */
    private void handleToggleFavorite(HttpServletRequest request, HttpServletResponse response, Long id)
            throws IOException {
        PrintWriter out = response.getWriter();
        JsonObject result = new JsonObject();

        try {
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            JsonObject requestData = gson.fromJson(sb.toString(), JsonObject.class);
            boolean isFavorite = requestData.get("isFavorite").getAsBoolean();

            boolean success = historyDao.toggleFavorite(id, isFavorite);

            if (success) {
                result.addProperty("success", true);
                result.addProperty("message", isFavorite ? "已收藏" : "已取消收藏");
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                result.addProperty("success", false);
                result.addProperty("message", "操作失败");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            result.addProperty("success", false);
            result.addProperty("message", "操作失败：" + e.getMessage());
        }

        out.print(gson.toJson(result));
    }

    /**
     * 处理删除历史记录.
     */
    private void handleDelete(HttpServletResponse response, Long id) throws IOException {
        PrintWriter out = response.getWriter();
        JsonObject result = new JsonObject();

        try {
            boolean success = historyDao.delete(id);

            if (success) {
                result.addProperty("success", true);
                result.addProperty("message", "删除成功");
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                result.addProperty("success", false);
                result.addProperty("message", "删除失败");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            result.addProperty("success", false);
            result.addProperty("message", "删除失败：" + e.getMessage());
        }

        out.print(gson.toJson(result));
    }

    /**
     * 将DiagramHistory对象转换为JSON.
     */
    private JsonObject historyToJson(DiagramHistory history) {
        JsonObject json = new JsonObject();
        json.addProperty("id", history.getId());
        json.addProperty("userId", history.getUserId());
        json.addProperty("title", history.getTitle());
        json.addProperty("description", history.getDescription());
        json.addProperty("originalText", history.getOriginalText());
        json.addProperty("plantumlCode", history.getPlantumlCode());
        json.addProperty("diagramType", history.getDiagramType());
        json.addProperty("imageUrl", history.getImageUrl());
        json.addProperty("createdAt", history.getCreatedAt() != null
            ? history.getCreatedAt().toString() : null);
        json.addProperty("updatedAt", history.getUpdatedAt() != null
            ? history.getUpdatedAt().toString() : null);
        json.addProperty("isFavorite", history.getIsFavorite());
        return json;
    }

    /**
     * 处理获取版本列表.
     */
    private void handleGetVersions(HttpServletResponse response, Long historyId) throws IOException {
        PrintWriter out = response.getWriter();
        JsonObject result = new JsonObject();

        try {
            List<DiagramVersion> versions = versionDao.findByHistoryId(historyId, 20);

            JsonArray versionsArray = new JsonArray();
            for (DiagramVersion version : versions) {
                versionsArray.add(versionToJson(version));
            }

            result.addProperty("success", true);
            result.add("data", versionsArray);
            result.addProperty("total", versions.size());

            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            result.addProperty("success", false);
            result.addProperty("message", "获取版本列表失败：" + e.getMessage());
        }

        out.print(gson.toJson(result));
    }

    /**
     * 处理获取特定版本.
     */
    private void handleGetVersion(HttpServletResponse response, Long versionId) throws IOException {
        PrintWriter out = response.getWriter();
        JsonObject result = new JsonObject();

        try {
            DiagramVersion version = versionDao.findById(versionId);

            if (version != null) {
                result.addProperty("success", true);
                result.add("data", versionToJson(version));
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                result.addProperty("success", false);
                result.addProperty("message", "版本不存在");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            result.addProperty("success", false);
            result.addProperty("message", "获取版本失败：" + e.getMessage());
        }

        out.print(gson.toJson(result));
    }

    /**
     * 创建版本（在更新历史记录时调用）.
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

    /**
     * 将DiagramVersion对象转换为JSON.
     */
    private JsonObject versionToJson(DiagramVersion version) {
        JsonObject json = new JsonObject();
        json.addProperty("id", version.getId());
        json.addProperty("historyId", version.getHistoryId());
        json.addProperty("versionNumber", version.getVersionNumber());
        json.addProperty("plantumlCode", version.getPlantumlCode());
        json.addProperty("title", version.getTitle());
        json.addProperty("changeDescription", version.getChangeDescription());
        json.addProperty("createdAt", version.getCreatedAt() != null
            ? version.getCreatedAt().toString() : null);
        json.addProperty("createdBy", version.getCreatedBy());
        return json;
    }
}

