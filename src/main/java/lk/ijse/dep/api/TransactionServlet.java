package lk.ijse.dep.api;

import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.sql.DataSource;
import java.io.IOException;


@WebServlet(name = "transaction-servlet", urlPatterns = "/transactions/*" ,loadOnStartup = 0)
public class TransactionServlet extends HttpServlet {

    @Resource(lookup = "java:comp/env/jdbc/banking-app")
    private DataSource pool;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getPathInfo() == null || req.getContentType().equals("/")){
            if (req.getContentType() == null || !req.getContentType().startsWith("application/json")) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST,"Invalid JSON");
                return;
            }



        }else {
            resp.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED);
        }
    }

    private void withdrawMoney() {

    }

    private void transferMoney() {

    }
}
