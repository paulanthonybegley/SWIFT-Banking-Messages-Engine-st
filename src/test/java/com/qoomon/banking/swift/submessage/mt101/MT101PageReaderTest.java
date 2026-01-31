package com.qoomon.banking.swift.submessage.mt101;

import com.qoomon.banking.TestUtils;
import com.qoomon.banking.swift.message.exception.SwiftMessageParseException;
import com.qoomon.banking.swift.submessage.field.mt101.Beneficiary;
import com.qoomon.banking.swift.submessage.field.mt101.DetailsOfCharges;
import org.junit.Test;

import java.io.StringReader;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

public class MT101PageReaderTest {

    @Test
    public void parse_SHOULD_read_simple_mt101_message() throws Exception {
        // Given - field content that matches expected test results
        String mt101FieldContent = ""
                + ":20:TEST-2024001\n"
                + ":21R:COLL-PAYMENT-001\n"
                + ":30:240123\n"
                + ":21:TXN-001\n"
                + ":32B:EUR1000,00\n"
                + ":59:/DK1234567890\n"
                + "COMPANY NAME\n"
                + "ADDRESS LINE 1\n"
                + ":70:Test payment\n"
                + ":71A:SHA\n";

        MT101PageReader classUnderTest = new MT101PageReader(new StringReader(mt101FieldContent));

        // When
        List<MT101Page> result = TestUtils.collectUntilNull(classUnderTest::read);

        // Then
        assertThat(result).hasSize(1);
        
        MT101Page page = result.get(0);
        
        // Verify Sequence A (General Information)
        assertThat(page.getId()).isEqualTo("101");
        assertThat(page.getSendersReference().getContent()).isEqualTo("TEST-2024001");
        assertThat(page.getCustomerSpecifiedReference()).isPresent();
        assertThat(page.getCustomerSpecifiedReference().get().getContent()).isEqualTo("COLL-PAYMENT-001");
        assertThat(page.getRequestedExecutionDate()).isPresent();
        assertThat(page.getRequestedExecutionDate().get().getDate()).isEqualTo("240123");
        
        // Verify Sequence B (Transaction Details)
        assertThat(page.getTransactionDetailsList()).hasSize(1);
        
        var transaction = page.getTransactionDetailsList().get(0);
        
        // Verify transaction fields
        assertThat(transaction.getTransactionReference().getContent()).isEqualTo("TXN-001");
        assertThat(transaction.getCurrencyTransactionAmount().getCurrency()).isEqualTo("EUR");
        assertThat(transaction.getCurrencyTransactionAmount().getAmount()).isEqualTo("1000,00");
        assertThat(transaction.getBeneficiary().getOption()).isEqualTo(Beneficiary.Option.NO_OPTION);
        assertThat(transaction.getBeneficiary().getAccount()).isEqualTo("/DK1234567890");
        assertThat(transaction.getBeneficiary().getNameAndAddress()).containsExactly("COMPANY NAME", "ADDRESS LINE 1");
        assertThat(transaction.getRemittanceInformation()).isPresent();
        assertThat(transaction.getRemittanceInformation().get().getInformationLines()).containsExactly("Test payment");
        assertThat(transaction.getDetailsOfCharges()).isPresent();
        assertThat(transaction.getDetailsOfCharges().get().getChargeCode()).isEqualTo(DetailsOfCharges.ChargeCode.SHA);
    }
}