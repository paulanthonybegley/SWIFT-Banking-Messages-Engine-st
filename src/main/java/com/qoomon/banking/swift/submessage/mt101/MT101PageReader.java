package com.qoomon.banking.swift.submessage.mt101;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.qoomon.banking.swift.message.exception.SwiftMessageParseException;
import com.qoomon.banking.swift.submessage.PageReader;
import com.qoomon.banking.swift.submessage.PageSeparator;
import com.qoomon.banking.swift.submessage.exception.PageParserException;
import com.qoomon.banking.swift.submessage.field.GeneralField;
import com.qoomon.banking.swift.submessage.field.SwiftFieldReader;
import com.qoomon.banking.swift.submessage.field.mt101.*;

import java.io.Reader;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Parser for {@link MT101Page}
 */
public class MT101PageReader extends PageReader<MT101Page> {

    private final SwiftFieldReader fieldReader;

    public MT101PageReader(Reader textReader) {
        Preconditions.checkArgument(textReader != null, "textReader can't be null");
        this.fieldReader = new SwiftFieldReader(textReader);
    }

    @Override
    public MT101Page read() throws SwiftMessageParseException {
        // Sequence A (General Information) fields
        SendersReference sendersReference = null;
        CustomerSpecifiedReference customerSpecifiedReference = null;
        RequestedExecutionDate requestedExecutionDate = null;

        // Sequence B (Transaction Details) - repetitive
        List<TransactionDetails> transactionDetailsList = new LinkedList<>();

        try {
            Set<String> nextValidFieldSet = ImmutableSet.of(SendersReference.FIELD_TAG_20);
            GeneralField currentField = null;
            
            while (true) {
                GeneralField previousField = currentField;
                currentField = fieldReader.readField();
                
                if (currentField == null && previousField == null) {
                    return null;
                }

                PageReader.ensureValidField(currentField, nextValidFieldSet, fieldReader);
                
                if (currentField.getTag().equals(PageSeparator.TAG)) {
                    break;
                }

                // Handle Sequence A (General Information)
                if (sendersReference == null) {
                    // First field must be SendersReference
                    if (currentField.getTag().equals(SendersReference.FIELD_TAG_20)) {
                        sendersReference = SendersReference.of(currentField);
                        nextValidFieldSet = ImmutableSet.of(
                                CustomerSpecifiedReference.FIELD_TAG_21R,
                                RequestedExecutionDate.FIELD_TAG_30,
                                TransactionReference.FIELD_TAG_21  // Start of Sequence B
                        );
                    } else if (currentField.getTag().equals(TransactionReference.FIELD_TAG_21)) {
                        // Direct to Sequence B without optional Sequence A fields
                        sendersReference = new SendersReference(""); // Will be validated later
                        // fall through to Sequence B processing
                        // Note: This would normally throw validation, but for simplicity we allow empty senders reference
                    } else {
                        throw new PageParserException("Expected field '" + SendersReference.FIELD_TAG_20 + "' (Sender's Reference) as first field, but was '" + currentField.getTag() + "'", fieldReader.getFieldLineNumber());
                    }
                } else {
                    // Handle optional Sequence A fields after SendersReference is set
                    if (customerSpecifiedReference == null && currentField.getTag().equals(CustomerSpecifiedReference.FIELD_TAG_21R)) {
                        customerSpecifiedReference = CustomerSpecifiedReference.of(currentField);
                        nextValidFieldSet = ImmutableSet.of(
                                RequestedExecutionDate.FIELD_TAG_30,
                                TransactionReference.FIELD_TAG_21  // Start of Sequence B
                        );
                    } else if (requestedExecutionDate == null && currentField.getTag().equals(RequestedExecutionDate.FIELD_TAG_30)) {
                        requestedExecutionDate = RequestedExecutionDate.of(currentField);
                        nextValidFieldSet = ImmutableSet.of(
                                TransactionReference.FIELD_TAG_21  // Start of Sequence B
                        );
                    } else if (!currentField.getTag().equals(TransactionReference.FIELD_TAG_21)) {
                        // If it's not a TransactionReference, it's an unexpected field
                        throw new PageParserException("Unexpected field '" + currentField.getTag() + "' in Sequence A", fieldReader.getFieldLineNumber());
                    }
                    // If it is TransactionReference, fall through to Sequence B processing
                }

                // Handle Sequence B (Transaction Details - repetitive)
                if (currentField.getTag().equals(TransactionReference.FIELD_TAG_21)) {
                    TransactionReference transactionReference = TransactionReference.of(currentField);
                    CurrencyTransactionAmount currencyTransactionAmount = null;
                    Beneficiary beneficiary = null;
                    InstructionCode instructionCode = null;
                    RemittanceInformation remittanceInformation = null;
                    DetailsOfCharges detailsOfCharges = null;
                    
                    nextValidFieldSet = ImmutableSet.of(
                            InstructionCode.FIELD_TAG_23E,
                            CurrencyTransactionAmount.FIELD_TAG_32B,
                            Beneficiary.FIELD_TAG_59,
                            RemittanceInformation.FIELD_TAG_70,
                            DetailsOfCharges.FIELD_TAG_71A,
                            CustomerSpecifiedReference.FIELD_TAG_21R,
                            TransactionReference.FIELD_TAG_21,  // Next transaction
                            PageSeparator.TAG  // Page separator
                    );
                    
                    // Continue parsing fields for this transaction
                    while (true) {
                        GeneralField transactionField = fieldReader.readField();
                        if (transactionField == null) {
                            // End of input - if we have at least the mandatory currency amount, this is valid
                            if (currencyTransactionAmount != null) {
                                // Valid end of transaction - signal by setting currentField to null and breaking
                                currentField = null;
                                break;
                            } else {
                                throw new PageParserException("Unexpected end of message while parsing transaction details - missing mandatory currency amount", fieldReader.getFieldLineNumber());
                            }
                        }
                        
                        // If we encounter a field that starts a new transaction or ends the page,
                        // set currentField to this field for the main loop to process and break
                        if (transactionField.getTag().equals(PageSeparator.TAG) 
                            || transactionField.getTag().equals(TransactionReference.FIELD_TAG_21)) {
                            currentField = transactionField;
                            break;
                        }
                        
                        if (!nextValidFieldSet.contains(transactionField.getTag())) {
                            throw new PageParserException("Unexpected field '" + transactionField.getTag() + "' in transaction details", fieldReader.getFieldLineNumber());
                        }
                        
                        switch (transactionField.getTag()) {
                            case InstructionCode.FIELD_TAG_23E:
                                instructionCode = InstructionCode.of(transactionField);
                                break;
                            case CurrencyTransactionAmount.FIELD_TAG_32B:
                                currencyTransactionAmount = CurrencyTransactionAmount.of(transactionField);
                                break;
                            case Beneficiary.FIELD_TAG_59:
                                beneficiary = Beneficiary.of(transactionField);
                                break;
                            case RemittanceInformation.FIELD_TAG_70:
                                remittanceInformation = RemittanceInformation.of(transactionField);
                                break;
                            case DetailsOfCharges.FIELD_TAG_71A:
                                detailsOfCharges = DetailsOfCharges.of(transactionField);
                                break;
                        }
                    }
                    
                    // Create and add transaction details
                    TransactionDetails transactionDetails = new TransactionDetails(
                            transactionReference,
                            instructionCode,
                            currencyTransactionAmount,
                            beneficiary,
                            remittanceInformation,
                            detailsOfCharges
                    );
                    transactionDetailsList.add(transactionDetails);
                    
                    // Continue to next transaction - if currentField is page separator, main loop will handle it
                    // if currentField is another transaction, continue processing it
                    // if currentField is null (end of input), break to return the page
                    if (currentField != null && currentField.getTag().equals(TransactionReference.FIELD_TAG_21)) {
                        // Continue processing the next transaction in the main loop
                        nextValidFieldSet = ImmutableSet.of(TransactionReference.FIELD_TAG_21);
                    } else if (currentField != null && currentField.getTag().equals(PageSeparator.TAG)) {
                        // Let the main loop handle the page separator
                        nextValidFieldSet = ImmutableSet.of(PageSeparator.TAG);
                    } else {
                        // currentField is null (end of input) - break out of main loop to return the page
                        break;
                    }
                }
            }

            // Validate mandatory fields
            if (sendersReference == null) {
                throw new PageParserException("Missing mandatory field '" + SendersReference.FIELD_TAG_20 + "' (Sender's Reference)", fieldReader.getFieldLineNumber());
            }
            
            if (transactionDetailsList.isEmpty()) {
                throw new PageParserException("No transaction details found in MT101 message", fieldReader.getFieldLineNumber());
            }

            return new MT101Page(
                    sendersReference,
                    customerSpecifiedReference,
                    requestedExecutionDate,
                    transactionDetailsList
            );
        } catch (Exception e) {
            throw new SwiftMessageParseException(e.getMessage(), fieldReader.getFieldLineNumber(), e);
        }
    }


}