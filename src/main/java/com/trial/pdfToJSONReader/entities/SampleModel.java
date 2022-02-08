package com.trial.pdfToJSONReader.entities;

import java.math.BigDecimal;
import java.util.List;


 // Just to mention, this is a sample model, meaning that after reading the pdf file, we can turn the output into a JSON that looks like this
public class SampleModel {

    private String title;
    private String accountNumber;
    private String metaInformation;
    private BigDecimal openingCollateral;
    private List<Transaction> transactions;
    private Transaction.Currency debitCurrency;
    private Transaction.Currency creditCurrency;
    private BigDecimal totalCredit;
    private BigDecimal totalDebit;
    private BigDecimal overallNetPosition;

    //Default constructor
    public SampleModel() {
    }

    //Getters and setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public BigDecimal getOpeningCollateral() {
        return openingCollateral;
    }

    public void setOpeningCollateral(BigDecimal openingCollateral) {
        this.openingCollateral = openingCollateral;
    }

    public String getMetaInformation() {
        return metaInformation;
    }

    public void setMetaInformation(String metaInformation) {
        this.metaInformation = metaInformation;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public Transaction.Currency getDebitCurrency() {
        return debitCurrency;
    }

    public void setDebitCurrency(Transaction.Currency debitCurrency) {
        this.debitCurrency = debitCurrency;
    }

    public Transaction.Currency getCreditCurrency() {
        return creditCurrency;
    }

    public void setCreditCurrency(Transaction.Currency creditCurrency) {
        this.creditCurrency = creditCurrency;
    }

    public BigDecimal getTotalCredit() {
        return totalCredit;
    }

    public void setTotalCredit(BigDecimal totalCredit) {
        this.totalCredit = totalCredit;
    }

    public BigDecimal getTotalDebit() {
        return totalDebit;
    }

    public void setTotalDebit(BigDecimal totalDebit) {
        this.totalDebit = totalDebit;
    }

    public BigDecimal getOverallNetPosition() {
        return overallNetPosition;
    }

    public void setOverallNetPosition(BigDecimal overallNetPosition) {
        this.overallNetPosition = overallNetPosition;
    }
}
