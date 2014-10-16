# ISC Rest Usage Guide

# Message Format

The message format is following a JSON RESTful implementation. This means both the request and response is always
formatted as a JSON document.

### Request


    Request URL:http://localhost:8088/rest/service/Security%20Service/isEmailServiceAvailable
    Request Method:POST
    Content-Type:application/json; charset=UTF-8

    Form Data:
        data:{"username":"admin","password":"admin","remember":false}

All parameters submitted to the server should be stored in the form data as part of the data JSON object.

### Response
