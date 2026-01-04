# API 接口文档

## 基础信息

- 基础路径：`/api`
- 请求格式：`application/json`
- 响应格式：`application/json`
- 认证方式：JWT Token（在请求头中添加 `Authorization: Bearer {token}`）

## 统一响应格式

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {}
}
```

### 状态码说明

| 状态码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未登录或 Token 失效 |
| 403 | 无权限 |
| 500 | 服务器内部错误 |

---

## 一、用户认证模块

### 1.1 用户登录

**接口地址：** `POST /api/auth/login`

**请求参数：**

```json
{
  "username": "admin",
  "password": "admin123"
}
```

**响应示例：**

```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "userInfo": {
      "id": 1,
      "username": "admin",
      "nickname": "管理员"
    }
  }
}
```

### 1.2 获取当前用户信息

**接口地址：** `GET /api/auth/info`

**请求头：** 需要 Authorization

**响应示例：**

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "username": "admin",
    "nickname": "管理员",
    "createTime": "2024-01-01 12:00:00"
  }
}
```

### 1.3 退出登录

**接口地址：** `POST /api/auth/logout`

**请求头：** 需要 Authorization

**响应示例：**

```json
{
  "code": 200,
  "message": "退出成功",
  "data": null
}
```

---

## 二、文章管理模块

### 2.1 文章列表查询

**接口地址：** `GET /api/article/list`

**请求参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| pageNum | Integer | 否 | 页码，默认1 |
| pageSize | Integer | 否 | 每页条数，默认10 |
| keyword | String | 否 | 搜索关键词（标题） |
| tagId | Long | 否 | 标签ID筛选 |
| status | Integer | 否 | 状态筛选：0-草稿，1-已发布 |

**响应示例：**

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "total": 100,
    "list": [
      {
        "id": 1,
        "title": "文章标题",
        "fileName": "a1b2c3d4-5678-90ab-cdef.md",
        "status": 1,
        "tags": [
          {"id": 1, "name": "Vue"},
          {"id": 2, "name": "前端"}
        ],
        "createTime": "2024-01-01 12:00:00",
        "updateTime": "2024-01-02 12:00:00"
      }
    ],
    "pageNum": 1,
    "pageSize": 10
  }
}
```

### 2.2 获取文章详情

**接口地址：** `GET /api/article/{id}`

**响应示例：**

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "title": "文章标题",
    "content": "# Markdown 内容...",
    "fileName": "a1b2c3d4-5678-90ab-cdef.md",
    "status": 1,
    "tagIds": [1, 2],
    "tags": [
      {"id": 1, "name": "Vue"},
      {"id": 2, "name": "前端"}
    ],
    "createTime": "2024-01-01 12:00:00",
    "updateTime": "2024-01-02 12:00:00"
  }
}
```

### 2.3 新增文章

**接口地址：** `POST /api/article`

**请求参数：**

```json
{
  "title": "文章标题",
  "content": "# Markdown 内容...",
  "tagIds": [1, 2],
  "status": 1
}
```

**响应示例：**

```json
{
  "code": 200,
  "message": "新增成功",
  "data": {
    "id": 1,
    "fileName": "a1b2c3d4-5678-90ab-cdef.md"
  }
}
```

### 2.4 编辑文章

**接口地址：** `PUT /api/article/{id}`

**请求参数：**

```json
{
  "title": "文章标题（修改后）",
  "content": "# Markdown 内容（修改后）...",
  "tagIds": [1, 3],
  "status": 1
}
```

**响应示例：**

```json
{
  "code": 200,
  "message": "编辑成功",
  "data": null
}
```

### 2.5 删除文章

**接口地址：** `DELETE /api/article/{id}`

**响应示例：**

```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

### 2.6 批量上传MD文件

**接口地址：** `POST /api/article/batch-upload`

**请求格式：** `multipart/form-data`

**请求参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| files | File[] | 是 | MD文件数组 |

**响应示例：**

```json
{
  "code": 200,
  "message": "上传成功",
  "data": {
    "successCount": 5,
    "failCount": 0,
    "failFiles": []
  }
}
```

---

## 三、标签管理模块

### 3.1 标签列表

**接口地址：** `GET /api/tag/list`

**请求参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| keyword | String | 否 | 搜索关键词 |

**响应示例：**

```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {"id": 1, "name": "Vue", "articleCount": 10},
    {"id": 2, "name": "前端", "articleCount": 15},
    {"id": 3, "name": "JavaScript", "articleCount": 8}
  ]
}
```

### 3.2 新增标签

**接口地址：** `POST /api/tag`

**请求参数：**

```json
{
  "name": "标签名称"
}
```

**响应示例：**

```json
{
  "code": 200,
  "message": "新增成功",
  "data": {
    "id": 4,
    "name": "标签名称"
  }
}
```

### 3.3 编辑标签

**接口地址：** `PUT /api/tag/{id}`

**请求参数：**

```json
{
  "name": "标签名称（修改后）"
}
```

**响应示例：**

```json
{
  "code": 200,
  "message": "编辑成功",
  "data": null
}
```

### 3.4 删除标签

**接口地址：** `DELETE /api/tag/{id}`

**响应示例：**

```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

---

## 四、文件上传模块

### 4.1 上传图片到OSS

**接口地址：** `POST /api/upload/image`

**请求格式：** `multipart/form-data`

**请求参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| file | File | 是 | 图片文件 |

**响应示例：**

```json
{
  "code": 200,
  "message": "上传成功",
  "data": {
    "url": "https://your-bucket.oss-cn-hangzhou.aliyuncs.com/images/2024/01/xxx.png"
  }
}
```

### 4.2 批量上传图片

**接口地址：** `POST /api/upload/images`

**请求格式：** `multipart/form-data`

**请求参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| files | File[] | 是 | 图片文件数组 |

**响应示例：**

```json
{
  "code": 200,
  "message": "上传成功",
  "data": {
    "urls": [
      "https://your-bucket.oss-cn-hangzhou.aliyuncs.com/images/2024/01/xxx1.png",
      "https://your-bucket.oss-cn-hangzhou.aliyuncs.com/images/2024/01/xxx2.png"
    ]
  }
}
```

---

## 错误响应示例

### 参数错误

```json
{
  "code": 400,
  "message": "参数错误：标题不能为空",
  "data": null
}
```

### 未登录

```json
{
  "code": 401,
  "message": "未登录或登录已过期，请重新登录",
  "data": null
}
```

### 服务器错误

```json
{
  "code": 500,
  "message": "服务器内部错误",
  "data": null
}
```
