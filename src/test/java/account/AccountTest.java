package account;

import org.junit.Test;
import service.BankOperation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import static org.junit.Assert.*;

/**
 * @author Tuan Hiep TRAN
 */
public class AccountTest {

    Date dateNow = new Date();

    @Test
    public void should_return_the_balance_1000_because_i_credit_1000() throws ParseException {
        Account account = new Account();
        assertEquals(0, account.getBalance(), 0);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        account.credit(1000.00, dateFormat.parse("14/03/2021"));
        assertEquals("The balance should be 1000 ", 1000.0, account.getBalance(), 0);
    }

    @Test
    public void should_return_balance_17000_because_i_debit_3000_from_20000() throws ParseException {
        Account account = new Account();
        account.setBalance(20000.0);
        assertEquals(20000.0, account.getBalance(), 0);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        account.debit(3000.00, dateFormat.parse("14/03/2021"));
        assertEquals("The balance should be 20000 - 3000 = 17000  ", 17000.0, account.getBalance(), 0);
    }

    @SuppressWarnings("resource")
    @Test
    public void should_return_the_same_printed_statement_as_expected_statement() throws ParseException, IOException {
        Account account = new Account();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        account.credit(1000.00, dateFormat.parse("01/01/2021"));
        account.credit(2000.00, dateFormat.parse("02/01/2021"));
        account.debit(500.00, dateFormat.parse("03/01/2021"));
        Files.deleteIfExists(Paths.get("src/test/resources/account/statement.txt"));
        FileOutputStream output = new FileOutputStream("src/test/resources/account/statement.txt", true);
        PrintStream printer = new PrintStream(output);
        account.printBalanceHistory(printer);
        String printed = new Scanner(new File("src/test/resources/account/statement.txt")).useDelimiter("\\Z").next();
        String expected = new Scanner(new File("src/test/resources/account/statementExpected.txt")).useDelimiter("\\Z")
                .next();
        System.out.println(expected.trim());
        System.out.println(printed.trim());
    }

    @Test
    public void should_credit() {
        Account account = new Account();
        assertEquals(0, account.getBalance(), 0);
        account.credit(1000.00,dateNow);
        assertEquals("The balance should be 1000 ", 1000.0, account.getBalance(), 0);
    }

    @Test
    public void should_credit_exception() {
        Account account = new Account();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            account.credit(null, dateNow)
        );
        assertEquals("Value cannot be null or negative", exception.getMessage());

        exception = assertThrows(IllegalArgumentException.class, () ->
            account.credit(0.00, dateNow)
        );
        assertEquals("Value cannot be null or negative", exception.getMessage());

        exception = assertThrows(IllegalArgumentException.class, () -> account.credit(-1000.00, dateNow)
        );
        assertEquals("Value cannot be null or negative", exception.getMessage());
    }

    @Test
    public void should_transfer(){
        Account account = new Account();
        Account destination = new Account();
        assertEquals(0, account.getBalance(), 0);
        assertEquals(0, destination.getBalance(), 0);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            account.transfer(destination,100)
        );
        assertEquals("Balance cannot be less than amount", exception.getMessage());

        account.credit(1000.00, dateNow);
        assertEquals("The balance should be 1000 ", 1000.0, account.getBalance(), 0);

        exception = assertThrows(IllegalArgumentException.class, () ->
            account.transfer(null,100)
        );
        assertEquals("Destination cannot be null", exception.getMessage());

        exception = assertThrows(IllegalArgumentException.class, () ->
            account.transfer(destination,0)
        );
        assertEquals("Amount cannot be 0 or negative", exception.getMessage());

        account.transfer(destination,100);
        assertEquals("The balance should be 900 ", 900.0, account.getBalance(), 0);
        assertEquals("The Destination balance should be 100 ", 100.0, destination.getBalance(), 0);
    }

    @Test
    public void should_keep_transaction_history_when_crediting_account()  {
        Account account = new Account();
        account.credit(100.00, dateNow);
        account.credit(150.00, dateNow);

        List<Transaction> transactions = account.getTransactions();
        assertEquals(2, transactions.size());
        assertEquals(BankOperation.CREDIT, transactions.get(0).getTypeOperation());
        assertEquals(Double.valueOf(100.00), transactions.get(0).getValue());
        assertEquals(BankOperation.CREDIT, transactions.get(1).getTypeOperation());
        assertEquals(Double.valueOf(150.00), transactions.get(1).getValue());

    }

    @Test
    public void should_keep_transaction_history_when_debiting_account(){
        Account account = new Account();
        account.credit(150.00, dateNow);
        account.debit(100.00, dateNow);

        List<Transaction> transactions = account.getTransactions();
        assertEquals(2, transactions.size());
        assertEquals(BankOperation.CREDIT, transactions.get(0).getTypeOperation());
        assertEquals(Double.valueOf(150.00), transactions.get(0).getValue());
        assertEquals(BankOperation.DEBIT, transactions.get(1).getTypeOperation());
        assertEquals(Double.valueOf(100.00), transactions.get(1).getValue());
    }

    @Test
    public void should_keep_transaction_history_in_operation_order(){
        Account account = new Account();
        account.credit(150.00, dateNow);
        account.debit(100.00, dateNow);
        account.credit(50.00, dateNow);

        List<Transaction> transactions = account.getTransactions();
        assertEquals(3, transactions.size());
        assertEquals(BankOperation.CREDIT, transactions.get(0).getTypeOperation());
        assertEquals(Double.valueOf(150.00), transactions.get(0).getValue());
        assertEquals(BankOperation.DEBIT, transactions.get(1).getTypeOperation());
        assertEquals(Double.valueOf(100.00), transactions.get(1).getValue());
        assertEquals(BankOperation.CREDIT, transactions.get(2).getTypeOperation());
        assertEquals(Double.valueOf(50.00), transactions.get(2).getValue());

    }
}
