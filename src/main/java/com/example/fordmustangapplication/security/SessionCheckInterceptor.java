package com.example.fordmustangapplication.security;

import com.example.fordmustangapplication.user.model.User;
import com.example.fordmustangapplication.user.model.UserRole;
import com.example.fordmustangapplication.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;
import java.util.UUID;

@Component
public class SessionCheckInterceptor implements HandlerInterceptor {

    private final List<String> PUBLIC_ENDPOINTS = List.of("/", "/login", "/register", "/info-application");

    private final List<String> ADMIN_ENDPOINTS = List.of("/admin-users");


    private final UserService userService;

    public SessionCheckInterceptor(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {


        String endpoint = request.getServletPath();

        if (PUBLIC_ENDPOINTS.contains(endpoint)) {
            return true;
        }

        HttpSession httpSession = request.getSession(false);

        if (httpSession == null) {
            response.sendRedirect("/login");
            return false;
        }

        User user = getValidUser(httpSession);

        if (user == null) {
            invalidate(httpSession);
            response.sendRedirect("/login");
            return false;
        }

        request.setAttribute("user", user);

        if (ADMIN_ENDPOINTS.contains(endpoint)) {
            if (user.getRole() != UserRole.ADMIN) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write("Access denied. Administrator privileges are required to view this page.");
                return false;
            }
        }
        return true;
    }


    private User getValidUser(HttpSession session) {
        if (session == null) {
            return null;
        }

        UUID userId = (UUID) session.getAttribute("user_id");

        if (userId == null) {
            return null;
        }

        User user = userService.getById(userId);

        if (user == null || !user.isActive()) {
            return null;
        }

        return user;
    }


    private void invalidate(HttpSession session) {
        if (session != null) {
            session.invalidate();
        }
    }
}
