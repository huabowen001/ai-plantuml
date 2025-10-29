package net.sourceforge.plantuml.servlet;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.sourceforge.plantuml.servlet.dao.UserDao;
import net.sourceforge.plantuml.servlet.model.User;
import net.sourceforge.plantuml.servlet.util.JWTUtil;
import net.sourceforge.plantuml.servlet.util.PasswordUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 用户认证Servlet（注册和登录）.
 */
public class AuthServlet extends HttpServlet {

    private final UserDao userDao = new UserDao();
    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String path = request.getServletPath();

        if ("/api/auth/register".equals(path)) {
            handleRegister(request, response);
        } else if ("/api/auth/login".equals(path)) {
            handleLogin(request, response);
        }
    }

    /**
     * 处理用户注册.
     */
    private void handleRegister(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
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
            String username = requestData.get("username").getAsString();
            String email = requestData.get("email").getAsString();
            String password = requestData.get("password").getAsString();
            String fullName = requestData.has("fullName")
                ? requestData.get("fullName").getAsString() : null;

            // 验证输入
            if (username == null || username.trim().isEmpty()
                    || email == null || email.trim().isEmpty()
                    || password == null || password.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                result.addProperty("success", false);
                result.addProperty("message", "用户名、邮箱和密码不能为空");
                out.print(gson.toJson(result));
                return;
            }

            // 检查用户名是否已存在
            if (userDao.findByUsername(username) != null) {
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                result.addProperty("success", false);
                result.addProperty("message", "用户名已存在");
                out.print(gson.toJson(result));
                return;
            }

            // 检查邮箱是否已存在
            if (userDao.findByEmail(email) != null) {
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                result.addProperty("success", false);
                result.addProperty("message", "邮箱已被注册");
                out.print(gson.toJson(result));
                return;
            }

            // 创建新用户
            User user = new User(username, email, PasswordUtil.hashPassword(password));
            user.setFullName(fullName);
            Long userId = userDao.create(user);

            if (userId != null) {
                // 生成JWT Token
                String token = JWTUtil.generateToken(userId, username);

                result.addProperty("success", true);
                result.addProperty("message", "注册成功");
                result.addProperty("token", token);

                JsonObject userData = new JsonObject();
                userData.addProperty("id", userId);
                userData.addProperty("username", username);
                userData.addProperty("email", email);
                userData.addProperty("fullName", fullName);
                result.add("user", userData);

                response.setStatus(HttpServletResponse.SC_CREATED);
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                result.addProperty("success", false);
                result.addProperty("message", "注册失败，请稍后再试");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            result.addProperty("success", false);
            result.addProperty("message", "服务器错误：" + e.getMessage());
        }

        out.print(gson.toJson(result));
    }

    /**
     * 处理用户登录.
     */
    private void handleLogin(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
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
            String usernameOrEmail = requestData.get("username").getAsString();
            String password = requestData.get("password").getAsString();

            // 验证输入
            if (usernameOrEmail == null || usernameOrEmail.trim().isEmpty()
                    || password == null || password.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                result.addProperty("success", false);
                result.addProperty("message", "用户名和密码不能为空");
                out.print(gson.toJson(result));
                return;
            }

            // 查找用户（支持用户名或邮箱登录）
            User user = userDao.findByUsername(usernameOrEmail);
            if (user == null) {
                user = userDao.findByEmail(usernameOrEmail);
            }

            if (user == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                result.addProperty("success", false);
                result.addProperty("message", "用户名或密码错误");
                out.print(gson.toJson(result));
                return;
            }

            // 验证密码
            if (!PasswordUtil.verifyPassword(password, user.getPasswordHash())) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                result.addProperty("success", false);
                result.addProperty("message", "用户名或密码错误");
                out.print(gson.toJson(result));
                return;
            }

            // 更新最后登录时间
            userDao.updateLastLogin(user.getId());

            // 生成JWT Token
            String token = JWTUtil.generateToken(user.getId(), user.getUsername());

            result.addProperty("success", true);
            result.addProperty("message", "登录成功");
            result.addProperty("token", token);

            JsonObject userData = new JsonObject();
            userData.addProperty("id", user.getId());
            userData.addProperty("username", user.getUsername());
            userData.addProperty("email", user.getEmail());
            userData.addProperty("fullName", user.getFullName());
            userData.addProperty("avatarUrl", user.getAvatarUrl());
            result.add("user", userData);

            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            result.addProperty("success", false);
            result.addProperty("message", "服务器错误：" + e.getMessage());
        }

        out.print(gson.toJson(result));
    }
}

