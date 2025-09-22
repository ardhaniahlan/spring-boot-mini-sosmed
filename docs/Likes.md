# Likes API Spec

## Like / Unlike Post

- Endpoint : POST /api/posts/{postId}/likes
- Header : Required (Authorization)
- Desc : Toggle like (kalau sudah like → unlike).

Response Body (Success) :
```json
{
  "data": {
    "post_id": 1,
    "liked": true
  }
}
```

## Get Likes by Post

- Endpoint : GET /api/posts/{postId}/likes

Response Body (Success) :

```json
{
  "data": [
    { "id": 1, "username": "ardhan" },
    { "id": 2, "username": "budi" }
  ],
  "paging": {
    "currentPage": 0,
    "totalPage": 10,
    "size": 10
  }
}
```

