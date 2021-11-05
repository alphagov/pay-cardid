package uk.gov.pay.card.model;

import static java.lang.String.format;

/**
 * An object type representing a card class according to ISO CPT BIN File specification
 * Possible values in this field can be C, D, CD and F. Each one of them has a different
 * meaning, where C=Credit,D=Debit, CD=Both and F=Debit II
 * <p>
 * These values are mapping to the outside world to C, CD or D
 */
public enum CardType {
    CREDIT("C", "C", "Credit"),
    DEBIT("D", "D", "Debit"),
    CREDIT_DEBIT("CD", "CD", "Both Credit and Debit"),
    DEBIT_II("F", "D", "Debit II");

    private final String paymentGatewayRepresentation;
    private final String govUkPayRepresentation;
    private final String description;

    CardType(String paymentGatewayRepresentation, String govUkPayRepresentation, String description) {
        this.paymentGatewayRepresentation = paymentGatewayRepresentation;
        this.govUkPayRepresentation = govUkPayRepresentation;
        this.description = description;
    }

    /**
     * Used for matching the representation in different BIN files
     *
     * @return String representation from BIN file
     */
    public String getPaymentGatewayRepresentation() {
        return paymentGatewayRepresentation;
    }

    /**
     * A more descriptive name, that matches ISO CPT BIN File
     *
     * @return String for human use
     */
    public String getDescription() {
        return description;
    }

    /**
     * Used for talking to the world of GOV.UK Pay
     * Possible values: C(credit), D(debit) and CD
     *
     * @return String for internal GOV.UK Pay use
     */
    public String getGovUkPayRepresentation() {
        return govUkPayRepresentation;
    }

    /**
     * /**
     * As {@code valueOf} cannot be overridden, in this case we define a new method
     * that takes care of mapping a {@code String} to a {@code CardClass}
     *
     * @param paymentGatewayRepresentation String representing the value for the column in the BIN file.
     *                                     This value is different based on the payment gateway.
     * @return A {@code CardClass} mapped to the {@code paymentGatewayRepresentation}, or{@code null}
     * when no mapping found
     * @throws NullPointerException     when {@code paymentGatewayRepresentation} is {@code null}
     * @throws IllegalArgumentException when {@code paymentGatewayRepresentation} doesn't match any value
     */
    public static CardType of(String paymentGatewayRepresentation) {
        if (paymentGatewayRepresentation == null)
            throw new NullPointerException("Value cannot be null for paymentGatewayRepresentation");

        switch (paymentGatewayRepresentation) {
            case "C":
                return CREDIT;
            case "CD":
                return CREDIT_DEBIT;
            case "D":
            case "P":
                return DEBIT;
            case "F":
                return DEBIT_II;
        }

        throw new IllegalArgumentException(format("No enum found for value [%s]", paymentGatewayRepresentation));
    }

    @Override
    public String toString() {
        return "CardType{" +
                "paymentGatewayRepresentation='" + paymentGatewayRepresentation + '\'' +
                ", govUkPayRepresentation='" + govUkPayRepresentation + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
