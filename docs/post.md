# Post API Spec

## Create Post
- Endpoint : POST /api/posts
- Header : Required (Authorization)
- Desc : Membuat post baru

Request Body :
```json
{
  "body": "Ini post tentang Spring Boot",
  "image_url": "http://example.com/img.png",
  "status": "published"
}
```

Response Body (Success) :
```json
{
  "data": "OK"
}
```

Response Body (Failed) :
```json
{
  "errors": "field must not blank"
}
```

## Get All Post

- Endpoint : GET /api/posts
- Desc : Mengambil semua post (published).

Response Body : 
```json
{
  "data": [
    {
      "id": 1,
      "body": "Ini post tentang Spring Boot",
      "image_url": null,
      "status": "published",
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

## Get Post by Id
- Endpoint : GET /api/posts/{postId}
- Desc : Mendapatkan detail post

Response body (Success) :
```json
{
  "data": {
    "id": 1,
    "body": "Ini post tentang Spring Boot",
    "image_url": null,
    "status": "published",
    "username": "ardhan",
    "created_at": 123456789
  }
}
```

## Get Post by userId
- Endpoint : GET /api/users/{userId}/posts
- Header : Required (Authorization)
- Desc : Mengambil semua post (published).

Response Body :
```json
{
  "data": [
    {
      "id": 1,
      "body": "Ini post tentang Spring Boot",
      "image_url": null,
      "status": "published",
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

## Update Post 

- Endpoint : PATCH /api/posts/{id}
- Header : Required (Authorization)

Request Body :
```json
{
  "body": "Update isi post",
  "status": "draft"
}
```

Response Body (Success):
```json
{
  "data": "OK"
}
```

Response Body (Failed):
```json
{
  "errors": "Unauthorize"
}
```

## Delete Post

- Endpoint : DELETE /api/posts/{postId}
- Header : Required (Authorization)

Response Body (Success):
```json
{
  "data": "OK"
}
```

Response Body (Failed):
```json
{
  "errors": "Post not found"
}
```