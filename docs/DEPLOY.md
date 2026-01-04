# 部署文档

## 环境要求

### 服务器环境
- 操作系统：Linux（推荐 CentOS 7+ 或 Ubuntu 18+）
- 内存：2GB 以上
- 磁盘：20GB 以上

### 软件环境
- JDK 17
- Node.js 18+
- MySQL 8.0
- Nginx（可选，用于反向代理）

## 一、数据库部署

### 1.1 安装 MySQL 8.0

```bash
# Ubuntu
sudo apt update
sudo apt install mysql-server

# CentOS
sudo yum install mysql-server
sudo systemctl start mysqld
```

### 1.2 创建数据库

```bash
# 登录 MySQL
mysql -u root -p

# 创建数据库
CREATE DATABASE md_manager CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 创建用户（可选）
CREATE USER 'mdmanager'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON md_manager.* TO 'mdmanager'@'localhost';
FLUSH PRIVILEGES;
```

### 1.3 初始化表结构

```bash
# 执行初始化脚本
mysql -u root -p md_manager < sql/init.sql
```

## 二、后端部署

### 2.1 编译项目

```bash
cd md-manager-backend

# 修改配置文件
vim src/main/resources/application.yml

# 打包
mvn clean package -DskipTests
```

### 2.2 配置文件修改

修改 `application.yml` 中的以下配置：

```yaml
# 数据库配置
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/md_manager?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: root
    password: 你的数据库密码

# 文件存储路径（Astro博客的content目录）
file:
  upload-path: /path/to/your/astro/blog/src/content/blog

# OSS配置
aliyun:
  oss:
    endpoint: oss-cn-hangzhou.aliyuncs.com
    access-key-id: 你的AccessKeyId
    access-key-secret: 你的AccessKeySecret
    bucket-name: 你的Bucket名称

# JWT配置
jwt:
  secret: 建议使用随机生成的32位以上字符串
  expiration: 86400000
```

### 2.3 启动服务

```bash
# 直接运行
java -jar target/md-manager-backend-1.0.0.jar

# 后台运行
nohup java -jar target/md-manager-backend-1.0.0.jar > logs/app.log 2>&1 &

# 使用 systemd 管理（推荐）
sudo vim /etc/systemd/system/md-manager.service
```

systemd 服务配置：

```ini
[Unit]
Description=MD Manager Backend
After=network.target

[Service]
Type=simple
User=root
WorkingDirectory=/opt/md-manager
ExecStart=/usr/bin/java -jar /opt/md-manager/md-manager-backend-1.0.0.jar
Restart=on-failure
RestartSec=10

[Install]
WantedBy=multi-user.target
```

```bash
# 启动服务
sudo systemctl daemon-reload
sudo systemctl start md-manager
sudo systemctl enable md-manager

# 查看状态
sudo systemctl status md-manager
```

## 三、前端部署

### 3.1 编译项目

```bash
cd md-manager-frontend

# 修改环境配置
vim .env.production
# VITE_API_BASE_URL=http://your-server-ip:8080/api

# 安装依赖
npm install

# 打包
npm run build
```

### 3.2 部署到 Nginx

```bash
# 复制静态文件
sudo cp -r dist/* /var/www/md-manager/

# 配置 Nginx
sudo vim /etc/nginx/conf.d/md-manager.conf
```

Nginx 配置：

```nginx
server {
    listen 80;
    server_name your-domain.com;  # 或服务器IP

    # 前端静态文件
    location / {
        root /var/www/md-manager;
        index index.html;
        try_files $uri $uri/ /index.html;
    }

    # API反向代理
    location /api {
        proxy_pass http://127.0.0.1:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;

        # 文件上传大小限制
        client_max_body_size 50m;
    }
}
```

```bash
# 测试配置
sudo nginx -t

# 重启 Nginx
sudo systemctl restart nginx
```

## 四、HTTPS 配置（可选）

### 4.1 使用 Let's Encrypt

```bash
# 安装 certbot
sudo apt install certbot python3-certbot-nginx

# 获取证书
sudo certbot --nginx -d your-domain.com

# 自动续期
sudo certbot renew --dry-run
```

## 五、常见问题

### 5.1 文件上传失败

1. 检查上传目录权限
```bash
chmod -R 755 /path/to/upload/directory
chown -R www-data:www-data /path/to/upload/directory
```

2. 检查 Nginx 上传大小限制
```nginx
client_max_body_size 50m;
```

### 5.2 数据库连接失败

1. 检查 MySQL 是否运行
```bash
systemctl status mysql
```

2. 检查数据库配置是否正确

3. 检查防火墙设置
```bash
sudo ufw allow 3306
```

### 5.3 OSS 上传失败

1. 检查 AccessKey 是否正确
2. 检查 Bucket 权限设置
3. 检查网络连接

### 5.4 跨域问题

后端已配置 CORS，如仍有问题，检查 Nginx 配置：

```nginx
location /api {
    add_header 'Access-Control-Allow-Origin' '*';
    add_header 'Access-Control-Allow-Methods' 'GET, POST, PUT, DELETE, OPTIONS';
    add_header 'Access-Control-Allow-Headers' 'DNT,User-Agent,X-Requested-With,If-Modified-Since,Cache-Control,Content-Type,Range,Authorization';
}
```

## 六、备份策略

### 6.1 数据库备份

```bash
# 创建备份脚本
vim /opt/backup/backup-mysql.sh
```

```bash
#!/bin/bash
DATE=$(date +%Y%m%d_%H%M%S)
BACKUP_DIR=/opt/backup/mysql
mkdir -p $BACKUP_DIR
mysqldump -u root -p'your_password' md_manager > $BACKUP_DIR/md_manager_$DATE.sql
# 保留最近7天的备份
find $BACKUP_DIR -name "*.sql" -mtime +7 -delete
```

```bash
# 添加定时任务
crontab -e
# 每天凌晨2点备份
0 2 * * * /opt/backup/backup-mysql.sh
```

### 6.2 文件备份

```bash
# MD文件备份
rsync -avz /path/to/md/files/ /opt/backup/md-files/
```

## 七、监控与日志

### 7.1 查看应用日志

```bash
# 查看实时日志
tail -f /opt/md-manager/logs/app.log

# 查看错误日志
grep ERROR /opt/md-manager/logs/app.log
```

### 7.2 查看 Nginx 日志

```bash
tail -f /var/log/nginx/access.log
tail -f /var/log/nginx/error.log
```

## 八、安全建议

1. 修改默认管理员密码
2. 使用强密码保护数据库
3. 配置防火墙只开放必要端口
4. 启用 HTTPS
5. 定期更新系统和依赖
6. 定期备份数据
