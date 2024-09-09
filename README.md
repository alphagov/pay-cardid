# pay-cardid

GOV.UK Pay Card information service

The service provides an API that can be accessed to retrieve card information for a given card number.

## Environment Variables

- `ADMIN_PORT`: The port number to listen for Dropwizard admin requests on. Defaults to `8081`.
- `BIND_HOST`: The IP address for the application to bind to. Defaults to `127.0.0.1`
- `JAVA_OPTS`: Options to pass to the JRE. Defaults to `-Xms1500m -Xmx1500m`.
- `PORT`: The port number to listen for requests on. Defaults to `8080`.
- `TEST_CARD_DATA_LOCATION`: The path to load bin ranges for test cards from. Defaults
  to `classpath:/data-sources/test-cards.csv`.
- `WORLDPAY_DATA_LOCATION`: The path to load bin ranges for Worldpay from. Defaults
  to `classpath:/data-sources/worldpay-v3.csv`.
- `DISCOVER_DATA_LOCATION`: The path to load bin ranges for Discover cards from. Defaults
  to `classpath:/data-sources/discover.csv`.

## Card data

The data for this service is sourced externally from supported providers.
The service currently supports data provided by:

- Worldpay
- Discover

For the service to build and run, BIN range data from the supported providers should be available in the `data-sources`
folder:

### Worldpay

Location: `/resources/data-sources/worldpay-v3.csv`
Format: csv

The csv file should have the following structure.

    00,20240122,611649,,,,,,,,,,,,,,,,,,,,,
    01,999999999999999998,999999999999999999,CN,MASTERCARD CREDIT,APERTURE SCIENCE INC.,BRA,76,BRAZIL,C,BRL,DCC allowed,AC000,N,N,,16,D,,,,,,
    99,611648,,,,,,,,,,,,,

| Key | Meaning     |
|-----|-------------|
| 00  | Header      |
| 01  | Data record |
| 99  | EOF         |

### Discover

Location: `/resources/data-sources/discover.csv`
Format: csv

Data from Discover is in pdf format and should be converted to csv. The csv file should have the following structure.

    01,START,END,TYPE,BRAND
    02,12345678,12345679,C,DISCOVER
    09

| Key | Meaning     |
|-----|-------------|
| 01  | Header      |
| 02  | Data record |
| 09  | EOF         |

### Test Card Data

Location: `/resources/data-sources/test-cards.csv`
Format: csv

The data is a manual collection of test cards provided by our API documentation, converted into a csv format. The csv
file should have the following structure:

    01,START,END,TYPE,BRAND
    02,123456789,123456789,C,VISA
    09

| Key | Meaning     |
|-----|-------------|
| 01  | Header      |
| 02  | Data record |
| 09  | EOF         |

## API Specification

### POST /v1/api/card

Returns information for a given card number.

#### Request example

    POST /v1/api/card

    {
        "cardNumber": "1234567812345678"
    }

#### Payment response

    HTTP/1.1 200 OK
    Content-Type: application/json

    {
        "brand": "visa",
        "type": "D",
        "label": "visa"
    }

## Licence

[MIT License](LICENSE)

## Vulnerability Disclosure

GOV.UK Pay aims to stay secure for everyone. If you are a security researcher and have discovered a security
vulnerability in this code, we appreciate your help in disclosing it to us in a responsible manner. Please refer to
our [vulnerability disclosure policy](https://www.gov.uk/help/report-vulnerability) and
our [security.txt](https://vdp.cabinetoffice.gov.uk/.well-known/security.txt) file for details.
