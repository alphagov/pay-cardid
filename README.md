# pay-cardid

GOV.UK Pay Card information service

The service provides an API that can be accessed to retrieve card information for a given card number.

## Environment Variables

| Variable                  | Defaut                                    | Purpose                                                     |
|---------------------------|-------------------------------------------|-------------------------------------------------------------|
| `ADMIN_PORT`              | `8081`                                    | The port number to listen for Dropwizard admin requests on. |
| `BIND_HOST`               | `127.0.0.1`                               | The IP address for the application to bind to.              |
| `JAVA_OPTS`               | `-Xms1500m -Xmx1500m`                     | Options to pass to the JRE.                                 |
| `PORT`                    | `8080`                                    | The port number to listen for requests on.                  |
| `TEST_CARD_DATA_LOCATION` | `classpath:/data-sources/test-cards.csv`  | The path to load bin ranges for test cards from.            |
| `WORLDPAY_DATA_LOCATION`  | `classpath:/data-sources/worldpay-v3.csv` | The path to load bin ranges for Worldpay from.              |
| `DISCOVER_DATA_LOCATION`  | `classpath:/data-sources/discover.csv`    | The path to load bin ranges for Discover cards from.        |

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
    01,222567000000000000,222567999999999999,CN,DEBIT MASTERCARD,UNKNOWN,NPL,524,NEPAL,D,USD,DCC allowed,DM000,N,,,,,,,,,,,
    01,222890000000000000,222895999999999999,CN,AMEX,UNKNOWN,NPL,524,NEPAL,D,USD,DCC allowed,AX000,N,,,,,,,,,,,
    01,410873333000000000,410873333999999999,CN,VISA DEBIT,UNKNOWN,USA,840,UNITED STATES,D,,DCC allowed,DE000,U,N,,,,,,,,,,
    99,611648,,,,,,,,,,,,,

| Key | Meaning     |
|-----|-------------|
| 00  | Header      |
| 01  | Data record |
| 99  | EOF         |

### Discover

Location: `/resources/data-sources/discover.csv`
Format: csv

Data from Discover is in PDF format and should be converted to csv. The csv file should have the following structure.

    01,START,END,TYPE,BRAND
    02,71231111,71241100,CD,DISCOVER
    02,71241111,71251400,CD,DISCOVER
    02,60110009,60110010,CD,DISCOVER
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
    02,22210000000,22210000000,C,MC,NOT_PREPAID,NOT_CORPORATE
    02,40000000000,40000000000,CD,VISA,NOT_PREPAID,NOT_CORPORATE
    02,40000025000,40000025000,C,VISA CREDIT,NOT_PREPAID,NOT_CORPORATE
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
        "cardNumber": "4242424242424242"
    }

#### Payment response

    HTTP/1.1 200 OK
    Content-Type: application/json

    {
        "brand": "visa",
        "type": "C",
        "label": "VISA CREDIT",
        "corporate": false,
        "prepaid": "NOT_PREPAID"
    }

## Licence

[MIT License](LICENSE)

## Vulnerability Disclosure

GOV.UK Pay aims to stay secure for everyone. If you are a security researcher and have discovered a security
vulnerability in this code, we appreciate your help in disclosing it to us in a responsible manner. Please refer to
our [vulnerability disclosure policy](https://www.gov.uk/help/report-vulnerability) and
our [security.txt](https://vdp.cabinetoffice.gov.uk/.well-known/security.txt) file for details.
