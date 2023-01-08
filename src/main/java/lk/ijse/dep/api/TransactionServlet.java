package lk.ijse.dep.api;

import jakarta.annotation.Resource;
import jakarta.json.Json;
import jakarta.json.JsonException;
import jakarta.json.JsonObject;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.stream.JsonParser;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lk.ijse.dep.dto.AccountDTO;
import lk.ijse.dep.dto.TransactionDTO;
import lk.ijse.dep.dto.TransferDTO;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.sql.*;
import java.util.Date;


@WebServlet(name = "transaction-servlet", urlPatterns = "/transactions/*" ,loadOnStartup = 0)
public class TransactionServlet extends HttpServlet {

    @Resource(lookup = "java:comp/env/jdbc/banking-app")
    private DataSource pool;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getPathInfo() == null || req.getContentType().equals("/")){
            try {
                if (req.getContentType() == null || !req.getContentType().startsWith("application/json")) {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST,"Invalid JSON");
                    return;
                }

                String json = req.getReader().lines().reduce("", (p, c) -> p + c);

                JsonParser parser = Json.createParser(new StringReader(json));
                parser.next();
                JsonObject jsonBObject = parser.getObject();
                String transactionType = jsonBObject.getString("type");
                if (transactionType.equalsIgnoreCase("withdraw")) {
                    TransactionDTO transactionDTO = JsonbBuilder.create().fromJson(json, TransactionDTO.class);
                    withdrawMoney(transactionDTO ,resp);
                } else if (transactionType.equalsIgnoreCase("transfer")) {
                    TransferDTO transferDTO = JsonbBuilder.create().fromJson(json, TransferDTO.class);
                    transferMoney(transferDTO,resp);
                } else if (transactionType.equalsIgnoreCase("deposit")) {
                    TransactionDTO transactionDTO = JsonbBuilder.create().fromJson(json, TransactionDTO.class);
                    depositMoney(transactionDTO,resp);
                } else {
                    throw new JsonException("Invalid JSON");
                }
          } catch (JsonException e) {
               resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid JSON");
            }


        }else {
            resp.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED);
        }
    }

    private void withdrawMoney(TransactionDTO transactionDTO, HttpServletResponse resp) {



    }

    private void transferMoney(TransferDTO transferDTO, HttpServletResponse resp) {


    }

    private void depositMoney(TransactionDTO transactionDTO, HttpServletResponse response) throws IOException {


        try {
            if (transactionDTO.getAccount() == null || !transactionDTO.getAccount().matches("[A-Fa-f0-9]{8}(-[A-Fa-f0-9]{4}){3}-[A-Fa-f0-9]{12}")) {
                throw new JsonException("Invalid account number");
            } else if (transactionDTO.getAmount() == null || transactionDTO.getAmount().compareTo(new BigDecimal(100)) < 100) {
                throw new JsonException("Invalid amount");
            }
            Connection connection = pool.getConnection();
            PreparedStatement stm = connection.prepareStatement("SELECT  * FROM Account WHERE  account_number = ?");
            stm.setString(1,transactionDTO.getAccount());
            ResultSet rst = stm.executeQuery();
            if (!rst.next()) {
                throw new JsonException("Invalid account  number");
            }

            try {
                connection.setAutoCommit(false);
                PreparedStatement stmUpdate = connection.prepareStatement("UPDATE  Account SET balance = balance + ?  WHERE account_number = ?");
                stmUpdate.setBigDecimal(1,transactionDTO.getAmount());
                stmUpdate.setString(2,transactionDTO.getAccount());
                if (stmUpdate.executeUpdate() != 1) {
                    throw new SQLException("Failed to update the balance");
                }

                PreparedStatement stmNewTransaction = connection.prepareStatement("INSERT INTO Transaction  (account,type, description,amount ,date) VALUES (?,?,?,?,?)");
                stmNewTransaction.setString(1,transactionDTO.getAccount());
                stmNewTransaction.setString(2,"CREDIT");
                stmNewTransaction.setString(3,"Deposit");
                stmNewTransaction.setBigDecimal(4,transactionDTO.getAmount());
                stmNewTransaction.setTimestamp(5,new Timestamp(new Date().getTime()));
                if (stmNewTransaction.executeUpdate() != 1) {
                    throw new SQLException("Failed to add a transaction record");
                }
                connection.commit();

                ResultSet resultSet = stm.executeQuery();
                resultSet.next();
                String name = resultSet.getString("holder_name");
                String address = resultSet.getString("holder_address");
                BigDecimal balance = resultSet.getBigDecimal("balance");

                AccountDTO accountDTO = new AccountDTO(transactionDTO.getAccount(), name, address, balance);
                response.setStatus(HttpServletResponse.SC_CREATED);
                response.setContentType("application/json");
                JsonbBuilder.create().toJson(accountDTO, response.getWriter());
            } catch (Throwable t) {
                connection.rollback();
                t.printStackTrace();
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"Failed to deposit the money, contact the bank");
            }finally {
                connection.setAutoCommit(true);
            }
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

    }
}
