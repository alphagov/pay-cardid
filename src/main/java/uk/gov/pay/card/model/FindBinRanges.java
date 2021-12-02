package uk.gov.pay.card.model;

import uk.gov.pay.card.db.RangeSetCardInformationStore;
import uk.gov.pay.card.db.loader.BinRangeDataLoader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Optional;

import static java.lang.String.format;
import static java.util.Collections.singletonList;

public class FindBinRanges {

    public static void main(String args[]) throws Exception {
        File file = new File("data/sources/worldpay/WP_341BIN_V03.CSV");

        URL url = file.toURI().toURL();
        BinRangeDataLoader worldpayBinRangeLoader = BinRangeDataLoader.BinRangeDataLoaderFactory.worldpay(url);
        RangeSetCardInformationStore cardInformationStore = new RangeSetCardInformationStore(singletonList(worldpayBinRangeLoader));
        cardInformationStore.initialiseCardInformation();

        File payments = new File("data/sources/worldpay/data.csv");
        BufferedReader reader = new BufferedReader(new FileReader(payments));

        String line;
        int counter = 0;

        File output = createOrGetFile();
        PrintWriter printWriter = new PrintWriter(output.getName());
        printWriter.println("gateway_account_id,amount,lower_bin_range,upper_bin_range,corporate_card,issuer_name,issuer_country_code,issuer_country_name,card_type,prepaid_status,card_brand,wallet, payment_status,external_id");

        int walletPayments = 0;
        int nomatchingrange = 0;
        int matchingwithcardid = 0;

        while ((line = reader.readLine()) != null) {
            if (counter == 0) {
                counter++;
                continue;
            }
            String[] column = line.split(",");

            if (column[2] != null && !column[2].isEmpty()) {
                Long number = Long.valueOf(column[2]);

                Long formatted = Long.valueOf(format("%-" + 11 + "d", number).replace(" ", "0"));
                Optional<CardInformation> mayBeCardInfo = cardInformationStore.find(formatted.toString());

                if(mayBeCardInfo.isEmpty()){
                    formatted = Long.valueOf(format("%-" + 11 + "d", number).replace(" ", "9"));
                    mayBeCardInfo = cardInformationStore.find(formatted.toString());
                }

                if (mayBeCardInfo.isPresent()) {
                    CardInformation cardInformation = mayBeCardInfo.get();

                    writeToFile(printWriter,
                            column[0], column[1],
                            cardInformation.getMin().toString(), cardInformation.getMax().toString(),
                            Boolean.toString(cardInformation.isCorporate()),
                            cardInformation.getIssuerName(),
                            cardInformation.getIssuerCountryCode(),
                            cardInformation.getIssuerCountryName(),
                            cardInformation.getCardType().getDescription(),
                            cardInformation.getPrepaidStatus().toString(),
                            cardInformation.getBrand(), column[3], column[4],
                            column[5]
                    );
                    matchingwithcardid++;
                } else {
                    writeToFile(printWriter,
                            column[0], column[1], formatted.toString(), formatted.toString(),
                            "", "", "",
                            "", "", "", "", column[3], column[4], column[5]
                    );
                    nomatchingrange++;
                }
            } else {
                writeToFile(printWriter,
                        column[0], column[1], "", "",
                        "", "", "",
                        "", "", "", "", column[3], column[4], column[5]
                );
                walletPayments++;
            }
        }

        printWriter.close();
        reader.close();
    }

    private static void writeToFile(PrintWriter writer, String gatewayAccountId, String amount,
                                    String lowerBinRange, String upperBinRange, String isCorporateCard,
                                    String issuerName, String issuerCountryCode, String issuerCountryName,
                                    String cardType, String prepaidStatus, String brand, String wallet,
                                    String paymentStatus, String externalId) {
        String[] data = {
                gatewayAccountId, amount,
                lowerBinRange, upperBinRange,
                isCorporateCard,
                issuerName, issuerCountryCode, issuerCountryName,
                cardType, prepaidStatus, brand, wallet, paymentStatus, externalId
        };
        String outputRow = String.join(",", data);
        writer.println(outputRow);
    }

    public static File createOrGetFile() throws IOException {
        File outpurFile = new File("output.csv");
        if (outpurFile.createNewFile()) {
            System.out.println("File created: " + outpurFile.getName());
        } else {
            System.out.println("File already exists.");
        }
        return outpurFile;
    }
}
