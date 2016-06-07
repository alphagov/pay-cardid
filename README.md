# pay-cardid
GOV.UK Pay Card information service

The service provides an API that could be accessed to retrieve card information for a given card number.

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
   
## API

### GET /v1/api/card/{card_number}

Returns information for a given card number.

#### Request example

```
GET /v1/api/card/1234567812345678
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
