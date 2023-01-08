package lk.ijse.dep.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDTO implements Serializable {
    private String type;
    private String account;
    private BigDecimal amount;
}
