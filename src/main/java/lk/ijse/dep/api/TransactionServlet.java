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
import lk.ijse.dep.dto.TransactionDTO;
import lk.ijse.dep.dto.TransferDTO;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.StringReader;


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

    private void depositMoney(TransactionDTO transactionDTO, HttpServletResponse response) {

    }
}
