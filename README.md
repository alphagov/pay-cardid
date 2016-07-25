# pay-cardid
GOV.UK Pay Card information service

The service provides an API that could be accessed to retrieve card information for a given card number.

## Environment Variables

 - `WORLDPAY_DATA_LOCATION`: Variable to override the default path for the card bin range data provided by Worldpay.
 - `DISCOVER_DATA_LOCATION`: Variable to override the default path for the card bin range data provided by Discover.
 - `TEST_CARD_DATA_LOCATION`: Variable to override the default path for the test card bin range data.

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

* The csv file is expected to have the following structure, as of WordlPay EMIS v19 document

 ```
 00!12102015!!!!!!!!!!
 05!511949000!511949999!CN!ELECTRON!SAMPLE COMP PLC!GBR!826!D!PE000!XE000!Y
 99!1!!!!!!!!!!
 ```
 
 * Legend:
     * `00` - header
     * `05` - data record 
     * `99` - EOF

### Discover

Location: /data/sources/discover
Format: csv

* Since the data from discover is in pdf format, the services expects it to be converted into a csv format. The csv file is
 expected to have the following structure.

 ```
 01,START,END,TYPE,BRAND
 02,12345678,12345679,C,DISCOVER
 09
 ```
 * Legend:
     * `01` - header
     * `02` - data record
     * `09` - EOF
   
### Test Card Data

Location: /data/sources/test-cards
Format: csv

* The data is a manual collection of test cards provided by our API documentation, converted into a csv format. The csv file is
 expected to have the following structure.

 ```
 01,START,END,TYPE,BRAND
 02,123456789,123456789,C,VISA
 09
 ```
 * Legend:
     * `01` - header
     * `02` - data record
     * `09` - EOF
   
## API

### POST /v1/api/card

Returns information for a given card number.

#### Request example

```
POST /v1/api/card
{
    "cardNumber": "1234567812345678"
}
```

#### Payment response

```
HTTP/1.1 200 OK
Content-Type: application/json

{
    "brand": "visa",
    "type": "D",
    "label": "visa"
}
```
