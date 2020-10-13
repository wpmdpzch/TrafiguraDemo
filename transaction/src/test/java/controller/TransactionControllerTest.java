package controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trafigura.transaction.TransactionApplication;
import com.trafigura.transaction.dto.TransactionDTO;
import com.trafigura.transaction.entity.Position;
import com.trafigura.transaction.entity.Transaction;
import com.trafigura.transaction.enums.OperationTypeEnum;
import com.trafigura.transaction.enums.TradeTypeEnum;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author ：wpm
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TransactionApplication.class)
@AutoConfigureMockMvc
public class TransactionControllerTest {

    @Autowired
    protected WebApplicationContext webAppContext;

    private MockMvc mockMvc;

    private ObjectMapper jsonMapper = new ObjectMapper();

    @Before
    public void setup() throws IOException {

        mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext).dispatchOptions(true).build();

    }

    @Test
    public void testPlan() throws Exception {
        saveTransactionSingleLegalTest();
        saveTransactionInvalidDataTest();
        quertPositionByCodeTest();

    }

    /**
     * Single legal data test
     * <p>
     * plan:
     * Insert : trade 1 REL BUY  10
     * Insert :trade 2 REL SELL  10
     * UPDATE : trade 1 REL BUY  1
     * UPDATE : trade 2 REL SELL 1
     * Cancel : trade 1 REL BUY 100;
     * <p>
     * result:
     * possition:
     * REL:-1
     * <p>
     * transaction count:5
     * position count:1
     *
     * @throws Exception
     */
//    @Test
    public void saveTransactionSingleLegalTest() throws Exception {
        TransactionDTO dto = new TransactionDTO();
        dto.setQuantity(10L);
        dto.setOperationType(OperationTypeEnum.INSERT);
        dto.setSecurityCode("REL");
        dto.setTradeType(TradeTypeEnum.BUY);
        dto.setTradeId(1L);

        mockMvc.perform(post("/api/v1/transaction/save")
                .content(jsonMapper.writeValueAsString(dto)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        dto.setTradeId(2L);
        dto.setTradeType(TradeTypeEnum.SELL);

        mockMvc.perform(post("/api/v1/transaction/save")
                .content(jsonMapper.writeValueAsString(dto)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        dto.setQuantity(1L);
        dto.setOperationType(OperationTypeEnum.UPDATE);
        dto.setTradeType(TradeTypeEnum.BUY);
        dto.setTradeId(1L);
        mockMvc.perform(post("/api/v1/transaction/save")
                .content(jsonMapper.writeValueAsString(dto)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        dto.setQuantity(1L);
        dto.setOperationType(OperationTypeEnum.UPDATE);
        dto.setTradeType(TradeTypeEnum.SELL);
        dto.setTradeId(2L);
        mockMvc.perform(post("/api/v1/transaction/save")
                .content(jsonMapper.writeValueAsString(dto)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        dto.setQuantity(100L);
        dto.setOperationType(OperationTypeEnum.CANCEL);
        dto.setTradeType(TradeTypeEnum.BUY);
        dto.setTradeId(1L);
        mockMvc.perform(post("/api/v1/transaction/save")
                .content(jsonMapper.writeValueAsString(dto)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());


        String result = mockMvc.perform(get("/api/v1/position/getAll")).andExpect(status().isOk()).andReturn()
                .getResponse().getContentAsString();
        List<Position> positionList = jsonMapper.readValue(result, new TypeReference<List<Position>>() {
        });

        assertTrue(positionList.size() == 1);

        assertTrue(positionList.get(0).getPositionValue().equals(-1L));


        String transactionResult = mockMvc.perform(get("/api/v1/transaction/getAll")).andExpect(status().isOk()).andReturn()
                .getResponse().getContentAsString();
        List<Transaction> transactionList = jsonMapper.readValue(transactionResult, new TypeReference<List<Transaction>>() {
        });
        assertTrue(transactionList.size() == 5);
    }

    /**
     * Invalid data test
     * <p>
     * plan:
     * Insert : trade 3 ITC BUY  10   Status: ok
     * Insert :trade 3 ITC SELL  10   Status:bad
     * UPDATE : trade 4 ITC BUY  1    Status:bad
     * Cancel : trade 4 ITC SELL 1    Status:bad
     * Cancel : trade 3 ITC SELL 1    Status: ok
     * Insert : trade 3 ITC BUY  10   Status: bad
     * UPDATE : trade 3 ITC BUY  10   Status: bad
     * <p>
     * result:
     * position:
     * REL 0;
     * <p>
     * transaction count:2 +5
     * position count:1+1
     *
     * @throws Exception
     */
//    @Test
    public void saveTransactionInvalidDataTest() throws Exception {
        TransactionDTO dto = new TransactionDTO();
        dto.setQuantity(10L);
        dto.setOperationType(OperationTypeEnum.INSERT);
        dto.setSecurityCode("ITC");
        dto.setTradeType(TradeTypeEnum.BUY);
        dto.setTradeId(3L);
        mockMvc.perform(post("/api/v1/transaction/save")
                .content(jsonMapper.writeValueAsString(dto)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/transaction/save")
                .content(jsonMapper.writeValueAsString(dto)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        dto.setOperationType(OperationTypeEnum.UPDATE);
        dto.setTradeId(4L);
        mockMvc.perform(post("/api/v1/transaction/save")
                .content(jsonMapper.writeValueAsString(dto)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        dto.setOperationType(OperationTypeEnum.CANCEL);
        dto.setTradeId(4L);
        mockMvc.perform(post("/api/v1/transaction/save")
                .content(jsonMapper.writeValueAsString(dto)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        dto.setOperationType(OperationTypeEnum.CANCEL);
        dto.setTradeId(3L);
        mockMvc.perform(post("/api/v1/transaction/save")
                .content(jsonMapper.writeValueAsString(dto)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        dto.setOperationType(OperationTypeEnum.INSERT);
        mockMvc.perform(post("/api/v1/transaction/save")
                .content(jsonMapper.writeValueAsString(dto)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        dto.setOperationType(OperationTypeEnum.UPDATE);
        mockMvc.perform(post("/api/v1/transaction/save")
                .content(jsonMapper.writeValueAsString(dto)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        String result = mockMvc.perform(get("/api/v1/position/getAll")).andExpect(status().isOk()).andReturn()
                .getResponse().getContentAsString();
        List<Position> positionList = jsonMapper.readValue(result, new TypeReference<List<Position>>() {
        });

        assertTrue(positionList.size() == 2);

//        assertTrue(positionList.get(0).getPositionValue().equals(0L));


        String transactionResult = mockMvc.perform(get("/api/v1/transaction/getAll")).andExpect(status().isOk()).andReturn()
                .getResponse().getContentAsString();
        List<Transaction> transactionList = jsonMapper.readValue(transactionResult, new TypeReference<List<Transaction>>() {
        });
        assertTrue(transactionList.size() == 7);
    }

    /**
     * Invalid data test
     * <p>
     * plan:
     * Insert : trade 5 INF BUY  10   Status: ok
     * Cancel : trade 5 INF SELL 1    Status: ok
     * Insert : trade 6 ABC BUY  10   Status: ok
     * Insert : trade 7 ABC BUY  10   Status: ok
     * UPDATE : trade 5 ABC BUY  10   Status: bad
     * <p>
     * Query: INF
     * <p>
     * result:
     * <p>
     * INF:0
     *
     * @throws Exception
     */
//    @Test
    public void quertPositionByCodeTest() throws Exception {
        TransactionDTO dto = new TransactionDTO();
        dto.setQuantity(10L);
        dto.setOperationType(OperationTypeEnum.INSERT);
        dto.setSecurityCode("INF");
        dto.setTradeType(TradeTypeEnum.BUY);
        dto.setTradeId(5L);
        mockMvc.perform(post("/api/v1/transaction/save")
                .content(jsonMapper.writeValueAsString(dto)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        dto.setQuantity(1L);
        dto.setTradeType(TradeTypeEnum.SELL);
        dto.setOperationType(OperationTypeEnum.CANCEL);
        mockMvc.perform(post("/api/v1/transaction/save")
                .content(jsonMapper.writeValueAsString(dto)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        dto.setOperationType(OperationTypeEnum.INSERT);
        dto.setTradeId(6L);
        dto.setQuantity(10L);
        dto.setSecurityCode("ABC");
        dto.setTradeType(TradeTypeEnum.BUY);
        mockMvc.perform(post("/api/v1/transaction/save")
                .content(jsonMapper.writeValueAsString(dto)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        dto.setOperationType(OperationTypeEnum.INSERT);
        dto.setTradeId(7L);
        mockMvc.perform(post("/api/v1/transaction/save")
                .content(jsonMapper.writeValueAsString(dto)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        dto.setOperationType(OperationTypeEnum.UPDATE);
        dto.setTradeId(5L);
        mockMvc.perform(post("/api/v1/transaction/save")
                .content(jsonMapper.writeValueAsString(dto)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());


        String result = mockMvc.perform(get("/api/v1/position/getByCode").param("securityCode", "INF")).andExpect(status().isOk()).andReturn()
                .getResponse().getContentAsString();
        Position position = jsonMapper.readValue(result, new TypeReference<Position>() {
        });

        assertTrue(position.getPositionValue() == 0);

    }


}
