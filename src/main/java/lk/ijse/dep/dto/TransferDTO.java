package lk.ijse.dep.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransferDTO {
    private String type;
    private String from;
    private String to;
    private BigDecimal amount;

}
