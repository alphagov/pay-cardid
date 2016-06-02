# pay-cardid
GOV.UK Pay Card information service

The service provides an API that could be accessed to retrieve card information for a given card number.

## Card data
The data for this service would need to be sourced externally from relevant providers. 
The service currently supports data provided by; 

- Worldpay 
- Discover 

For the service to built run the relevant data from the supported providers would need to be placed in the `data` 
directory in csv format.

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
