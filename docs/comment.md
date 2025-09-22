# Comment API Spec

## Create Comment

- Endpoint : POST /api/posts/{postId}/comments
- Header : Required (Authorization)

Request Body : 
```json
{
  "body": "Komentar pertama saya"
}
```

Response Body (Success) :
```json
{
  "data": "OK"
}
```

Response Body (failed) :
```json
{
  "errors": "Body must not blank"
}
```

## Get Comments by Post

Endpoint : GET /api/posts/{postId}/comments

Response body (Success):

```json
{
  "data": [
    {
      "id": 1,
      "body": "Komentar pertama saya",
      "username": "ardhan",
      "created_at": 123456789
    }
  ],
  "paging": {
    "currentPage": 0,
    "totalPage": 10,
    "size": 10
  }
}
```

Response Body (failed) :
```json
{
  "errors": "Post not found"
}
```

## Delete Comment

- Endpoint : DELETE /api/comments/{id}
- Header : Required (Authorization)

Response Body (Success) : 
```json
{
  "data": "OK"
}
```

Response Body (Failed) :
```json
{
  "data": "Comment not found"
}
```