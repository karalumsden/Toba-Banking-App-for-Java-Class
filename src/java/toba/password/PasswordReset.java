package toba.password;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import javax.servlet.*;
import javax.servlet.http.*;

import toba.business.User;
import toba.data.UserDB;
import toba.password.PasswordUtil;

public class PasswordReset extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String url = "/Password_reset.jsp";

        // get current action
        String action = request.getParameter("action");
        if (action == null) {
            action = "submit"; // default action
        }

        if (action.equals("submit")) {
            url = "/Password_reset.jsp";
        } else if (action.equals("resetPassword")) {
            String password = request.getParameter("password");

            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");

            String message = "";

            if (password == null || password.isEmpty()) {
                message = "Please fill out the password field.";
                url = "/Password_reset.jsp";
            } else if (user == null) {
                message = "Oops! We noticed that you don't have a customer account yet. Please sign up below.";
                url = "/New_customer.jsp";
            } else {
                String hashedPassword;
                String salt = "";
                String saltedAndHashedPassword;
                try {
                    hashedPassword = PasswordUtil.hashPassword(user.getPassword());
                    salt = PasswordUtil.getSalt();
                    saltedAndHashedPassword = PasswordUtil.hashAndSaltPassword(user.getPassword());

                } catch (NoSuchAlgorithmException ex) {
                    hashedPassword = ex.getMessage();
                    saltedAndHashedPassword = ex.getMessage();
                }
                user.setPassword(saltedAndHashedPassword);
                url = "/Success.jsp";
                UserDB.update(user);
            }
            request.setAttribute("user", user);
            request.setAttribute("message", message);
        }
        getServletContext()
                .getRequestDispatcher(url)
                .forward(request, response);
    }
}
