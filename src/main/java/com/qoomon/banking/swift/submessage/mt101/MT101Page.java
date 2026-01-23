package com.qoomon.banking.swift.submessage.mt101;

import com.google.common.base.Preconditions;
import com.qoomon.banking.swift.submessage.Page;
import com.qoomon.banking.swift.submessage.PageSeparator;
import com.qoomon.banking.swift.submessage.field.mt101.*;

import java.util.List;
import java.util.Optional;

import static com.qoomon.banking.swift.submessage.field.FieldUtils.swiftTextOf;

/**
 * MT101 General Direct Debit Message Page
 * <p>
 * Contains Sequence A (General Information) and Sequence B (Transaction Details - repetitive)
 */
public class MT101Page implements Page {

    public static final String MESSAGE_ID_101 = "101";

    // Sequence A (General Information) - Mandatory
    private final SendersReference sendersReference;
    
    // Sequence A (General Information) - Optional
    private final Optional<CustomerSpecifiedReference> customerSpecifiedReference;
    private final Optional<RequestedExecutionDate> requestedExecutionDate;

    // Sequence B (Transaction Details) - Repetitive
    private final List<TransactionDetails> transactionDetailsList;

    public MT101Page(
            SendersReference sendersReference,
            CustomerSpecifiedReference customerSpecifiedReference,
            RequestedExecutionDate requestedExecutionDate,
            List<TransactionDetails> transactionDetailsList) {

        Preconditions.checkArgument(sendersReference != null, "sendersReference can't be null");
        Preconditions.checkArgument(transactionDetailsList != null, "transactionDetailsList can't be null");
        Preconditions.checkArgument(!transactionDetailsList.isEmpty(), "transactionDetailsList can't be empty");

        this.sendersReference = sendersReference;
        this.customerSpecifiedReference = Optional.ofNullable(customerSpecifiedReference);
        this.requestedExecutionDate = Optional.ofNullable(requestedExecutionDate);
        this.transactionDetailsList = transactionDetailsList;
    }

    public SendersReference getSendersReference() {
        return sendersReference;
    }

    public Optional<CustomerSpecifiedReference> getCustomerSpecifiedReference() {
        return customerSpecifiedReference;
    }

    public Optional<RequestedExecutionDate> getRequestedExecutionDate() {
        return requestedExecutionDate;
    }

    public List<TransactionDetails> getTransactionDetailsList() {
        return transactionDetailsList;
    }

    @Override
    public String getId() {
        return MESSAGE_ID_101;
    }

    public String getContent() {
        StringBuilder contentBuilder = new StringBuilder();
        
        // Sequence A: General Information
        contentBuilder.append(swiftTextOf(sendersReference)).append("\n");
        
        if (customerSpecifiedReference.isPresent()) {
            contentBuilder.append(swiftTextOf(customerSpecifiedReference.get())).append("\n");
        }
        
        if (requestedExecutionDate.isPresent()) {
            contentBuilder.append(swiftTextOf(requestedExecutionDate.get())).append("\n");
        }
        
        // Sequence B: Transaction Details
        for (TransactionDetails transactionDetails : transactionDetailsList) {
            contentBuilder.append(swiftTextOf(transactionDetails.getTransactionReference())).append("\n");
            
            if (transactionDetails.getInstructionCode().isPresent()) {
                contentBuilder.append(swiftTextOf(transactionDetails.getInstructionCode().get())).append("\n");
            }
            
            contentBuilder.append(swiftTextOf(transactionDetails.getCurrencyTransactionAmount())).append("\n");
            contentBuilder.append(swiftTextOf(transactionDetails.getBeneficiary())).append("\n");
            
            if (transactionDetails.getRemittanceInformation().isPresent()) {
                contentBuilder.append(swiftTextOf(transactionDetails.getRemittanceInformation().get())).append("\n");
            }
            
            if (transactionDetails.getDetailsOfCharges().isPresent()) {
                contentBuilder.append(swiftTextOf(transactionDetails.getDetailsOfCharges().get())).append("\n");
            }
        }
        
        contentBuilder.append(PageSeparator.TAG);
        return contentBuilder.toString();
    }
}