# Markdown 文件管理系统

## 项目简介

这是一个用于管理 Astro 博客 Markdown 文件的全栈管理系统。系统提供了文章的添加、编辑、查看、批量上传等功能，并支持将图片上传至 OSS 存储。

## 技术栈

### 前端
- Vue 3 (Composition API)
- Vue Router 4
- Pinia (状态管理)
- Element Plus (UI组件库)
- MD-Editor-V3 (Markdown编辑器)
- Axios (HTTP客户端)
- Vite (构建工具)

### 后端
- JDK 17
- Spring Boot 3.5.9
- MyBatis-Plus 3.5.5
- MySQL 8.0
- JWT (用户认证)
- Aliyun OSS SDK (图片存储)

## 项目结构

```
astro/
├── md-manager-backend/     # 后端项目
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/mdmanager/
│   │   │   │       ├── config/         # 配置类
│   │   │   │       ├── controller/     # 控制器
│   │   │   │       ├── entity/         # 实体类
│   │   │   │       ├── mapper/         # MyBatis Mapper
│   │   │   │       ├── service/        # 服务层
│   │   │   │       ├── dto/            # 数据传输对象
│   │   │   │       ├── vo/             # 视图对象
│   │   │   │       ├── common/         # 公共类
│   │   │   │       └── utils/          # 工具类
│   │   │   └── resources/
│   │   │       ├── mapper/             # MyBatis XML映射文件
│   │   │       └── application.yml     # 配置文件
│   │   └── test/                       # 测试代码
│   └── pom.xml
├── md-manager-frontend/    # 前端项目
│   ├── src/
│   │   ├── api/            # API接口
│   │   ├── components/     # 公共组件
│   │   ├── views/          # 页面组件
│   │   ├── router/         # 路由配置
│   │   ├── store/          # 状态管理
│   │   ├── utils/          # 工具函数
│   │   ├── styles/         # 样式文件
│   │   ├── App.vue
│   │   └── main.js
│   ├── public/
│   ├── index.html
│   ├── vite.config.js
│   └── package.json
├── sql/                    # 数据库脚本
│   └── init.sql            # 初始化脚本
└── docs/                   # 项目文档
    ├── README.md           # 项目说明
    ├── API.md              # API文档
    └── DEPLOY.md           # 部署文档
```

## 功能模块

### 1. 用户认证
- 管理员登录（无注册功能）
- JWT Token 认证
- 登录状态管理

### 2. 文章管理
- 文章列表查询（支持分页、搜索）
- 新增文章
- 编辑文章
- 查看文章详情
- 删除文章

### 3. Markdown 编辑器
- 支持实时预览
- 支持代码块高亮
- 支持图片上传至 OSS
- 支持常用 Markdown 语法

### 4. 文件操作
- MD 文件生成并保存到服务器指定目录
- 批量上传现有 MD 文件
- 文件解析并保存到数据库

### 5. 标签管理
- 标签列表
- 标签选择
- 标签关联文章

## 数据库设计

### 表前缀
所有表名使用 `t_` 前缀

### 主要表
- `t_user` - 用户表
- `t_article` - 文章表
- `t_tag` - 标签表
- `t_article_tag` - 文章标签关联表

## 配置说明

### 后端配置 (application.yml)

```yaml
# 数据库配置
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/md_manager?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: root
    password: your_password

# 文件存储路径
file:
  upload-path: /path/to/your/astro/blog/src/content/blog

# OSS配置
aliyun:
  oss:
    endpoint: oss-cn-hangzhou.aliyuncs.com
    access-key-id: your_access_key_id
    access-key-secret: your_access_key_secret
    bucket-name: your_bucket_name

# JWT配置
jwt:
  secret: your_jwt_secret_key
  expiration: 86400000  # 24小时
```

### 前端配置

在 `.env` 文件中配置：
```
VITE_API_BASE_URL=http://localhost:8080/api
```

## 快速开始

### 1. 数据库初始化

```bash
# 创建数据库
mysql -u root -p < sql/init.sql
```

### 2. 启动后端

```bash
cd md-manager-backend
mvn spring-boot:run
```

### 3. 启动前端

```bash
cd md-manager-frontend
npm install
npm run dev
```

### 4. 访问系统

打开浏览器访问：http://localhost:5173

默认管理员账号：
- 用户名：admin
- 密码：admin123

## 注意事项

1. 首次使用请修改默认管理员密码
2. OSS 配置请使用自己的阿里云 OSS 信息
3. 文件上传路径请根据实际 Astro 博客目录修改
4. JWT 密钥建议使用随机生成的强密钥

## 开发说明

### 代码规范
- 所有代码添加中文注释
- 遵循阿里巴巴 Java 开发规范
- Vue 组件使用 Composition API

### 表命名规范
- 表名前缀：`t_`
- 字段名使用下划线命名法

## License

MIT License
