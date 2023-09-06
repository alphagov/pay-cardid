# pay-cardid

GOV.UK Pay Card information service

The service provides an API that could be accessed to retrieve card information for a given card number.

## Environment Variables

  - `ADMIN_PORT`: The port number to listen for Dropwizard admin requests on. Defaults to `8081`.
  - `DISCOVER_DATA_LOCATION`: The URL to load bin ranges for Discover cards from. Defaults to `file:///app/data/discover/Merchant_Marketing.csv`.
  - `JAVA_OPTS`: Options to pass to the JRE. Defaults to `-Xms1500m -Xmx1500m`.
  - `PORT`: The port number to listen for requests on. Defaults to `8080`.
  - `TEST_CARD_DATA_LOCATION`: The URL to load bin ranges for test cards from. Defaults to `file:///app/data/test-cards/test-card-bin-ranges.csv`.
  - `WORLDPAY_DATA_LOCATION`: The URL to load the Worldpay bin range data from. Defaults to `file:///app/data/worldpay/WP_341BIN_V03.CSV`.

## Card data
The data for this service would need to be sourced externally from relevant providers. 
The service currently supports data provided by: 

- Worldpay
- Discover

For the service to built and run the relevant data from the supported providers would need to be placed in the appropriate
location under the `data` folder as follows:

### Worldpay

Location: /data/sources/worldpay
Format: csv

The csv file is expected to have the following structure, as of Worldpay EMIS v19 document

    00!12102015!!!!!!!!!!
    05!511949000!511949999!CN!ELECTRON!SAMPLE COMP PLC!GBR!826!D!PE000!XE000!Y
    99!1!!!!!!!!!!

|Key|Meaning    |
|---|-----------|
|00 |Header     |
|05 |Data record|
|99 |EOF        |

### Discover

Location: /data/sources/discover
Format: csv

Since the data from discover is in pdf format, the services expects it to be converted into a csv format. The csv file is
 expected to have the following structure.

    01,START,END,TYPE,BRAND
    02,12345678,12345679,C,DISCOVER
    09

|Key|Meaning    |
|---|-----------|
|01 |Header     |
|02 |Data record|
|09 |EOF        |

### Test Card Data

Location: /data/sources/test-cards
Format: csv

The data is a manual collection of test cards provided by our API documentation, converted into a csv format. The csv file is
 expected to have the following structure:

    01,START,END,TYPE,BRAND
    02,123456789,123456789,C,VISA
    09

|Key|Meaning    |
|---|-----------|
|01 |Header     |
|02 |Data record|
|09 |EOF        |

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

GOV.UK Pay aims to stay secure for everyone. If you are a security researcher and have discovered a security vulnerability in this code, we appreciate your help in disclosing it to us in a responsible manner. Please refer to our [vulnerability disclosure policy](https://www.gov.uk/help/report-vulnerability) and our [security.txt](https://vdp.cabinetoffice.gov.uk/.well-known/security.txt) file for details.
