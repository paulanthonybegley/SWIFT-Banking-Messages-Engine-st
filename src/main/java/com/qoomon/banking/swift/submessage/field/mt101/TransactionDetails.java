package com.qoomon.banking.swift.submessage.field.mt101;

import com.google.common.base.Preconditions;
import com.qoomon.banking.swift.submessage.field.SwiftField;

import java.util.Optional;

/**
 * Transaction Details for MT101 Sequence B
 */
public class TransactionDetails {

    /**
     * @see TransactionReference#FIELD_TAG_21
     */
    private final TransactionReference transactionReference;

    /**
     * @see InstructionCode#FIELD_TAG_23E
     */
    private final Optional<InstructionCode> instructionCode;

    /**
     * @see CurrencyTransactionAmount#FIELD_TAG_32B
     */
    private final CurrencyTransactionAmount currencyTransactionAmount;

    /**
     * @see Beneficiary#FIELD_TAG_59
     */
    private final Beneficiary beneficiary;

    /**
     * @see RemittanceInformation#FIELD_TAG_70
     */
    private final Optional<RemittanceInformation> remittanceInformation;

    /**
     * @see DetailsOfCharges#FIELD_TAG_71A
     */
    private final Optional<DetailsOfCharges> detailsOfCharges;

    public TransactionDetails(
            TransactionReference transactionReference,
            InstructionCode instructionCode,
            CurrencyTransactionAmount currencyTransactionAmount,
            Beneficiary beneficiary,
            RemittanceInformation remittanceInformation,
            DetailsOfCharges detailsOfCharges) {

        Preconditions.checkArgument(transactionReference != null, "transactionReference can't be null");
        Preconditions.checkArgument(currencyTransactionAmount != null, "currencyTransactionAmount can't be null");
        Preconditions.checkArgument(beneficiary != null, "beneficiary can't be null");

        this.transactionReference = transactionReference;
        this.instructionCode = Optional.ofNullable(instructionCode);
        this.currencyTransactionAmount = currencyTransactionAmount;
        this.beneficiary = beneficiary;
        this.remittanceInformation = Optional.ofNullable(remittanceInformation);
        this.detailsOfCharges = Optional.ofNullable(detailsOfCharges);
    }

    public TransactionReference getTransactionReference() {
        return transactionReference;
    }

    public Optional<InstructionCode> getInstructionCode() {
        return instructionCode;
    }

    public CurrencyTransactionAmount getCurrencyTransactionAmount() {
        return currencyTransactionAmount;
    }

    public Beneficiary getBeneficiary() {
        return beneficiary;
    }

    public Optional<RemittanceInformation> getRemittanceInformation() {
        return remittanceInformation;
    }

    public Optional<DetailsOfCharges> getDetailsOfCharges() {
        return detailsOfCharges;
    }
}