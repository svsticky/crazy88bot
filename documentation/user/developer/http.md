# HTTP 
The bot includes a built-in HTTP server running on port 9001.

## Routes
### `/favicon.ico` 
```
302 Temporary Redirect
Location: https://public.svsticky.nl/logos/hoofd_outline_zwart.png
```

### `/teams/list`
```
200 OK
Content-Type: application/json
```
```json
{
  "teams": [
    1,
    2
  ]
}
```

### `/submissions/list?teamId=<id>`
```
200 OK
Content-Type: application/json
```
```json
{
    "submitted": [
        1,
        2
    ]
}
```

### `/submissions?teamId=<teamId>&assignmentId=<assignmentId>`
```
200 OK
Content-Type: image/png
```
The body will contain the PNG image. A different image format can also be returned.