package com.trial.pdfToJSONReader.utils;

import com.trial.pdfToJSONReader.entities.SampleModel;
import com.trial.pdfToJSONReader.entities.Transaction;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FileReader {

    /**
     * Each line of the file passed is seperated by a new line, this means every data in the pdf actually reside in a separate line of its own,
     * hence we can leverage on this to hold every line and perform operations/in-dept extraction from them
     */
    private final static String LINE_SEPARATOR = "\n";

    /**
     * For every given line we have at hand, each word is seperated by a white space, hence we declare this constant which will be used later
     * to split a given line in order to get every word on that line one by one.
     */
    private final static String WORD_SEPARATOR = " ";

    /**
     * This is a little too much and may not mean much, but it is the list of supported currencies as defined in {@link Transaction.Currency}. <br>
     * We use these enums to ensure that we only take values of currencies we are interested in when extracting them form the file.
     */
    private final List<String> currencies = Arrays.stream(Transaction.Currency.values())
            .map(Enum::name).collect(Collectors.toList());

    /**
     * This method is responsible for reading the sample file specified by the file path passed to it.
     * @param pathToFile This is the path to the file we want to read
     * @return {@link SampleModel}. This sample model is the JSON representation we get after reading the passed file.
     */
    public SampleModel read(final String pathToFile) {

        //Declare the result object (JSON) that we will enrich with information we shall read from the pdf file
        final SampleModel sampleModel = new SampleModel();

        try {

            //Load the document, given the file path to the document of interest
            final PDDocument document = PDDocument.load(new File(pathToFile));

            //Instantiate the PDF Stripper which we shall use to read the document
            final PDFTextStripper pdfStripper = new PDFTextStripper();

            //Retrieve text from the loaded PDF document, at this point, the whole document is read and arranged like a string in multi lines
            final String text = pdfStripper.getText(document);

            //At this point we can now manipulate the text (multi-line String) to yield whatever we want
            //We first turn the multi-lines text into an array, the element of this array is therefore each of the single line
            final String[] textSplit = text.split(LINE_SEPARATOR);
            if (textSplit.length < 1 || Arrays.stream(textSplit).allMatch(line -> line.trim().isEmpty())) {
                //In case after reading the file, there are no information, just break out
                return sampleModel;
            }

            //Prepare variables like the array size, a string builder, so we can gather all the unwanted text,
            // and a container list which will hold all the individual line of transaction found in the PDF file
            final int textSplitLength = textSplit.length;
            final StringBuilder metaBuilder = new StringBuilder();
            final List<Transaction> transactions = new ArrayList<>();

            //Now we iterate through the array holding the individual lines extracted from the PDF, for each line we pick,
            //it has a unique index in the array, and with this information, we can do what we please with a line.
            /*
            For example, when we read the text from the sample document, we get the following individual organized lines:

                                                                                                           ----------- 0
                INTER-BANK SETTLEMENT ADVICE                                                               ----------- 1
                                                                                                           ----------- 2
                The Treasurer / Head of Operations                                                         ----------- 3
                First Bank Nigeria Ltd                                                                     ----------- 4
                35 Marina                                                                                  ----------- 5
                Lagos Island, Lagos                                                                        ----------- 6
                26/06/2020                                                                                 ----------- 7
                 Session 3                                                                                 ----------- 8
                Account No.4000070135 Opening Collateral:N3,390,000,000.00                                 ----------- 9
                S/N Payment Scheme SchemeType Debit (NGN) Credit (NGN)                                     ---------- 10
                1  e-Transact Card 20,000.00 -                                                             ---------- 11
                2  FMDQ Transaction Fee FMDQ 2,199,733.83 -                                                ---------- 12
                3  Interswitch Card - 4,320,410,494.37                                                     ---------- 13
                4  NAPS SETTLEMENT NAPS 112,780,422.02 -                                                   ---------- 14
                5  NIBSS e-BillsPay EFT 13,126,182.79 -                                                    ---------- 15
                6  NIBSS Instant Payment EFT 421,219,549.97 -                                              ---------- 16
                Total Debit / Credit(NGN) 549,345,888.61 4,320,410,494.37                                  ---------- 17
                OVERALL NET POSITION (NGN) 3,771,064,605.76 CR                                             ---------- 18
                Nigeria Inter-Bank Settlement System              Settlement Advice                        ---------- 19

            The above 20 lines (even though some of them are white spaces are what we get and each of these lines is what we have
            added into an array and will individually loop through, so we can now prepare them properly and produce an organized result (JSON)
             */
            for(int i = 0; i < textSplitLength; i++) {
                // step 1. we take a given line
                final String data = textSplit[i].trim();
                //Step 2. we note the index of that given line, and do a switch-case over it
                switch (i) {
                    //If the index is 0 or 2, it is a white space (empty line), there are no text there, so we don't care
                    case 0: case 2: break;
                    case 1: {
                        //If the index is one, then it is the title of the document, hence we enrich the JSON we initially declared with a title
                        sampleModel.setTitle(data);
                        break;
                    }
                    case 3: case 4: case 5: case 6: case 7: case 8: {
                        //If the index is any one of the aforementioned (3,4,5,6,7,8), we combine all of them together,
                        // we will later save this into meta information (observe that we use string builder to do this combining thing)
                        metaBuilder.append(data).append(LINE_SEPARATOR);
                        break;
                    }
                    case 9: {
                        //If the index is 9, then it contains some information like account number, and opening collateral,
                        // hence we take the line, split it using space, so we get individual word, and form a new array
                        // from the line, then we can take what ever information we want from it
                        final String[] accountAndCollateral = data.split(WORD_SEPARATOR);
                        final String accountNumber = accountAndCollateral[1].split("\\.")[1];
                        final String openingCollateralStr = (accountAndCollateral[3].split(":")[1]);
                        sampleModel.setAccountNumber(accountNumber);
                        sampleModel.setOpeningCollateral(extractMonetaryValue(openingCollateralStr));
                        break;
                    }
                    case 10: {
                        //If the index is 10, then there are information like currency that we can extract from it.
                        final List<String> currencies = Arrays.stream(data.split(WORD_SEPARATOR))
                                .map(t -> t.replace("(", "")
                                        .replace(")", "").toUpperCase())
                                .filter(this.currencies::contains).collect(Collectors.toList());
                        if (currencies.size() == 2) {
                            sampleModel.setDebitCurrency(Transaction.Currency.valueOf(currencies.get(0)));
                            sampleModel.setCreditCurrency(Transaction.Currency.valueOf(currencies.get(1)));
                        }
                        break;
                    }
                    default: {
                        if (i == textSplitLength - 1 && !data.isEmpty()) {
                            //If the index is the last line, then we have another bunch of unwanted information,
                            // we also append this to the word we were building earline full of unnecessary things
                            metaBuilder.append(data).append(LINE_SEPARATOR);
                        } else {
                            // Next we break every other line from any other indices by space, so we can hold onto a single word
                            final String[] splitData = data.split(WORD_SEPARATOR);
                            if (i == textSplitLength - 2) {
                                //If we are dealing with second to the last line, then we can take information like overall net position from it
                                final String overallNetStr = splitData[4];
                                sampleModel.setOverallNetPosition(extractMonetaryValue(overallNetStr));
                            } else if ((i == textSplitLength - 3)) {
                                //If we are dealing with the third to the last line, then we can take information like total debit and total credit from it
                                final String totalDebitStr = splitData[4];
                                final String totalCreditStr = splitData[5];
                                sampleModel.setTotalDebit(extractMonetaryValue(totalDebitStr));
                                sampleModel.setTotalCredit(extractMonetaryValue(totalCreditStr));
                            } else {
                                //If we are dealing with any other line, then it is in particular information from the table
                                // Hence we declare a transaction object (because it is in fact a transaction)
                                final Transaction transaction = new Transaction();

                                //Since we had earlier split every other line like we mentioned in line 154 by space,
                                // We can stylishly take off every piece of information we want, because we know the index it will lie
                                final String serialNumber = splitData[0];
                                final String schemeType = splitData[splitData.length - 3];
                                final String debitAmount = splitData[splitData.length - 2];
                                final String creditAmount = splitData[splitData.length - 1];
                                //This is the only weird thing (a little difficult to take off), but read it and digest it is very easy
                                final String paymentScheme = data.split(String.format("%s %s %s", schemeType,
                                        debitAmount, creditAmount))[0].split(serialNumber)[1].trim();

                                //Finally, we enrich the transaction object we declare earlier in line 168
                                transaction.setSchemeType(schemeType);
                                transaction.setPaymentScheme(paymentScheme);
                                transaction.setSerialNumber(Integer.parseInt(serialNumber));
                                transaction.setDebitAmount(extractMonetaryValue(debitAmount));
                                transaction.setCreditAmount(extractMonetaryValue(creditAmount));

                                //Then we add this line of transaction to the list of transactions
                                transactions.add(transaction);
                            }
                        }
                    }
                }
            }

            //Lastly, we add the list of transactions in the JSON model we are building
            sampleModel.setTransactions(transactions);
            sampleModel.setMetaInformation(metaBuilder.toString());

            //Closing the document
            document.close();

        } catch (Exception e) {

            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }

        return sampleModel;
    }

    /**
     * This is a method that takes a money-like string, and returns the value as a BigDecimal,
     * which is a data structure design to hold monetary value.
     */
    private BigDecimal extractMonetaryValue(final String data) {
        final String formattedData = data.replaceAll("[^0-9.]", "");
        return formattedData.isEmpty() || formattedData.isBlank() ? BigDecimal.ZERO : new BigDecimal(formattedData);
    }
}
