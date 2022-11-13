package lk.ijse.dep.api;


import jakarta.annotation.Resource;
import jakarta.json.JsonException;
import jakarta.json.bind.JsonbBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lk.ijse.dep.dto.AccountDTO;

import javax.sql.DataSource;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

@WebServlet(name = "account-servlet", urlPatterns = "/accounts/*",loadOnStartup = 0)
public class AccountServlet extends HttpServlet {
    @Resource(lookup = "java:comp/env/jdbc/banking-app" )
    private DataSource pool;


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getPathInfo() == null || req.getPathInfo().equals("/")){
            try{
                if (req.getContentType() == null || !req.getContentType().startsWith("application/json")){
                    throw new JsonException("Invalid JSON");
                }

               AccountDTO accountDTO = JsonbBuilder.create().fromJson(req.getReader(), AccountDTO.class);
                       createAccount(accountDTO,resp);

            } catch (JsonException e){
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST,e.getMessage());
            }
        }else {
            resp.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED);
        }

    }

    private void createAccount(AccountDTO accountDTO, HttpServletResponse resp) {

        try(Connection connection = pool.getConnection()){
            if(accountDTO.getName() == null || accountDTO.getName().matches("[A-Za-z ]+")){
                throw new JsonException("Invalid account holder address");
            }else if (accountDTO.getAddress() == null || accountDTO.getAddress().isBlank()){
                throw new JsonException("Invalid account holder address");
            }

            accountDTO.setAccountNumber(UUID.randomUUID().toString());
            accountDTO.setBalance(BigDecimal.ZERO);



        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
